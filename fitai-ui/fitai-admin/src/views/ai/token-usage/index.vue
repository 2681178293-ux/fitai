<template>
  <div class="table-box p-5">
    <el-row :gutter="16" class="mb-4">
      <el-col :xs="12" :sm="8" :md="6" :lg="4">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value">{{ stats.totalCalls ?? 0 }}</div>
          <div class="stat-label">累计调用</div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="8" :md="6" :lg="4">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value stat-token">{{ formatNumber(stats.totalTokens) }}</div>
          <div class="stat-label">累计 Token</div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="8" :md="6" :lg="4">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value">{{ stats.todayCalls ?? 0 }}</div>
          <div class="stat-label">今日调用</div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="8" :md="6" :lg="4">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value stat-token">{{ formatNumber(stats.todayTokens) }}</div>
          <div class="stat-label">今日 Token</div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="8" :md="6" :lg="4">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value">{{ stats.userCount ?? 0 }}</div>
          <div class="stat-label">用户数</div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16" class="mb-4">
      <el-col :xs="24" :md="8">
        <el-card shadow="never">
          <template #header><span>按场景汇总</span></template>
          <el-empty v-if="!(stats.sceneStats || []).length" description="暂无数据" :image-size="60" />
          <el-table v-else :data="stats.sceneStats" border size="small">
            <el-table-column label="场景" min-width="100">
              <template #default="{ row }">{{ sceneLabel(row.name) }}</template>
            </el-table-column>
            <el-table-column prop="calls" label="调用" width="80" />
            <el-table-column prop="tokens" label="Token" min-width="90">
              <template #default="{ row }">{{ formatNumber(row.tokens) }}</template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      <el-col :xs="24" :md="8">
        <el-card shadow="never">
          <template #header><span>按模型汇总</span></template>
          <el-empty v-if="!(stats.modelStats || []).length" description="暂无数据" :image-size="60" />
          <el-table v-else :data="stats.modelStats" border size="small">
            <el-table-column prop="name" label="模型" min-width="120" />
            <el-table-column prop="calls" label="调用" width="80" />
            <el-table-column prop="tokens" label="Token" min-width="90">
              <template #default="{ row }">{{ formatNumber(row.tokens) }}</template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      <el-col :xs="24" :md="8">
        <el-card shadow="never">
          <template #header>
            <span>近 30 天趋势</span>
          </template>
          <el-empty v-if="!trend.length" description="暂无数据" :image-size="60" />
          <el-table v-else :data="trend" border size="small" max-height="240">
            <el-table-column prop="name" label="日期" min-width="110" />
            <el-table-column prop="calls" label="调用" width="80" />
            <el-table-column prop="tokens" label="Token" min-width="90">
              <template #default="{ row }">{{ formatNumber(row.tokens) }}</template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <vxe-grid ref="xGrid" v-bind="gridOptions">
      <template #toolbar_buttons>
        <el-button :icon="Refresh" @click="handleRefresh">刷新</el-button>
      </template>
      <template #scene="{ row }">{{ sceneLabel(row.scene) }}</template>
    </vxe-grid>
  </div>
</template>

<script setup name="aiTokenUsage">
import { ref, reactive, onMounted } from "vue";
import { Refresh } from "@element-plus/icons-vue";
import { getTokenUsagePageApi, getTokenUsageStatsApi, getTokenUsageTrendApi } from "@/api/modules/ai-obs";

const xGrid = ref();
const stats = ref({});
const trend = ref([]);

function formatNumber(n) {
  if (n == null) return "0";
  const num = Number(n);
  if (num >= 1e6) return (num / 1e6).toFixed(2) + "M";
  if (num >= 1e3) return (num / 1e3).toFixed(2) + "K";
  return String(num);
}
function sceneLabel(scene) {
  const map = {
    chat: "对话",
    knowledge: "知识库问答",
    plan: "训练计划（生成）",
    plan_modify: "训练计划（改动作）",
    diet: "饮食方案",
    review: "训练点评"
  };
  return map[scene] || scene || "-";
}

const gridOptions = reactive({
  border: true,
  showOverflow: true,
  height: 520,
  loading: false,
  rowConfig: { keyField: "id" },
  toolbarConfig: { slots: { buttons: "toolbar_buttons" }, refresh: true, zoom: false },
  proxyConfig: {
    form: true,
    response: { result: "list", total: "total" },
    ajax: {
      query: async ({ page, form }) => {
        const params = { pageNum: page.currentPage, pageSize: page.pageSize, ...form };
        try {
          const res = await getTokenUsagePageApi(params);
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
      {
        field: "scene",
        title: "场景",
        span: 6,
        itemRender: {
          name: "$select",
          options: [
            { label: "全部", value: "" },
            { label: "训练计划（生成）", value: "plan" },
            { label: "训练计划（改动作）", value: "plan_modify" },
            { label: "饮食方案", value: "diet" },
            { label: "训练点评", value: "review" },
            { label: "知识库问答", value: "knowledge" }
          ],
          props: { placeholder: "请选择" }
        }
      },
      { field: "model", title: "模型", span: 6, itemRender: { name: "$input", props: { placeholder: "模型" } } },
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
    { field: "id", title: "ID", width: 90 },
    { field: "userId", title: "用户ID", width: 90 },
    { field: "nickname", title: "昵称", minWidth: 120 },
    { field: "scene", title: "场景", width: 150, slots: { default: "scene" } },
    { field: "model", title: "模型", minWidth: 140 },
    { field: "tokens", title: "Token", width: 100 },
    { field: "createTime", title: "时间", width: 170 }
  ]
});

const handleRefresh = () => xGrid.value?.commitProxy("query");

async function loadStats() {
  try {
    const res = await getTokenUsageStatsApi();
    stats.value = res.data || {};
  } catch (e) {
    stats.value = {};
  }
}
async function loadTrend() {
  try {
    const res = await getTokenUsageTrendApi(30);
    trend.value = res.data || [];
  } catch (e) {
    trend.value = [];
  }
}

onMounted(() => {
  loadStats();
  loadTrend();
});
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
.stat-token {
  color: #409eff;
}
</style>
