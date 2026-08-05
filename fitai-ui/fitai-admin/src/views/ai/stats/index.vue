<template>
  <div class="p-5">
    <div class="mb-6 flex flex-wrap items-center justify-between gap-3">
      <h1 class="text-xl font-bold">AI 统计</h1>
      <div class="flex items-center gap-3">
        <span v-if="lastLoadedText" class="text-xs text-gray-400">更新于 {{ lastLoadedText }}</span>
        <el-button type="primary" plain :icon="Refresh" :loading="loading" @click="fetchStats">刷新数据</el-button>
      </div>
    </div>
    <div v-loading="loading" class="stats-grid">
      <el-row :gutter="16">
        <el-col :xs="24" :sm="12" :md="8" :lg="6">
          <el-card shadow="hover" class="stat-card">
            <div class="stat-value">{{ stats.totalConversations ?? 0 }}</div>
            <div class="stat-label">总会话数</div>
          </el-card>
        </el-col>
        <el-col :xs="24" :sm="12" :md="8" :lg="6">
          <el-card shadow="hover" class="stat-card">
            <div class="stat-value">{{ stats.todayConversations ?? 0 }}</div>
            <div class="stat-label">今日会话数</div>
          </el-card>
        </el-col>
        <el-col :xs="24" :sm="12" :md="8" :lg="6">
          <el-card shadow="hover" class="stat-card">
            <div class="stat-value">{{ stats.totalMessages ?? 0 }}</div>
            <div class="stat-label">总消息数</div>
          </el-card>
        </el-col>
        <el-col :xs="24" :sm="12" :md="8" :lg="6">
          <el-card shadow="hover" class="stat-card">
            <div class="stat-value">{{ stats.totalKnowledge ?? 0 }}</div>
            <div class="stat-label">知识库文档数</div>
          </el-card>
        </el-col>
        <el-col :xs="24" :sm="12" :md="8" :lg="6">
          <el-card shadow="hover" class="stat-card stat-card-token">
            <div class="stat-value">{{ formatNumber(stats.totalTokens) }}</div>
            <div class="stat-label">总 Token 消耗</div>
          </el-card>
        </el-col>
        <el-col :xs="24" :sm="12" :md="8" :lg="6">
          <el-card shadow="hover" class="stat-card stat-card-token">
            <div class="stat-value">{{ formatNumber(stats.todayTokens) }}</div>
            <div class="stat-label">今日 Token 消耗</div>
          </el-card>
        </el-col>
      </el-row>
      <el-card class="mt-6" shadow="hover">
        <template #header><span>按模型 Token 消耗</span></template>
        <el-empty v-if="!(stats.tokensByModel || []).length" description="暂无按模型统计数据（产生对话后将自动汇总）" />
        <el-table v-else :data="stats.tokensByModel" border>
          <el-table-column prop="modelName" label="模型" min-width="120" />
          <el-table-column prop="tokenCount" label="Token 数" width="140">
            <template #default="{ row }">{{ formatNumber(row.tokenCount) }}</template>
          </el-table-column>
        </el-table>
      </el-card>
      <el-row :gutter="16" class="mt-6">
        <el-col :xs="24" :md="12">
          <el-card shadow="hover">
            <template #header><span>按场景 Token 消耗（累计）</span></template>
            <el-empty v-if="!(tokensByScene || []).length" description="暂无累计场景数据" />
            <el-table v-else :data="tokensByScene" border size="small">
              <el-table-column prop="scene" label="场景" min-width="100">
                <template #default="{ row }">{{ sceneLabel(row.scene) }}</template>
              </el-table-column>
              <el-table-column prop="tokenCount" label="Token 数" width="140">
                <template #default="{ row }">{{ formatNumber(row.tokenCount) }}</template>
              </el-table-column>
            </el-table>
          </el-card>
        </el-col>
        <el-col :xs="24" :md="12">
          <el-card shadow="hover">
            <template #header><span>按场景 Token 消耗（今日）</span></template>
            <el-empty v-if="!(todayTokensByScene || []).length" description="今日暂无场景消耗记录" />
            <el-table v-else :data="todayTokensByScene" border size="small">
              <el-table-column prop="scene" label="场景" min-width="100">
                <template #default="{ row }">{{ sceneLabel(row.scene) }}</template>
              </el-table-column>
              <el-table-column prop="tokenCount" label="Token 数" width="140">
                <template #default="{ row }">{{ formatNumber(row.tokenCount) }}</template>
              </el-table-column>
            </el-table>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup name="aiStats">
import { ref, onMounted } from "vue";
import { Refresh } from "@element-plus/icons-vue";
import { ElMessage } from "element-plus";
import { getAiStatsApi } from "@/api/modules/ai";

const loading = ref(false);
const stats = ref({});
const tokensByScene = ref([]);
const todayTokensByScene = ref([]);
const lastLoadedText = ref("");

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

async function fetchStats() {
  loading.value = true;
  try {
    const res = await getAiStatsApi();
    const data = res.data || {};
    stats.value = data;
    tokensByScene.value = data.tokensByScene || [];
    todayTokensByScene.value = data.todayTokensByScene || [];
    lastLoadedText.value = new Date().toLocaleString();
  } catch (e) {
    stats.value = {};
    tokensByScene.value = [];
    todayTokensByScene.value = [];
    ElMessage.error(e?.msg || e?.message || "加载统计数据失败");
  } finally {
    loading.value = false;
  }
}

onMounted(fetchStats);
</script>

<style scoped>
.stats-grid {
  min-height: 200px;
}
.stat-card {
  margin-bottom: 16px;
}
.stat-value {
  font-size: 28px;
  font-weight: 600;
  color: #303133;
}
.stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 8px;
}
.stat-card-token .stat-value {
  color: #409eff;
}
</style>
