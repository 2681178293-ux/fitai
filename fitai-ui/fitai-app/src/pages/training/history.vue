<template>
  <view class="page">
    <view v-if="loading" class="loading">加载中...</view>
    <template v-else>
      <view class="stats-row">
        <view class="stat-box blue">
          <text class="stat-num">{{ stats.totalWorkouts }}</text>
          <text class="stat-label">训练次数</text>
        </view>
        <view class="stat-box green">
          <text class="stat-num">{{ formatDuration(stats.totalDuration) }}</text>
          <text class="stat-label">总时长</text>
        </view>
        <view class="stat-box orange">
          <text class="stat-num">{{ stats.totalCalories }}</text>
          <text class="stat-label">热量(kcal)</text>
        </view>
      </view>

      <view v-if="logs.length === 0" class="empty">暂无训练记录</view>
      <view v-for="log in logs" :key="String(log.id)" class="log-card">
        <view class="log-head">
          <view class="log-main">
            <text class="log-title">{{ log.exerciseName || '自由训练' }}</text>
            <view class="log-meta">
              <text>{{ formatDate(log.trainingDate) }}</text>
              <text>{{ log.duration || 0 }} 分钟</text>
              <text>{{ logCalories(log) }} kcal</text>
            </view>
          </view>
          <view v-if="log.sets" class="log-side">
            <text class="side-text">{{ log.sets }} 组 × {{ log.reps }} 次</text>
            <text v-if="log.weight" class="side-text">{{ log.weight }} kg</text>
          </view>
        </view>
        <view v-if="log.note" class="log-note">{{ log.note }}</view>
        <view v-if="log.id" class="log-actions">
          <view class="btn-review" @click="openReview(log.id)">AI 点评</view>
        </view>
      </view>
    </template>

    <!-- AI 点评弹层 -->
    <view v-if="reviewOpen" class="mask" @click="closeReview">
      <view class="review-sheet" @click.stop>
        <view class="review-title">AI 训练点评</view>
        <scroll-view scroll-y class="review-body">
          <view v-if="reviewLoading" class="review-loading">生成中...</view>
          <template v-else-if="reviewPayload">
            <view v-if="reviewPayload.overallScore != null" class="review-score">
              <text>综合评分 </text>
              <text class="score-val">{{ reviewPayload.overallScore }}</text>
            </view>
            <text v-if="reviewPayload.summary" class="review-summary">{{ reviewPayload.summary }}</text>
            <view v-if="asList(reviewPayload.highlights).length" class="review-block">
              <text class="block-title good">亮点</text>
              <text v-for="(line, i) in asList(reviewPayload.highlights)" :key="'h' + i" class="bullet">· {{ line }}</text>
            </view>
            <view v-if="asList(reviewPayload.improvements).length" class="review-block">
              <text class="block-title warn">可改进</text>
              <text v-for="(line, i) in asList(reviewPayload.improvements)" :key="'i' + i" class="bullet">· {{ line }}</text>
            </view>
            <view v-if="asList(reviewPayload.nextSuggestions).length" class="review-block">
              <text class="block-title tip">下次建议</text>
              <text v-for="(line, i) in asList(reviewPayload.nextSuggestions)" :key="'n' + i" class="bullet">· {{ line }}</text>
            </view>
            <view v-if="reviewPayload.encouragement" class="encourage">{{ reviewPayload.encouragement }}</view>
          </template>
          <view v-else class="review-empty">暂无点评内容</view>
        </scroll-view>
        <view class="review-footer" @click="closeReview">关闭</view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useUserStore } from '@/store/user'
import { getTrainingLogs } from '@/api/training'
import { reviewTraining } from '@/api/ai'
import { extractTrainingLogRecords, normalizeTrainingLog } from '@/utils/trainingLog'

const userStore = useUserStore()
const loading = ref(true)
const logs = ref([])
const stats = ref({ totalWorkouts: 0, totalDuration: 0, totalCalories: 0 })

const reviewOpen = ref(false)
const reviewLoading = ref(false)
const reviewPayload = ref(null)
const activeLogId = ref(null)

function logCalories(log) {
  if (log == null) return 0
  const c = log.caloriesBurned ?? log.calories
  return c != null ? Number(c) : 0
}

function formatDate(date) {
  if (!date) return ''
  return String(date).slice(0, 10)
}

function formatDuration(minutes) {
  if (!minutes) return '0分钟'
  if (minutes < 60) return `${minutes}分钟`
  const h = Math.floor(minutes / 60)
  const m = minutes % 60
  return m > 0 ? `${h}小时${m}分` : `${h}小时`
}

function asList(value) {
  if (Array.isArray(value)) {
    return value.map((x) => (typeof x === 'string' ? x : JSON.stringify(x)))
  }
  if (value == null || value === '') return []
  return [String(value)]
}

async function loadLogs() {
  loading.value = true
  try {
    const res = await getTrainingLogs({ pageNum: 1, pageSize: 100 })
    const page = res.data
    const records = extractTrainingLogRecords(page)
    logs.value = records.map((row) => normalizeTrainingLog(row))
    stats.value = {
      totalWorkouts: logs.value.length,
      totalDuration: logs.value.reduce((s, log) => s + (log.duration || 0), 0),
      totalCalories: logs.value.reduce((s, log) => s + logCalories(log), 0)
    }
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

function closeReview() {
  reviewOpen.value = false
  reviewPayload.value = null
  activeLogId.value = null
}

async function openReview(logId) {
  if (logId == null || logId === '') return
  activeLogId.value = logId
  reviewOpen.value = true
  reviewLoading.value = true
  reviewPayload.value = null
  try {
    const res = await reviewTraining({
      trainingLogId: logId,
      includeHistory: true
    })
    const data = res.data
    reviewPayload.value = data && typeof data === 'object' ? data : { summary: String(data ?? '') }
  } catch (e) {
    uni.showToast({ title: e.msg || 'AI 点评失败', icon: 'none' })
    closeReview()
  } finally {
    reviewLoading.value = false
  }
}

onMounted(() => {
  if (!userStore.isLoggedIn) {
    uni.navigateTo({ url: '/pages/login/index' })
    return
  }
  loadLogs()
})
</script>

<style lang="scss" scoped>
.page {
  min-height: 100vh;
  padding: 12px;
  padding-bottom: 24px;
  background: #f5f5f5;
}
.loading,
.empty {
  text-align: center;
  padding: 40px;
  color: #999;
}
.stats-row {
  display: flex;
  gap: 8px;
  margin-bottom: 12px;
}
.stat-box {
  flex: 1;
  border-radius: 10px;
  padding: 12px 8px;
  text-align: center;
  color: #fff;
}
.stat-box.blue {
  background: linear-gradient(135deg, #3b82f6, #2563eb);
}
.stat-box.green {
  background: linear-gradient(135deg, #22c55e, #16a34a);
}
.stat-box.orange {
  background: linear-gradient(135deg, #f97316, #ea580c);
}
.stat-num {
  display: block;
  font-size: 18px;
  font-weight: bold;
}
.stat-label {
  font-size: 11px;
  opacity: 0.9;
  margin-top: 4px;
}
.log-card {
  background: #fff;
  border-radius: 12px;
  padding: 14px;
  margin-bottom: 10px;
}
.log-head {
  display: flex;
  justify-content: space-between;
  gap: 8px;
}
.log-title {
  font-size: 15px;
  font-weight: 600;
  color: #111;
}
.log-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 6px;
  font-size: 12px;
  color: #6b7280;
}
.log-side {
  text-align: right;
}
.side-text {
  display: block;
  font-size: 12px;
  color: #6b7280;
}
.log-note {
  margin-top: 8px;
  padding: 8px;
  background: #f9fafb;
  border-radius: 8px;
  font-size: 12px;
  color: #6b7280;
}
.log-actions {
  margin-top: 10px;
}
.btn-review {
  display: inline-block;
  padding: 6px 14px;
  background: #eff6ff;
  color: #2563eb;
  border-radius: 8px;
  font-size: 13px;
}
.mask {
  position: fixed;
  left: 0;
  right: 0;
  top: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.45);
  z-index: 999;
  display: flex;
  align-items: flex-end;
  justify-content: center;
}
.review-sheet {
  width: 100%;
  max-height: 75vh;
  background: #fff;
  border-radius: 16px 16px 0 0;
  display: flex;
  flex-direction: column;
}
.review-title {
  padding: 16px;
  font-weight: bold;
  text-align: center;
  border-bottom: 1px solid #f3f4f6;
}
.review-body {
  flex: 1;
  max-height: 55vh;
  padding: 12px 16px;
}
.review-loading,
.review-empty {
  text-align: center;
  color: #999;
  padding: 24px;
}
.review-score {
  margin-bottom: 10px;
  font-size: 14px;
}
.score-val {
  color: #d97706;
  font-weight: bold;
}
.review-summary {
  font-size: 14px;
  color: #374151;
  line-height: 1.6;
  margin-bottom: 12px;
}
.review-block {
  margin-bottom: 12px;
}
.block-title {
  display: block;
  font-size: 13px;
  font-weight: 600;
  margin-bottom: 6px;
}
.block-title.good {
  color: #15803d;
}
.block-title.warn {
  color: #b45309;
}
.block-title.tip {
  color: #1d4ed8;
}
.bullet {
  display: block;
  font-size: 13px;
  color: #4b5563;
  line-height: 1.5;
  margin-bottom: 4px;
}
.encourage {
  margin-top: 8px;
  padding: 10px;
  background: #f5f3ff;
  color: #6d28d9;
  border-radius: 8px;
  font-size: 13px;
}
.review-footer {
  padding: 14px;
  text-align: center;
  color: #2563eb;
  font-weight: 600;
  border-top: 1px solid #f3f4f6;
}
</style>
