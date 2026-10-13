<template>
  <div class="table-box p-5">
    <vxe-grid ref="xGrid" v-bind="gridOptions">
      <template #toolbar_buttons>
        <el-button type="primary" :icon="Plus" @click="handleAdd">新增规则</el-button>
        <el-button type="danger" :icon="Delete" @click="handleBatchDelete">批量删除</el-button>
        <el-button :icon="Refresh" @click="handleRefresh">刷新</el-button>
      </template>

      <template #eventType="{ row }">
        <el-tag type="primary">{{ eventLabel(row.eventType) }}</el-tag>
      </template>

      <template #points="{ row }">
        <span :class="Number(row.points) >= 0 ? 'text-green-500' : 'text-red-500'" class="font-medium">
          {{ Number(row.points) >= 0 ? "+" : "" }}{{ row.points ?? 0 }}
        </span>
      </template>

      <template #dailyLimit="{ row }">
        <span>{{ row.dailyLimit ? row.dailyLimit : "不限" }}</span>
      </template>

      <!-- 后端：0正常 1停用 -->
      <template #status="{ row }">
        <el-tag :type="String(row.status) === '0' ? 'success' : 'info'">
          {{ String(row.status) === '0' ? '正常' : '停用' }}
        </el-tag>
      </template>

      <template #action="{ row }">
        <el-button type="primary" link :icon="Edit" @click="handleEdit(row)">编辑</el-button>
        <el-button type="danger" link :icon="Delete" @click="handleDelete(row)">删除</el-button>
      </template>
    </vxe-grid>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑积分规则' : '新增积分规则'" width="640px" destroy-on-close>
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="110px">
        <el-form-item label="规则名称" prop="ruleName">
          <el-input v-model="formData.ruleName" placeholder="如：每日签到奖励" />
        </el-form-item>
        <el-form-item label="规则编码" prop="ruleCode">
          <el-input v-model="formData.ruleCode" placeholder="如：daily_sign" />
        </el-form-item>
        <el-form-item label="事件类型" prop="eventType">
          <el-select v-model="formData.eventType" filterable placeholder="请选择事件类型" style="width: 100%">
            <el-option v-for="item in eventOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="积分值" prop="points">
          <el-input-number v-model="formData.points" :min="-100000" :max="100000" style="width: 100%" />
        </el-form-item>
        <el-form-item label="每日上限">
          <el-input-number v-model="formData.dailyLimit" :min="0" style="width: 100%" placeholder="0 表示不限" />
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
  </div>
</template>

<script setup name="memberPointsRule">
import { ref, reactive, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { Plus, Refresh, Edit, Delete } from "@element-plus/icons-vue";
import {
  getPointsRulePageApi,
  getPointsRuleEventsApi,
  createPointsRuleApi,
  updatePointsRuleApi,
  deletePointsRuleApi
} from "@/api/modules/member";

const xGrid = ref();
const formRef = ref();
const dialogVisible = ref(false);
const isEdit = ref(false);
const submitLoading = ref(false);

const defaultEventOptions = [
  { value: "sign", label: "签到" },
  { value: "train", label: "训练" },
  { value: "order", label: "订单" },
  { value: "review", label: "评价" }
];
const eventOptions = ref([...defaultEventOptions]);

const defaultForm = {
  ruleId: null,
  ruleName: "",
  ruleCode: "",
  eventType: "",
  points: 0,
  dailyLimit: 0,
  status: "0"
};

const formData = reactive({ ...defaultForm });

const formRules = {
  ruleName: [{ required: true, message: "请输入规则名称", trigger: "blur" }],
  ruleCode: [{ required: true, message: "请输入规则编码", trigger: "blur" }],
  eventType: [{ required: true, message: "请选择事件类型", trigger: "change" }],
  points: [{ required: true, message: "请输入积分值", trigger: "blur" }]
};

const eventLabel = value => {
  const hit = eventOptions.value.find(i => i.value === value);
  return hit ? hit.label : value || "-";
};

const gridOptions = reactive({
  border: true,
  showOverflow: true,
  height: 600,
  loading: false,
  rowConfig: { keyField: "ruleId", isHover: true },
  columnConfig: { resizable: true },
  checkboxConfig: { highlight: true },
  toolbarConfig: {
    slots: { buttons: "toolbar_buttons" },
    refresh: true,
    zoom: false,
    export: true,
    custom: true
  },
  exportConfig: {
    types: ["xlsx", "csv", "pdf", "html", "xml", "txt"]
  },
  formConfig: {
    items: [
      {
        field: "ruleName",
        title: "规则名称",
        span: 6,
        itemRender: { name: "$input", props: { placeholder: "可选", clearable: true } }
      },
      {
        field: "eventType",
        title: "事件类型",
        span: 6,
        itemRender: {
          name: "$select",
          options: [
            { label: "全部", value: "" },
            { label: "签到", value: "sign" },
            { label: "训练", value: "train" },
            { label: "订单", value: "order" },
            { label: "评价", value: "review" }
          ],
          props: { placeholder: "全部", clearable: true }
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
          props: { placeholder: "全部", clearable: true }
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
  pagerConfig: {
    enabled: true,
    pageSize: 10,
    pageSizes: [10, 20, 50]
  },
  proxyConfig: {
    seq: true,
    form: true,
    response: { result: "list", total: "total" },
    ajax: {
      query: async ({ page, form }) => {
        try {
          const res = await getPointsRulePageApi({
            pageNum: page.currentPage,
            pageSize: page.pageSize,
            ruleName: form.ruleName || undefined,
            eventType: form.eventType || undefined,
            status: form.status === "" || form.status == null ? undefined : String(form.status)
          });
          const pageData = res.data || {};
          return { list: pageData.records || [], total: pageData.total || 0 };
        } catch (e) {
          console.error(e);
          return { list: [], total: 0 };
        }
      }
    }
  },
  columns: [
    { type: "checkbox", width: 50 },
    { type: "seq", title: "序号", width: 60 },
    { field: "ruleName", title: "规则名称", minWidth: 160 },
    { field: "ruleCode", title: "规则编码", minWidth: 150 },
    { field: "eventType", title: "事件类型", width: 150, slots: { default: "eventType" } },
    { field: "points", title: "积分值", width: 100, slots: { default: "points" } },
    { field: "dailyLimit", title: "每日上限", width: 110, slots: { default: "dailyLimit" } },
    { field: "status", title: "状态", width: 90, slots: { default: "status" } },
    { title: "操作", width: 150, fixed: "right", slots: { default: "action" } }
  ]
});

const handleRefresh = () => {
  xGrid.value?.commitProxy("query");
};

const loadEventOptions = async () => {
  try {
    const res = await getPointsRuleEventsApi();
    eventOptions.value = Array.isArray(res?.data) && res.data.length ? res.data : [...defaultEventOptions];
  } catch (e) {
    console.error(e);
    eventOptions.value = [...defaultEventOptions];
  }
};

const handleAdd = () => {
  isEdit.value = false;
  Object.assign(formData, { ...defaultForm });
  dialogVisible.value = true;
};

const handleEdit = row => {
  isEdit.value = true;
  Object.assign(formData, {
    ruleId: row.ruleId,
    ruleName: row.ruleName || "",
    ruleCode: row.ruleCode || "",
    eventType: row.eventType || "",
    points: row.points ?? 0,
    dailyLimit: row.dailyLimit ?? 0,
    status: row.status != null ? String(row.status) : "0"
  });
  dialogVisible.value = true;
};

const handleSubmit = async () => {
  try {
    await formRef.value.validate();
    submitLoading.value = true;
    const payload = {
      ruleName: formData.ruleName,
      ruleCode: formData.ruleCode,
      eventType: formData.eventType,
      points: formData.points,
      dailyLimit: formData.dailyLimit,
      status: String(formData.status)
    };
    if (isEdit.value) {
      await updatePointsRuleApi({ ...payload, ruleId: formData.ruleId });
      ElMessage.success("更新成功");
    } else {
      await createPointsRuleApi(payload);
      ElMessage.success("创建成功");
    }
    dialogVisible.value = false;
    handleRefresh();
  } finally {
    submitLoading.value = false;
  }
};

const handleDelete = row => {
  ElMessageBox.confirm(`确定删除规则「${row.ruleName}」吗？`, "提示", { type: "warning" })
    .then(async () => {
      await deletePointsRuleApi(row.ruleId);
      ElMessage.success("已删除");
      handleRefresh();
    })
    .catch(() => {});
};

const handleBatchDelete = () => {
  const records = xGrid.value?.getCheckboxRecords() || [];
  if (records.length === 0) {
    ElMessage.warning("请选择要删除的数据");
    return;
  }
  const ids = records.map(r => r.ruleId).join(",");
  ElMessageBox.confirm(`确定删除选中的 ${records.length} 条规则吗？`, "提示", { type: "warning" })
    .then(async () => {
      await deletePointsRuleApi(ids);
      ElMessage.success("已删除");
      handleRefresh();
    })
    .catch(() => {});
};

onMounted(() => {
  loadEventOptions();
});
</script>

<style scoped>
.table-box {
  background: #fff;
  border-radius: 8px;
}
</style>
