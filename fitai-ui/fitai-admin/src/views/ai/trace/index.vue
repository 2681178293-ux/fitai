<template>
  <div class="table-box p-5">
    <el-row :gutter="16" class="mb-4">
      <el-col :xs="12" :sm="8" :md="6" :lg="4">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value">{{ stats.totalCount ?? 0 }}</div>
          <div class="stat-label">链路总数</div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="8" :md="6" :lg="4">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value stat-ok">{{ stats.successCount ?? 0 }}</div>
          <div class="stat-label">成功数</div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="8" :md="6" :lg="4">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value stat-fail">{{ stats.failedCount ?? 0 }}</div>
          <div class="stat-label">失败数</div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="8" :md="6" :lg="4">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value">{{ stats.avgLatencyMs ?? 0 }} ms</div>
          <div class="stat-label">平均耗时</div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="8" :md="6" :lg="4">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value">{{ stats.todayCount ?? 0 }}</div>
          <div class="stat-label">今日链路</div>
        </el-card>
      </el-col>
    </el-row>

    <vxe-grid ref="xGrid" v-bind="gridOptions">
      <template #toolbar_buttons>
        <el-button :icon="Refresh" @click="handleRefresh">刷新</el-button>
      </template>
      <template #status="{ row }">
        <el-tag v-if="String(row.status) === '0'" type="success">成功</el-tag>
        <el-tag v-else type="danger">失败</el-tag>
      </template>
      <template #action="{ row }">
        <el-button type="primary" link :icon="View" @click="handleDetail(row)">详情</el-button>
      </template>
    </vxe-grid>

    <el-dialog v-model="dialogVisible" title="调用链路详情" width="760px" destroy-on-close>
      <el-descriptions v-if="detail" :column="2" border size="small">
        <el-descriptions-item label="链路ID">{{ detail.traceId }}</el-descriptions-item>
        <el-descriptions-item label="链路UUID">{{ detail.traceUuid || "-" }}</el-descriptions-item>
        <el-descriptions-item label="用户ID">{{ detail.customerId ?? "-" }}</el-descriptions-item>
        <el-descriptions-item label="会话ID">{{ detail.sessionId ?? "-" }}</el-descriptions-item>
        <el-descriptions-item label="会话标识">{{ detail.conversationId || "-" }}</el-descriptions-item>
        <el-descriptions-item label="Agent">{{ detail.agentCode || "-" }}</el-descriptions-item>
        <el-descriptions-item label="模型">{{ detail.modelCode || "-" }}</el-descriptions-item>
        <el-descriptions-item label="业务场景">{{ detail.bizScene || "-" }}</el-descriptions-item>
        <el-descriptions-item label="提示 Token">{{ detail.promptTokens ?? "-" }}</el-descriptions-item>
        <el-descriptions-item label="补全 Token">{{ detail.completionTokens ?? "-" }}</el-descriptions-item>
        <el-descriptions-item label="工具调用次数">{{ detail.toolCallCount ?? "-" }}</el-descriptions-item>
        <el-descriptions-item label="总耗时">{{ detail.totalLatencyMs ?? "-" }} ms</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag v-if="String(detail.status) === '0'" type="success">成功</el-tag>
          <el-tag v-else type="danger">失败</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ detail.createTime || "-" }}</el-descriptions-item>
        <el-descriptions-item label="错误信息" :span="2">{{ detail.errorMsg || "-" }}</el-descriptions-item>
        <el-descriptions-item label="链路步骤" :span="2">
          <pre class="steps-json">{{ formatJson(detail.stepsJson) }}</pre>
        </el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="dialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="aiTrace">
import { ref, reactive, onMounted } from "vue";
import { ElMessage } from "element-plus";
import { Refresh, View } from "@element-plus/icons-vue";
import { getTracePageApi, getTraceStatsApi, getTraceByIdApi } from "@/api/modules/ai-obs";

const xGrid = ref();
const stats = ref({});
const dialogVisible = ref(false);
const detail = ref(null);

function formatJson(raw) {
  if (!raw) return "-";
  try {
    return JSON.stringify(JSON.parse(raw), null, 2);
  } catch (e) {
    return raw;
  }
}

const gridOptions = reactive({
  border: true,
  showOverflow: true,
  height: 560,
  loading: false,
  rowConfig: { keyField: "traceId" },
  toolbarConfig: { slots: { buttons: "toolbar_buttons" }, refresh: true, zoom: false },
  proxyConfig: {
    form: true,
    response: { result: "list", total: "total" },
    ajax: {
      query: async ({ page, form }) => {
        const params = { pageNum: page.currentPage, pageSize: page.pageSize, ...form };
        try {
          const res = await getTracePageApi(params);
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
      { field: "keyword", title: "关键词", span: 6, itemRender: { name: "$input", props: { placeholder: "链路ID/UUID" } } },
      { field: "userId", title: "用户ID", span: 5, itemRender: { name: "$input", props: { placeholder: "用户ID" } } },
      { field: "agentCode", title: "Agent", span: 5, itemRender: { name: "$input", props: { placeholder: "coach/diet/rehab/cs" } } },
      { field: "modelCode", title: "模型", span: 5, itemRender: { name: "$input", props: { placeholder: "模型" } } },
      { field: "bizScene", title: "场景", span: 5, itemRender: { name: "$input", props: { placeholder: "业务场景" } } },
      {
        field: "status",
        title: "状态",
        span: 5,
        itemRender: {
          name: "$select",
          options: [
            { label: "全部", value: "" },
            { label: "成功", value: "0" },
            { label: "失败", value: "1" }
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
    { field: "traceId", title: "链路ID", width: 90 },
    { field: "traceUuid", title: "UUID", minWidth: 180 },
    { field: "customerId", title: "用户ID", width: 90 },
    { field: "agentCode", title: "Agent", width: 90 },
    { field: "modelCode", title: "模型", width: 130 },
    { field: "bizScene", title: "场景", width: 110 },
    { field: "promptTokens", title: "提示Token", width: 100 },
    { field: "completionTokens", title: "补全Token", width: 100 },
    { field: "totalLatencyMs", title: "耗时(ms)", width: 100 },
    { field: "status", title: "状态", width: 80, slots: { default: "status" } },
    { field: "createTime", title: "时间", width: 170 },
    { title: "操作", width: 90, fixed: "right", slots: { default: "action" } }
  ]
});

const handleRefresh = () => xGrid.value?.commitProxy("query");

async function loadStats() {
  try {
    const res = await getTraceStatsApi();
    stats.value = res.data || {};
  } catch (e) {
    stats.value = {};
  }
}

const handleDetail = async row => {
  try {
    const res = await getTraceByIdApi(row.traceId);
    detail.value = res.data || null;
    dialogVisible.value = true;
  } catch (e) {
    ElMessage.error("获取详情失败");
  }
};

onMounted(loadStats);
</script>

<style scoped>
.table-box {
  background: #fff;
  border-radius: 8px;
}
.stat-card {
  margin-bottom: 8px;
}
.stat-value {
  font-size: 22px;
  font-weight: 600;
  color: #303133;
}
.stat-label {
  font-size: 13px;
  color: #909399;
  margin-top: 6px;
}
.stat-ok {
  color: #67c23a;
}
.stat-fail {
  color: #f56c6c;
}
.steps-json {
  max-height: 260px;
  overflow: auto;
  white-space: pre-wrap;
  word-break: break-all;
  margin: 0;
  font-size: 12px;
}
</style>
