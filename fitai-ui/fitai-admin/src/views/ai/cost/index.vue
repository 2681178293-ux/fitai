<template>
  <div class="table-box p-5">
    <el-row :gutter="16" class="mb-4">
      <el-col :xs="12" :sm="8" :md="6" :lg="4">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value stat-money">¥{{ formatMoney(stats.totalCost) }}</div>
          <div class="stat-label">累计费用</div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="8" :md="6" :lg="4">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value stat-money">¥{{ formatMoney(stats.todayCost) }}</div>
          <div class="stat-label">今日费用</div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="8" :md="6" :lg="4">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value">{{ formatNumber(stats.totalTokens) }}</div>
          <div class="stat-label">累计 Token</div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="8" :md="6" :lg="4">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value">{{ stats.totalCount ?? 0 }}</div>
          <div class="stat-label">流水条数</div>
        </el-card>
      </el-col>
    </el-row>

    <el-card v-if="(stats.modelStats || []).length" shadow="never" class="mb-4">
      <template #header><span>按模型成本汇总</span></template>
      <el-table :data="stats.modelStats" border size="small">
        <el-table-column prop="modelCode" label="模型" min-width="140" />
        <el-table-column prop="calls" label="调用次数" width="120" />
        <el-table-column prop="totalTokens" label="Token 合计" width="140">
          <template #default="{ row }">{{ formatNumber(row.totalTokens) }}</template>
        </el-table-column>
        <el-table-column prop="costAmount" label="费用(元)" width="140">
          <template #default="{ row }">¥{{ formatMoney(row.costAmount) }}</template>
        </el-table-column>
      </el-table>
    </el-card>

    <vxe-grid ref="xGrid" v-bind="gridOptions">
      <template #toolbar_buttons>
        <el-button :icon="Refresh" @click="handleRefresh">刷新</el-button>
      </template>
      <template #cost="{ row }">¥{{ formatMoney(row.costAmount) }}</template>
      <template #action="{ row }">
        <el-button type="primary" link :icon="View" @click="handleDetail(row)">详情</el-button>
      </template>
    </vxe-grid>

    <el-dialog v-model="dialogVisible" title="成本流水详情" width="600px" destroy-on-close>
      <el-descriptions v-if="detail" :column="2" border size="small">
        <el-descriptions-item label="流水ID">{{ detail.costId }}</el-descriptions-item>
        <el-descriptions-item label="用户ID">{{ detail.customerId ?? "-" }}</el-descriptions-item>
        <el-descriptions-item label="会话ID">{{ detail.sessionId ?? "-" }}</el-descriptions-item>
        <el-descriptions-item label="模型">{{ detail.modelCode || "-" }}</el-descriptions-item>
        <el-descriptions-item label="业务场景">{{ detail.bizScene || "-" }}</el-descriptions-item>
        <el-descriptions-item label="费用(元)">¥{{ formatMoney(detail.costAmount) }}</el-descriptions-item>
        <el-descriptions-item label="提示 Token">{{ detail.promptTokens ?? "-" }}</el-descriptions-item>
        <el-descriptions-item label="补全 Token">{{ detail.completionTokens ?? "-" }}</el-descriptions-item>
        <el-descriptions-item label="总 Token">{{ detail.totalTokens ?? "-" }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ detail.createTime || "-" }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="dialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="aiCost">
import { ref, reactive, onMounted } from "vue";
import { ElMessage } from "element-plus";
import { Refresh, View } from "@element-plus/icons-vue";
import { getCostPageApi, getCostStatsApi, getCostByIdApi } from "@/api/modules/ai-obs";

const xGrid = ref();
const stats = ref({});
const dialogVisible = ref(false);
const detail = ref(null);

function formatMoney(n) {
  if (n == null) return "0.00";
  return Number(n).toFixed(2);
}
function formatNumber(n) {
  if (n == null) return "0";
  const num = Number(n);
  if (num >= 1e6) return (num / 1e6).toFixed(2) + "M";
  if (num >= 1e3) return (num / 1e3).toFixed(2) + "K";
  return String(num);
}

const gridOptions = reactive({
  border: true,
  showOverflow: true,
  height: 520,
  loading: false,
  rowConfig: { keyField: "costId" },
  toolbarConfig: { slots: { buttons: "toolbar_buttons" }, refresh: true, zoom: false },
  proxyConfig: {
    form: true,
    response: { result: "list", total: "total" },
    ajax: {
      query: async ({ page, form }) => {
        const params = { pageNum: page.currentPage, pageSize: page.pageSize, ...form };
        try {
          const res = await getCostPageApi(params);
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
      { field: "userId", title: "用户ID", span: 6, itemRender: { name: "$input", props: { placeholder: "用户ID" } } },
      { field: "modelCode", title: "模型", span: 6, itemRender: { name: "$input", props: { placeholder: "模型" } } },
      { field: "bizScene", title: "场景", span: 6, itemRender: { name: "$input", props: { placeholder: "业务场景" } } },
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
    { field: "costId", title: "流水ID", width: 90 },
    { field: "customerId", title: "用户ID", width: 90 },
    { field: "modelCode", title: "模型", minWidth: 140 },
    { field: "bizScene", title: "场景", width: 120 },
    { field: "promptTokens", title: "提示Token", width: 100 },
    { field: "completionTokens", title: "补全Token", width: 100 },
    { field: "totalTokens", title: "总Token", width: 100 },
    { field: "costAmount", title: "费用(元)", width: 110, slots: { default: "cost" } },
    { field: "createTime", title: "时间", width: 170 },
    { title: "操作", width: 90, fixed: "right", slots: { default: "action" } }
  ]
});

const handleRefresh = () => xGrid.value?.commitProxy("query");

async function loadStats() {
  try {
    const res = await getCostStatsApi();
    stats.value = res.data || {};
  } catch (e) {
    stats.value = {};
  }
}

const handleDetail = async row => {
  try {
    const res = await getCostByIdApi(row.costId);
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
.stat-money {
  color: #e6a23c;
}
</style>
