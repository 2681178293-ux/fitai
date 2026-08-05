<template>
  <div class="table-box p-5">
    <vxe-grid ref="xGrid" v-bind="gridOptions">
      <template #toolbar_buttons>
        <el-button type="success" :icon="VideoPlay" @click="openRun">运行 RAG 评估</el-button>
        <el-button :icon="Refresh" @click="handleRefresh">刷新</el-button>
      </template>
      <template #recall="{ row }">{{ scoreText(row.recallScore) }}</template>
      <template #faith="{ row }">{{ scoreText(row.faithfulness) }}</template>
      <template #relevancy="{ row }">{{ scoreText(row.relevancyScore) }}</template>
      <template #correctness="{ row }">{{ scoreText(row.correctness) }}</template>
      <template #action="{ row }">
        <el-button type="primary" link :icon="View" @click="handleView(row)">详情</el-button>
        <el-button type="danger" link :icon="Delete" @click="handleDelete(row)">删除</el-button>
      </template>
    </vxe-grid>

    <!-- 运行评估 -->
    <el-dialog v-model="runVisible" title="运行 RAG 评估" width="640px" destroy-on-close>
      <el-tabs v-model="runMode">
        <el-tab-pane label="数据集批量" name="dataset">
          <el-form label-width="100px">
            <el-form-item label="RAG 数据集">
              <el-select v-model="runForm.datasetId" placeholder="请选择 rag 类型数据集" filterable clearable style="width: 100%">
                <el-option v-for="d in datasetOptions" :key="d.id" :label="d.name" :value="d.id" />
              </el-select>
            </el-form-item>
            <el-form-item label="知识库ID">
              <el-input v-model="runForm.knowledgeId" placeholder="选填，仅标记与校验" />
            </el-form-item>
          </el-form>
        </el-tab-pane>
        <el-tab-pane label="单条评估" name="single">
          <el-form label-width="100px">
            <el-form-item label="查询问题">
              <el-input v-model="runForm.question" type="textarea" :rows="2" placeholder="测试问题" />
            </el-form-item>
            <el-form-item label="期望答案">
              <el-input v-model="runForm.expectedAnswer" type="textarea" :rows="2" placeholder="选填，提供后计算召回与正确性" />
            </el-form-item>
            <el-form-item label="知识库ID">
              <el-input v-model="runForm.knowledgeId" placeholder="选填" />
            </el-form-item>
          </el-form>
        </el-tab-pane>
      </el-tabs>

      <template v-if="report">
        <el-divider>评估报告</el-divider>
        <el-descriptions :column="2" border size="small">
          <el-descriptions-item label="样本数">{{ report.summary?.samples }}</el-descriptions-item>
          <el-descriptions-item label="平均耗时">{{ report.summary?.avgLatencyMs }} ms</el-descriptions-item>
          <el-descriptions-item label="召回均值">{{ avgText(report.summary?.avgRecall) }}</el-descriptions-item>
          <el-descriptions-item label="忠实度均值">{{ avgText(report.summary?.avgFaithfulness) }}</el-descriptions-item>
          <el-descriptions-item label="相关性均值">{{ avgText(report.summary?.avgRelevancy) }}</el-descriptions-item>
          <el-descriptions-item label="正确性均值">{{ avgText(report.summary?.avgCorrectness) }}</el-descriptions-item>
          <el-descriptions-item label="忠实度达标率" :span="2">
            {{ report.summary ? (report.summary.faithfulnessPassRate * 100).toFixed(1) + "%" : "-" }}
          </el-descriptions-item>
        </el-descriptions>
      </template>

      <template #footer>
        <el-button @click="runVisible = false">关闭</el-button>
        <el-button type="primary" :loading="runLoading" @click="doRun">开始评估</el-button>
      </template>
    </el-dialog>

    <!-- 记录详情 -->
    <el-dialog v-model="viewVisible" title="评估记录详情" width="680px" destroy-on-close>
      <el-descriptions v-if="viewData" :column="2" border size="small">
        <el-descriptions-item label="评估名称" :span="2">{{ viewData.evalName || "-" }}</el-descriptions-item>
        <el-descriptions-item label="问题" :span="2">{{ viewData.question }}</el-descriptions-item>
        <el-descriptions-item label="期望答案" :span="2">{{ viewData.expectedAnswer || "-" }}</el-descriptions-item>
        <el-descriptions-item label="实际答案" :span="2">{{ viewData.actualAnswer || "-" }}</el-descriptions-item>
        <el-descriptions-item label="召回">{{ scoreText(viewData.recallScore) }}</el-descriptions-item>
        <el-descriptions-item label="忠实度">{{ scoreText(viewData.faithfulness) }}</el-descriptions-item>
        <el-descriptions-item label="相关性">{{ scoreText(viewData.relevancyScore) }}</el-descriptions-item>
        <el-descriptions-item label="正确性">{{ scoreText(viewData.correctness) }}</el-descriptions-item>
        <el-descriptions-item label="耗时">{{ viewData.latencyMs ?? "-" }} ms</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ viewData.createTime }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup name="aiRagEval">
import { ref, reactive, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { Refresh, Delete, VideoPlay, View } from "@element-plus/icons-vue";
import {
  getRagEvalPageApi,
  getRagEvalDetailApi,
  runRagEvalApi,
  deleteRagEvalApi,
  getDatasetOptionsApi
} from "@/api/modules/ai-agent";

const xGrid = ref();
const datasetOptions = ref([]);

const scoreText = v => (v == null ? "-" : Number(v).toFixed(2));
const avgText = v => (v == null ? "-" : Number(v).toFixed(3));

const gridOptions = reactive({
  border: true,
  showOverflow: true,
  height: 600,
  loading: false,
  rowConfig: { keyField: "evalId" },
  toolbarConfig: { slots: { buttons: "toolbar_buttons" }, refresh: true, zoom: false },
  proxyConfig: {
    form: true,
    response: { result: "list", total: "total" },
    ajax: {
      query: async ({ page, form }) => {
        const params = { pageNum: page.currentPage, pageSize: page.pageSize, ...form };
        try {
          const res = await getRagEvalPageApi(params);
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
      { field: "knowledgeId", title: "知识库ID", span: 6, itemRender: { name: "$input", props: { placeholder: "知识库ID" } } },
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
    { field: "evalName", title: "评估名称", minWidth: 130 },
    { field: "question", title: "问题", minWidth: 180 },
    { field: "recallScore", title: "召回", width: 80, slots: { default: "recall" } },
    { field: "faithfulness", title: "忠实度", width: 80, slots: { default: "faith" } },
    { field: "relevancyScore", title: "相关性", width: 80, slots: { default: "relevancy" } },
    { field: "correctness", title: "正确性", width: 80, slots: { default: "correctness" } },
    { field: "latencyMs", title: "耗时(ms)", width: 90 },
    { field: "createTime", title: "创建时间", width: 170 },
    { title: "操作", width: 140, fixed: "right", slots: { default: "action" } }
  ]
});

const handleRefresh = () => xGrid.value?.commitProxy("query");

async function loadOptions() {
  try {
    const res = await getDatasetOptionsApi("rag");
    datasetOptions.value = res.data || [];
  } catch (e) {
    // ignore
  }
}
onMounted(loadOptions);

// ===== 运行评估 =====
const runVisible = ref(false);
const runMode = ref("dataset");
const runLoading = ref(false);
const report = ref(null);
const runForm = reactive({ datasetId: null, knowledgeId: "", question: "", expectedAnswer: "" });

const openRun = () => {
  Object.assign(runForm, { datasetId: null, knowledgeId: "", question: "", expectedAnswer: "" });
  report.value = null;
  runMode.value = "dataset";
  runVisible.value = true;
};

const doRun = async () => {
  const payload = { knowledgeId: runForm.knowledgeId ? Number(runForm.knowledgeId) : null };
  if (runMode.value === "dataset") {
    if (!runForm.datasetId) return ElMessage.warning("请选择数据集");
    payload.datasetId = runForm.datasetId;
  } else {
    if (!runForm.question) return ElMessage.warning("请输入查询问题");
    payload.question = runForm.question;
    payload.expectedAnswer = runForm.expectedAnswer || null;
  }
  runLoading.value = true;
  try {
    const res = await runRagEvalApi(payload);
    report.value = res.data || null;
    ElMessage.success("评估完成，记录已落库");
    handleRefresh();
  } catch (e) {
    ElMessage.error(e?.msg || e?.message || "评估失败");
  } finally {
    runLoading.value = false;
  }
};

// ===== 详情 =====
const viewVisible = ref(false);
const viewData = ref(null);
const handleView = async row => {
  try {
    const res = await getRagEvalDetailApi(row.evalId);
    viewData.value = res.data || row;
  } catch (e) {
    viewData.value = row;
  }
  viewVisible.value = true;
};

const handleDelete = row => {
  ElMessageBox.confirm("确定删除该评估记录？", "提示", { type: "warning" }).then(async () => {
    try {
      await deleteRagEvalApi(row.evalId);
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
