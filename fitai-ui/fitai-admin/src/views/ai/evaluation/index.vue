<template>
  <div class="table-box p-5">
    <vxe-grid ref="xGrid" v-bind="gridOptions">
      <template #toolbar_buttons>
        <el-button type="primary" :icon="Plus" @click="handleAdd">新增评测</el-button>
        <el-button :icon="Refresh" @click="handleRefresh">刷新</el-button>
      </template>
      <template #model="{ row }">
        <span>{{ row.modelName || row.modelId || "-" }}<span v-if="row.modelKey" class="text-gray-400">（{{ row.modelKey }}）</span></span>
      </template>
      <template #score="{ row }">
        <span v-if="row.status === '1'">{{ accuracyText(row.metricsJson) }}</span>
        <span v-else>-</span>
      </template>
      <template #status="{ row }">
        <el-tag v-if="row.status === '0'" type="warning">进行中</el-tag>
        <el-tag v-else-if="row.status === '1'" type="success">完成</el-tag>
        <el-tag v-else-if="row.status === '2'" type="danger">失败</el-tag>
        <el-tag v-else type="info">未开始</el-tag>
      </template>
      <template #action="{ row }">
        <el-button type="success" link :icon="VideoPlay" :disabled="row.status === '0'" @click="handleRun(row)">运行评测</el-button>
        <el-button type="primary" link :icon="View" @click="handleView(row)">报告</el-button>
        <el-button type="primary" link :icon="Edit" @click="handleEdit(row)">编辑</el-button>
        <el-button type="danger" link :icon="Delete" @click="handleDelete(row)">删除</el-button>
      </template>
    </vxe-grid>

    <!-- 新增/编辑 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑评测' : '新增评测'" width="520px" destroy-on-close>
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
        <el-form-item label="评测名称" prop="evalName">
          <el-input v-model="formData.evalName" placeholder="评测名称" />
        </el-form-item>
        <el-form-item label="模型" prop="modelId">
          <el-select v-model="formData.modelId" placeholder="请选择模型" filterable style="width: 100%">
            <el-option v-for="m in modelOptions" :key="m.id" :label="`${m.name}（${m.modelKey}）`" :value="m.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="数据集">
          <el-select v-model="formData.datasetId" placeholder="请选择 eval 类型数据集" filterable clearable style="width: 100%">
            <el-option v-for="d in datasetOptions" :key="d.id" :label="d.name" :value="d.id" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 报告 -->
    <el-dialog v-model="viewVisible" title="评测报告" width="560px" destroy-on-close>
      <el-descriptions v-if="viewMetrics" :column="1" border size="small">
        <el-descriptions-item label="准确率">{{ (viewMetrics.accuracy * 100).toFixed(1) }}%</el-descriptions-item>
        <el-descriptions-item label="样本行数">{{ viewMetrics.rowCount }}</el-descriptions-item>
        <el-descriptions-item label="达标数（≥0.6）">{{ viewMetrics.passCount }}</el-descriptions-item>
        <el-descriptions-item label="平均单条耗时">{{ viewMetrics.avgLatencyMs }} ms</el-descriptions-item>
        <el-descriptions-item label="累计 Token">{{ viewMetrics.totalTokens }}</el-descriptions-item>
        <el-descriptions-item label="提示 / 补全 Token">{{ viewMetrics.promptTokens }} / {{ viewMetrics.completionTokens }}</el-descriptions-item>
        <el-descriptions-item label="评测总耗时">{{ viewMetrics.durationSec }} s</el-descriptions-item>
      </el-descriptions>
      <el-empty v-else description="暂无评测结果，请先运行评测" />
    </el-dialog>
  </div>
</template>

<script setup name="aiEvaluation">
import { ref, reactive, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { Plus, Refresh, Edit, Delete, VideoPlay, View } from "@element-plus/icons-vue";
import {
  getEvaluationPageApi,
  addEvaluationApi,
  updateEvaluationApi,
  deleteEvaluationApi,
  runEvaluationApi,
  getModelOptionsApi,
  getDatasetOptionsApi
} from "@/api/modules/ai-agent";

const xGrid = ref();
const formRef = ref();
const dialogVisible = ref(false);
const isEdit = ref(false);
const submitLoading = ref(false);

const modelOptions = ref([]);
const datasetOptions = ref([]);

const formData = reactive({ evaluationId: null, evalName: "", modelId: null, datasetId: null });

const formRules = {
  evalName: [{ required: true, message: "请输入评测名称", trigger: "blur" }],
  modelId: [{ required: true, message: "请选择模型", trigger: "change" }]
};

function parseMetrics(json) {
  if (!json) return null;
  try {
    return typeof json === "string" ? JSON.parse(json) : json;
  } catch (e) {
    return null;
  }
}

const accuracyText = json => {
  const m = parseMetrics(json);
  return m && m.accuracy != null ? `${(m.accuracy * 100).toFixed(1)}%` : "-";
};

const gridOptions = reactive({
  border: true,
  showOverflow: true,
  height: 600,
  loading: false,
  rowConfig: { keyField: "evaluationId" },
  toolbarConfig: { slots: { buttons: "toolbar_buttons" }, refresh: true, zoom: false },
  proxyConfig: {
    form: true,
    response: { result: "list", total: "total" },
    ajax: {
      query: async ({ page, form }) => {
        const params = { pageNum: page.currentPage, pageSize: page.pageSize, ...form };
        try {
          const res = await getEvaluationPageApi(params);
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
      { field: "keyword", title: "关键词", span: 6, itemRender: { name: "$input", props: { placeholder: "评测名称" } } },
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
    { field: "evalName", title: "评测名称", minWidth: 150 },
    { field: "modelName", title: "模型", minWidth: 160, slots: { default: "model" } },
    { field: "datasetName", title: "数据集", minWidth: 130 },
    { field: "score", title: "准确率", width: 100, slots: { default: "score" } },
    { field: "status", title: "状态", width: 100, slots: { default: "status" } },
    { field: "createTime", title: "创建时间", width: 170 },
    { title: "操作", width: 280, fixed: "right", slots: { default: "action" } }
  ]
});

const handleRefresh = () => xGrid.value?.commitProxy("query");

async function loadOptions() {
  try {
    const [mRes, dRes] = await Promise.all([getModelOptionsApi(), getDatasetOptionsApi("eval")]);
    modelOptions.value = mRes.data || [];
    datasetOptions.value = dRes.data || [];
  } catch (e) {
    // ignore
  }
}
onMounted(loadOptions);

const handleAdd = () => {
  isEdit.value = false;
  Object.assign(formData, { evaluationId: null, evalName: "", modelId: null, datasetId: null });
  dialogVisible.value = true;
};

const handleEdit = row => {
  isEdit.value = true;
  Object.assign(formData, {
    evaluationId: row.evaluationId,
    evalName: row.evalName || "",
    modelId: row.modelId ?? null,
    datasetId: row.datasetId ?? null
  });
  dialogVisible.value = true;
};

const handleSubmit = async () => {
  try {
    await formRef.value.validate();
    submitLoading.value = true;
    const payload = {
      evaluationId: isEdit.value ? formData.evaluationId : null,
      evalName: formData.evalName,
      modelId: formData.modelId,
      datasetId: formData.datasetId || null
    };
    if (isEdit.value) await updateEvaluationApi(payload);
    else await addEvaluationApi(payload);
    ElMessage.success(isEdit.value ? "编辑成功" : "新增成功");
    dialogVisible.value = false;
    handleRefresh();
  } catch (e) {
    if (e !== false) ElMessage.error("操作失败");
  } finally {
    submitLoading.value = false;
  }
};

const handleRun = row => {
  ElMessageBox.confirm(`确定运行评测「${row.evalName}」？将异步逐行推理并评分。`, "提示", { type: "warning" }).then(async () => {
    try {
      await runEvaluationApi(row.evaluationId);
      ElMessage.success("评测已开始执行，请稍后刷新查看结果");
      handleRefresh();
    } catch (e) {
      ElMessage.error(e?.msg || e?.message || "触发失败");
    }
  });
};

const viewVisible = ref(false);
const viewMetrics = ref(null);
const handleView = row => {
  viewMetrics.value = parseMetrics(row.metricsJson);
  viewVisible.value = true;
};

const handleDelete = row => {
  ElMessageBox.confirm(`确定删除评测「${row.evalName}」？`, "提示", { type: "warning" }).then(async () => {
    try {
      await deleteEvaluationApi(row.evaluationId);
      ElMessage.success("删除成功");
      handleRefresh();
    } catch (e) {
      ElMessage.error("删除失败");
    }
  });
};
</script>

<style scoped>
.table-box {
  background: #fff;
  border-radius: 8px;
}
</style>
