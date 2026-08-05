<template>
  <div class="p-5">
    <div class="mb-5 flex flex-wrap items-center justify-between gap-3">
      <h1 class="text-xl font-bold">AI 统计看板增强</h1>
      <div class="flex items-center gap-3">
        <span class="text-sm text-gray-500">趋势天数</span>
        <el-select v-model="days" style="width: 110px" @change="loadTrends">
          <el-option :value="7" label="近 7 天" />
          <el-option :value="14" label="近 14 天" />
          <el-option :value="30" label="近 30 天" />
          <el-option :value="90" label="近 90 天" />
        </el-select>
        <el-button type="primary" plain :icon="Refresh" :loading="loading" @click="loadAll">刷新</el-button>
      </div>
    </div>

    <!-- 概览卡片 -->
    <el-row v-loading="loading" :gutter="16">
      <el-col v-for="card in overviewCards" :key="card.key" :xs="12" :sm="8" :md="6" :lg="4">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value" :class="card.cls">{{ card.format ? formatNumber(overview[card.key]) : overview[card.key] ?? 0 }}</div>
          <div class="stat-label">{{ card.label }}</div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 趋势 -->
    <el-row :gutter="16" class="mt-2">
      <el-col :xs="24" :md="12">
        <el-card shadow="hover">
          <template #header><span>Token 趋势（近 {{ days }} 天）</span></template>
          <el-empty v-if="!tokenTrend.length" description="暂无数据" :image-size="60" />
          <el-table v-else :data="tokenTrend" border size="small" max-height="360">
            <el-table-column prop="label" label="日期" width="80" />
            <el-table-column label="会话Token" min-width="100">
              <template #default="{ row }">{{ formatNumber(row.messageTokens) }}</template>
            </el-table-column>
            <el-table-column label="非会话Token" min-width="110">
              <template #default="{ row }">{{ formatNumber(row.usageTokens) }}</template>
            </el-table-column>
            <el-table-column label="合计" min-width="100">
              <template #default="{ row }">{{ formatNumber(row.totalTokens) }}</template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      <el-col :xs="24" :md="12">
        <el-card shadow="hover">
          <template #header><span>调用趋势（近 {{ days }} 天）</span></template>
          <el-empty v-if="!callTrend.length" description="暂无数据" :image-size="60" />
          <el-table v-else :data="callTrend" border size="small" max-height="360">
            <el-table-column prop="label" label="日期" width="80" />
            <el-table-column prop="messages" label="消息数" min-width="90" />
            <el-table-column prop="assistantMessages" label="助手消息" min-width="90" />
            <el-table-column prop="newSessions" label="新增会话" min-width="90" />
            <el-table-column prop="usageCalls" label="非会话调用" min-width="100" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <!-- 分布 -->
    <el-row :gutter="16" class="mt-4">
      <el-col :xs="24" :md="12">
        <el-card shadow="hover">
          <template #header><span>模型分布</span></template>
          <el-empty v-if="!modelDist.length" description="暂无数据" :image-size="60" />
          <el-table v-else :data="modelDist" border size="small">
            <el-table-column prop="name" label="模型" min-width="140" />
            <el-table-column label="Token 合计" min-width="120">
              <template #default="{ row }">{{ formatNumber(row.tokenCount) }}</template>
            </el-table-column>
            <el-table-column prop="callCount" label="调用次数" min-width="100" />
          </el-table>
        </el-card>
      </el-col>
      <el-col :xs="24" :md="12">
        <el-card shadow="hover">
          <template #header><span>场景分布</span></template>
          <el-empty v-if="!sceneDist.length" description="暂无数据" :image-size="60" />
          <el-table v-else :data="sceneDist" border size="small">
            <el-table-column label="场景" min-width="140">
              <template #default="{ row }">{{ sceneLabel(row.name) }}</template>
            </el-table-column>
            <el-table-column label="Token 合计" min-width="120">
              <template #default="{ row }">{{ formatNumber(row.tokenCount) }}</template>
            </el-table-column>
            <el-table-column prop="callCount" label="调用次数" min-width="100" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup name="aiStatsPlus">
import { ref, onMounted } from "vue";
import { Refresh } from "@element-plus/icons-vue";
import { ElMessage } from "element-plus";
import {
  getStatsOverviewApi,
  getStatsTokenTrendApi,
  getStatsCallTrendApi,
  getStatsModelDistApi,
  getStatsSceneDistApi
} from "@/api/modules/ai-obs";

const loading = ref(false);
const days = ref(7);
const overview = ref({});
const tokenTrend = ref([]);
const callTrend = ref([]);
const modelDist = ref([]);
const sceneDist = ref([]);

const overviewCards = [
  { key: "totalSessions", label: "总会话数" },
  { key: "todaySessions", label: "今日会话" },
  { key: "activeSessions", label: "活跃会话" },
  { key: "totalMessages", label: "总消息数" },
  { key: "todayMessages", label: "今日消息" },
  { key: "todayCalls", label: "今日调用" },
  { key: "totalTokens", label: "总 Token", format: true, cls: "stat-token" },
  { key: "todayTokens", label: "今日 Token", format: true, cls: "stat-token" },
  { key: "tokenRecords", label: "用量记录数" },
  { key: "knowledgeDocs", label: "知识库文档" },
  { key: "totalModels", label: "模型总数" },
  { key: "enabledModels", label: "启用模型" }
];

function formatNumber(n) {
  if (n == null) return "0";
  const num = Number(n);
  if (num >= 1e6) return (num / 1e6).toFixed(2) + "M";
  if (num >= 1e3) return (num / 1e3).toFixed(2) + "K";
  return String(num);
}
function sceneLabel(name) {
  const map = {
    chat: "对话",
    knowledge: "知识库问答",
    plan: "训练计划（生成）",
    plan_modify: "训练计划（改动作）",
    diet: "饮食方案",
    review: "训练点评"
  };
  return map[name] || name || "-";
}

async function loadOverview() {
  try {
    const res = await getStatsOverviewApi();
    overview.value = res.data || {};
  } catch (e) {
    overview.value = {};
  }
}
async function loadTrends() {
  try {
    const [t, c] = await Promise.all([getStatsTokenTrendApi(days.value), getStatsCallTrendApi(days.value)]);
    tokenTrend.value = t.data || [];
    callTrend.value = c.data || [];
  } catch (e) {
    tokenTrend.value = [];
    callTrend.value = [];
  }
}
async function loadDist() {
  try {
    const [m, s] = await Promise.all([getStatsModelDistApi(), getStatsSceneDistApi()]);
    modelDist.value = m.data || [];
    sceneDist.value = s.data || [];
  } catch (e) {
    modelDist.value = [];
    sceneDist.value = [];
  }
}

async function loadAll() {
  loading.value = true;
  try {
    await Promise.all([loadOverview(), loadTrends(), loadDist()]);
  } catch (e) {
    ElMessage.error("加载统计数据失败");
  } finally {
    loading.value = false;
  }
}

onMounted(loadAll);
</script>

<style scoped>
.stat-card {
  margin-bottom: 16px;
}
.stat-value {
  font-size: 24px;
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
