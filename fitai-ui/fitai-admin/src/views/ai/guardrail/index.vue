<template>
  <div class="table-box p-5">
    <vxe-grid ref="xGrid" v-bind="gridOptions">
      <template #toolbar_buttons>
        <el-button type="primary" :icon="Plus" @click="handleAdd">新增规则</el-button>
        <el-button type="success" :icon="MagicStick" @click="openCheck">试运行</el-button>
        <el-button :icon="Refresh" @click="handleRefresh">刷新</el-button>
      </template>
      <template #ruleType="{ row }">
        <el-tag size="small">{{ typeLabel(row.ruleType) }}</el-tag>
      </template>
      <template #action_col="{ row }">
        <el-tag size="small" :type="actionTag(row.action)">{{ actionLabel(row.action) }}</el-tag>
      </template>
      <template #status="{ row }">
        <el-tag v-if="row.status === '0'" type="success">正常</el-tag>
        <el-tag v-else type="info">停用</el-tag>
      </template>
      <template #action="{ row }">
        <el-button type="primary" link :icon="Edit" @click="handleEdit(row)">编辑</el-button>
        <el-button
          link
          :type="row.status === '0' ? 'warning' : 'success'"
          @click="handleToggleStatus(row)"
        >{{ row.status === "0" ? "停用" : "启用" }}</el-button>
        <el-button type="danger" link :icon="Delete" @click="handleDelete(row)">删除</el-button>
      </template>
    </vxe-grid>

    <!-- 新增/编辑 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑规则' : '新增规则'" width="600px" destroy-on-close>
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
        <el-form-item label="规则名称" prop="ruleName">
          <el-input v-model="formData.ruleName" placeholder="规则名称" />
        </el-form-item>
        <el-form-item label="类型" prop="ruleType">
          <el-select v-model="formData.ruleType" placeholder="请选择" style="width: 100%">
            <el-option label="关键词（keyword）" value="keyword" />
            <el-option label="正则（regex）" value="regex" />
            <el-option label="长度限制（length）" value="length" />
            <el-option label="医疗提示（medical）" value="medical" />
            <el-option label="免责声明（disclaimer）" value="disclaimer" />
          </el-select>
        </el-form-item>
        <el-form-item label="规则内容" prop="ruleContent">
          <el-input v-model="formData.ruleContent" type="textarea" :rows="3" :placeholder="contentTip" />
        </el-form-item>
        <el-form-item label="动作">
          <el-select v-model="formData.action" style="width: 100%">
            <el-option label="拦截拒答（block）" value="block" />
            <el-option label="仅告警（warn）" value="warn" />
            <el-option label="打码替换（replace）" value="replace" />
          </el-select>
        </el-form-item>
        <el-form-item label="优先级">
          <el-input-number v-model="formData.priority" :min="0" style="width: 140px" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="formData.status">
            <el-radio value="0">正常</el-radio>
            <el-radio value="1">停用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 试运行 -->
    <el-dialog v-model="checkVisible" title="护栏试运行" width="600px" destroy-on-close>
      <el-form label-width="90px">
        <el-form-item label="场景">
          <el-radio-group v-model="checkForm.scene">
            <el-radio value="input">输入侧（input）</el-radio>
            <el-radio value="output">输出侧（output）</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="待检测文本">
          <el-input v-model="checkForm.text" type="textarea" :rows="4" placeholder="输入待检测文本" />
        </el-form-item>
      </el-form>
      <div class="mb-3">
        <el-button type="primary" :loading="checkLoading" :icon="MagicStick" @click="doCheck">运行检测</el-button>
      </div>
      <template v-if="checkResult">
        <el-descriptions :column="1" border size="small">
          <el-descriptions-item label="是否拦截">
            <el-tag :type="checkResult.blocked ? 'danger' : 'success'">{{ checkResult.blocked ? "已拦截" : "通过" }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="命中规则">
            <span v-if="checkResult.hitRules && checkResult.hitRules.length">{{ checkResult.hitRules.join("、") }}</span>
            <span v-else>无</span>
          </el-descriptions-item>
          <el-descriptions-item label="处理后文本">
            <div class="result-text">{{ checkResult.resultText }}</div>
          </el-descriptions-item>
        </el-descriptions>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="aiGuardrail">
import { ref, reactive, computed } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { Plus, Refresh, Edit, Delete, MagicStick } from "@element-plus/icons-vue";
import {
  getGuardrailPageApi,
  getGuardrailDetailApi,
  addGuardrailApi,
  updateGuardrailApi,
  changeGuardrailStatusApi,
  deleteGuardrailApi,
  checkGuardrailApi
} from "@/api/modules/ai-agent";

const xGrid = ref();
const formRef = ref();
const dialogVisible = ref(false);
const isEdit = ref(false);
const submitLoading = ref(false);

const TYPE_LABELS = { keyword: "关键词", regex: "正则", length: "长度限制", medical: "医疗提示", disclaimer: "免责声明" };
const ACTION_LABELS = { block: "拦截", warn: "告警", replace: "打码" };
const typeLabel = t => TYPE_LABELS[t] || t;
const actionLabel = a => ACTION_LABELS[a] || a;
const actionTag = a => (a === "block" ? "danger" : a === "warn" ? "warning" : "info");

const formData = reactive({
  guardrailId: null,
  ruleName: "",
  ruleType: "keyword",
  ruleContent: "",
  action: "block",
  priority: 0,
  status: "0"
});

const formRules = {
  ruleName: [{ required: true, message: "请输入规则名称", trigger: "blur" }],
  ruleType: [{ required: true, message: "请选择类型", trigger: "change" }],
  ruleContent: [{ required: true, message: "请输入规则内容", trigger: "blur" }]
};

const contentTip = computed(() => {
  switch (formData.ruleType) {
    case "keyword":
      return "关键词表，逗号/分号/换行分隔";
    case "regex":
      return "正则表达式";
    case "length":
      return "最大字符数（数值）";
    default:
      return "输出侧追加的声明文案";
  }
});

const gridOptions = reactive({
  border: true,
  showOverflow: true,
  height: 600,
  loading: false,
  rowConfig: { keyField: "guardrailId" },
  toolbarConfig: { slots: { buttons: "toolbar_buttons" }, refresh: true, zoom: false },
  proxyConfig: {
    form: true,
    response: { result: "list", total: "total" },
    ajax: {
      query: async ({ page, form }) => {
        const params = { pageNum: page.currentPage, pageSize: page.pageSize, ...form };
        try {
          const res = await getGuardrailPageApi(params);
          const data = res.data || {};
          return { list: data.records || [], total: data.total || 0 };
        } catch (e) {
          return { list: [], total: 0 };
        }
      }
    }
  },
  formConfig: {
    items: [
      { field: "keyword", title: "关键词", span: 6, itemRender: { name: "$input", props: { placeholder: "规则名称" } } },
      {
        field: "ruleType",
        title: "类型",
        span: 6,
        itemRender: {
          name: "$select",
          options: [
            { label: "全部", value: "" },
            { label: "关键词", value: "keyword" },
            { label: "正则", value: "regex" },
            { label: "长度限制", value: "length" },
            { label: "医疗提示", value: "medical" },
            { label: "免责声明", value: "disclaimer" }
          ],
          props: { placeholder: "请选择" }
        }
      },
      {
        field: "status",
        title: "状态",
        span: 6,
        itemRender: {
          name: "$select",
          options: [
            { label: "全部", value: "" },
            { label: "正常", value: "0" },
            { label: "停用", value: "1" }
          ],
          props: { placeholder: "请选择" }
        }
      },
      {
        span: 6,
        align: "center",
        itemRender: {
          name: "$buttons",
          children: [
            { props: { type: "submit", content: "搜索", status: "primary" } },
            { props: { type: "reset", content: "重置" } }
          ]
        }
      }
    ]
  },
  columns: [
    { type: "seq", width: 60 },
    { field: "ruleName", title: "规则名称", minWidth: 140 },
    { field: "ruleType", title: "类型", width: 110, slots: { default: "ruleType" } },
    { field: "ruleContent", title: "规则内容", minWidth: 180 },
    { field: "action", title: "动作", width: 90, slots: { default: "action_col" } },
    { field: "priority", title: "优先级", width: 80 },
    { field: "status", title: "状态", width: 90, slots: { default: "status" } },
    { title: "操作", width: 200, fixed: "right", slots: { default: "action" } }
  ]
});

const handleRefresh = () => xGrid.value?.commitProxy("query");

const resetForm = () => {
  Object.assign(formData, {
    guardrailId: null,
    ruleName: "",
    ruleType: "keyword",
    ruleContent: "",
    action: "block",
    priority: 0,
    status: "0"
  });
};

const handleAdd = () => {
  isEdit.value = false;
  resetForm();
  dialogVisible.value = true;
};

const handleEdit = async row => {
  isEdit.value = true;
  resetForm();
  try {
    const res = await getGuardrailDetailApi(row.guardrailId);
    const d = res.data || {};
    Object.assign(formData, {
      guardrailId: d.guardrailId,
      ruleName: d.ruleName || "",
      ruleType: d.ruleType || "keyword",
      ruleContent: d.ruleContent || "",
      action: d.action || "block",
      priority: d.priority ?? 0,
      status: d.status || "0"
    });
  } catch (e) {
    ElMessage.error("获取详情失败");
    return;
  }
  dialogVisible.value = true;
};

const handleSubmit = async () => {
  try {
    await formRef.value.validate();
    submitLoading.value = true;
    const payload = {
      ruleName: formData.ruleName,
      ruleType: formData.ruleType,
      ruleContent: formData.ruleContent,
      action: formData.action,
      priority: formData.priority ?? 0,
      status: formData.status
    };
    if (isEdit.value) await updateGuardrailApi(formData.guardrailId, payload);
    else await addGuardrailApi(payload);
    ElMessage.success(isEdit.value ? "编辑成功" : "新增成功");
    dialogVisible.value = false;
    handleRefresh();
  } catch (e) {
    if (e !== false) ElMessage.error("操作失败");
  } finally {
    submitLoading.value = false;
  }
};

const handleToggleStatus = row => {
  const target = row.status === "0" ? "1" : "0";
  changeGuardrailStatusApi(row.guardrailId, target)
    .then(() => {
      ElMessage.success("操作成功");
      handleRefresh();
    })
    .catch(() => ElMessage.error("操作失败"));
};

const handleDelete = row => {
  ElMessageBox.confirm(`确定删除规则「${row.ruleName}」？`, "提示", { type: "warning" }).then(async () => {
    try {
      await deleteGuardrailApi(row.guardrailId);
      ElMessage.success("删除成功");
      handleRefresh();
    } catch (e) {
      ElMessage.error("删除失败");
    }
  });
};

// ===== 试运行 =====
const checkVisible = ref(false);
const checkLoading = ref(false);
const checkResult = ref(null);
const checkForm = reactive({ scene: "input", text: "" });

const openCheck = () => {
  Object.assign(checkForm, { scene: "input", text: "" });
  checkResult.value = null;
  checkVisible.value = true;
};

const doCheck = async () => {
  if (!checkForm.text) return ElMessage.warning("请输入待检测文本");
  checkLoading.value = true;
  try {
    const res = await checkGuardrailApi({ scene: checkForm.scene, text: checkForm.text });
    checkResult.value = res.data || null;
  } catch (e) {
    ElMessage.error(e?.msg || e?.message || "检测失败");
  } finally {
    checkLoading.value = false;
  }
};
</script>

<style scoped>
.table-box {
  background: #fff;
  border-radius: 8px;
}
.result-text {
  white-space: pre-wrap;
  line-height: 1.6;
}
.mb-3 {
  margin-bottom: 12px;
}
</style>
