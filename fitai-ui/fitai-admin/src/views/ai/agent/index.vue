<template>
  <div class="table-box p-5">
    <vxe-grid ref="xGrid" v-bind="gridOptions">
      <template #toolbar_buttons>
        <el-button type="primary" :icon="Plus" @click="handleAdd">新增 Agent</el-button>
        <el-button :icon="Refresh" @click="handleRefresh">刷新</el-button>
      </template>
      <template #model="{ row }">
        <span>{{ modelName(row.modelId) }}</span>
      </template>
      <template #status="{ row }">
        <el-tag v-if="row.status === '0'" type="success">正常</el-tag>
        <el-tag v-else type="info">停用</el-tag>
      </template>
      <template #action="{ row }">
        <el-button type="success" link :icon="VideoPlay" @click="handleRun(row)">试运行</el-button>
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
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑 Agent' : '新增 Agent'" width="680px" destroy-on-close>
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="110px">
        <el-form-item label="名称" prop="agentName">
          <el-input v-model="formData.agentName" placeholder="Agent 名称" maxlength="64" />
        </el-form-item>
        <el-form-item label="编码" prop="agentCode">
          <el-input v-model="formData.agentCode" placeholder="唯一编码，如 fitness_coach" maxlength="64" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="formData.description" type="textarea" :rows="2" placeholder="选填" maxlength="256" />
        </el-form-item>
        <el-form-item label="主模型" prop="modelId">
          <el-select v-model="formData.modelId" placeholder="请选择模型" filterable style="width: 100%">
            <el-option v-for="m in modelOptions" :key="m.id" :label="`${m.name}（${m.modelKey}）`" :value="m.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="提示词">
          <el-select v-model="formData.promptId" placeholder="选填，系统提示词" filterable clearable style="width: 100%">
            <el-option v-for="p in promptOptions" :key="p.id" :label="p.name" :value="p.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="绑定工具">
          <el-select v-model="selectedToolIds" placeholder="选填，可多选" multiple filterable style="width: 100%">
            <el-option v-for="t in toolOptions" :key="t.id" :label="`${t.name}（${t.code}）`" :value="t.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="编排流程">
          <el-input v-model="formData.flowJson" type="textarea" :rows="4" placeholder="流程 JSON（nodes/edges），留空为单次 LLM 调用" />
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
    <el-dialog v-model="runVisible" title="Agent 试运行" width="720px" destroy-on-close>
      <el-form label-width="80px">
        <el-form-item label="输入">
          <el-input v-model="runInput" type="textarea" :rows="3" placeholder="输入测试问题（可留空）" />
        </el-form-item>
      </el-form>
      <div class="mb-3">
        <el-button type="primary" :loading="runLoading" :icon="VideoPlay" @click="doRun">开始运行</el-button>
      </div>
      <template v-if="runResult">
        <el-descriptions :column="2" border size="small">
          <el-descriptions-item label="模型">{{ runResult.modelName || "-" }}</el-descriptions-item>
          <el-descriptions-item label="总 Token">{{ runResult.totalTokens ?? "-" }}</el-descriptions-item>
          <el-descriptions-item label="提示 Token">{{ runResult.promptTokens ?? "-" }}</el-descriptions-item>
          <el-descriptions-item label="补全 Token">{{ runResult.completionTokens ?? "-" }}</el-descriptions-item>
        </el-descriptions>
        <div class="run-block">
          <div class="run-title">执行轨迹</div>
          <el-table :data="runResult.steps || []" size="small" border>
            <el-table-column type="index" width="50" />
            <el-table-column prop="step" label="节点" width="120" />
            <el-table-column prop="type" label="类型" width="100" />
            <el-table-column prop="output" label="输出摘要" show-overflow-tooltip />
          </el-table>
        </div>
        <div class="run-block">
          <div class="run-title">最终回答</div>
          <div class="run-answer">{{ runResult.finalAnswer || "-" }}</div>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="aiAgent">
import { ref, reactive, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { Plus, Refresh, Edit, Delete, VideoPlay } from "@element-plus/icons-vue";
import {
  getAgentPageApi,
  getAgentDetailApi,
  addAgentApi,
  updateAgentApi,
  deleteAgentApi,
  changeAgentStatusApi,
  runAgentApi,
  getModelOptionsApi,
  getPromptOptionsApi,
  getToolOptionsApi
} from "@/api/modules/ai-agent";

const xGrid = ref();
const formRef = ref();
const dialogVisible = ref(false);
const isEdit = ref(false);
const submitLoading = ref(false);

const modelOptions = ref([]);
const promptOptions = ref([]);
const toolOptions = ref([]);
const selectedToolIds = ref([]);

const formData = reactive({
  agentId: null,
  agentName: "",
  agentCode: "",
  description: "",
  modelId: null,
  promptId: null,
  flowJson: "",
  toolIdsJson: "",
  status: "0"
});

const formRules = {
  agentName: [{ required: true, message: "请输入名称", trigger: "blur" }],
  agentCode: [{ required: true, message: "请输入编码", trigger: "blur" }],
  modelId: [{ required: true, message: "请选择主模型", trigger: "change" }]
};

const modelName = id => {
  const m = modelOptions.value.find(x => x.id === id);
  return m ? `${m.name}（${m.modelKey}）` : id ?? "-";
};

const gridOptions = reactive({
  border: true,
  showOverflow: true,
  height: 600,
  loading: false,
  rowConfig: { keyField: "agentId" },
  toolbarConfig: { slots: { buttons: "toolbar_buttons" }, refresh: true, zoom: false },
  proxyConfig: {
    form: true,
    response: { result: "list", total: "total" },
    ajax: {
      query: async ({ page, form }) => {
        const params = { pageNum: page.currentPage, pageSize: page.pageSize, ...form };
        try {
          const res = await getAgentPageApi(params);
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
      { field: "keyword", title: "关键词", span: 6, itemRender: { name: "$input", props: { placeholder: "名称" } } },
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
    { field: "agentName", title: "名称", minWidth: 140 },
    { field: "agentCode", title: "编码", width: 150 },
    { field: "modelId", title: "模型", minWidth: 160, slots: { default: "model" } },
    { field: "description", title: "描述", minWidth: 160 },
    { field: "status", title: "状态", width: 90, slots: { default: "status" } },
    { field: "createTime", title: "创建时间", width: 170 },
    { title: "操作", width: 240, fixed: "right", slots: { default: "action" } }
  ]
});

const handleRefresh = () => xGrid.value?.commitProxy("query");

async function loadOptions() {
  try {
    const [mRes, pRes, tRes] = await Promise.all([
      getModelOptionsApi(),
      getPromptOptionsApi(),
      getToolOptionsApi()
    ]);
    modelOptions.value = mRes.data || [];
    promptOptions.value = pRes.data || [];
    toolOptions.value = tRes.data || [];
  } catch (e) {
    // 选项加载失败不阻断页面
  }
}

onMounted(loadOptions);

const resetForm = () => {
  Object.assign(formData, {
    agentId: null,
    agentName: "",
    agentCode: "",
    description: "",
    modelId: null,
    promptId: null,
    flowJson: "",
    toolIdsJson: "",
    status: "0"
  });
  selectedToolIds.value = [];
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
    const res = await getAgentDetailApi(row.agentId);
    const d = res.data || {};
    Object.assign(formData, {
      agentId: d.agentId,
      agentName: d.agentName || "",
      agentCode: d.agentCode || "",
      description: d.description || "",
      modelId: d.modelId ?? null,
      promptId: d.promptId ?? null,
      flowJson: d.flowJson || "",
      toolIdsJson: d.toolIdsJson || "",
      status: d.status || "0"
    });
    try {
      selectedToolIds.value = d.toolIdsJson ? JSON.parse(d.toolIdsJson) : [];
    } catch (err) {
      selectedToolIds.value = [];
    }
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
      agentName: formData.agentName,
      agentCode: formData.agentCode,
      description: formData.description || null,
      modelId: formData.modelId,
      promptId: formData.promptId || null,
      flowJson: formData.flowJson || null,
      toolIdsJson: selectedToolIds.value.length ? JSON.stringify(selectedToolIds.value) : null,
      status: formData.status
    };
    if (isEdit.value) await updateAgentApi(formData.agentId, payload);
    else await addAgentApi(payload);
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
  changeAgentStatusApi(row.agentId, target)
    .then(() => {
      ElMessage.success("操作成功");
      handleRefresh();
    })
    .catch(() => ElMessage.error("操作失败"));
};

const handleDelete = row => {
  ElMessageBox.confirm(`确定删除 Agent「${row.agentName}」？`, "提示", { type: "warning" }).then(async () => {
    try {
      await deleteAgentApi(row.agentId);
      ElMessage.success("删除成功");
      handleRefresh();
    } catch (e) {
      ElMessage.error("删除失败");
    }
  });
};

// ===== 试运行 =====
const runVisible = ref(false);
const runInput = ref("");
const runLoading = ref(false);
const runResult = ref(null);
const runAgentId = ref(null);

const handleRun = row => {
  runAgentId.value = row.agentId;
  runInput.value = "";
  runResult.value = null;
  runVisible.value = true;
};

const doRun = async () => {
  runLoading.value = true;
  try {
    const res = await runAgentApi(runAgentId.value, { input: runInput.value });
    runResult.value = res.data || null;
    ElMessage.success("运行完成");
  } catch (e) {
    ElMessage.error(e?.msg || e?.message || "运行失败");
  } finally {
    runLoading.value = false;
  }
};
</script>

<style scoped>
.table-box {
  background: #fff;
  border-radius: 8px;
}
.run-block {
  margin-top: 16px;
}
.run-title {
  font-weight: 600;
  margin-bottom: 8px;
}
.run-answer {
  white-space: pre-wrap;
  line-height: 1.6;
  padding: 10px 12px;
  background: #f5f7fa;
  border-radius: 6px;
}
.mb-3 {
  margin-bottom: 12px;
}
</style>
