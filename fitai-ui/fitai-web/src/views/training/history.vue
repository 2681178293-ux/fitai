<script setup>
import { ref, onMounted } from 'vue'
import { getTrainingLogs } from '@/api/training'
import { extractTrainingLogRecords, normalizeTrainingLog } from '@/utils/trainingLog'
import { Calendar, Timer, Sunny, ChatDotRound } from '@element-plus/icons-vue'
import AiReviewDialog from '@/components/training/AiReviewDialog.vue'

const loading = ref(false)
const logs = ref([])
const aiReviewOpen = ref(false)
const aiReviewLogId = ref(null)
const stats = ref({
  totalWorkouts: 0,
  totalDuration: 0,
  totalCalories: 0
})

// 加载训练记录
const loadLogs = async () => {
  loading.value = true
  try {
    const page = await getTrainingLogs({ pageNum: 1, pageSize: 100 })
    const records = extractTrainingLogRecords(page)
    logs.value = records.map((row) => normalizeTrainingLog(row))
    
    // 计算统计数据
    stats.value = {
      totalWorkouts: logs.value.length,
      totalDuration: logs.value.reduce((sum, log) => sum + (log.duration || 0), 0),
      totalCalories: logs.value.reduce((sum, log) => sum + (log.caloriesBurned || 0), 0)
    }
  } catch (error) {
    console.error('加载训练记录失败:', error)
  } finally {
    loading.value = false
  }
}

// 格式化日期
const formatDate = (date) => {
  if (!date) return ''
  return new Date(date).toLocaleDateString()
}

// 格式化时长
const formatDuration = (minutes) => {
  if (!minutes) return '0分钟'
  if (minutes < 60) return `${minutes}分钟`
  const hours = Math.floor(minutes / 60)
  const mins = minutes % 60
  return mins > 0 ? `${hours}小时${mins}分钟` : `${hours}小时`
}

function openAiReview(logId) {
  if (!logId) return
  aiReviewLogId.value = logId
  aiReviewOpen.value = true
}

onMounted(() => {
  loadLogs()
})
</script>

<template>
  <div class="training-history-page p-4">
    <h1 class="text-xl font-bold mb-4 flex items-center gap-2">
      <el-icon class="text-orange-500"><Sunny /></el-icon>
      训练记录
    </h1>

    <!-- 统计卡片 -->
    <div class="stats-grid grid grid-cols-3 gap-4 mb-6">
      <div class="stat-card bg-gradient-to-r from-blue-500 to-blue-600 text-white rounded-lg p-4 text-center">
        <div class="text-3xl font-bold">{{ stats.totalWorkouts }}</div>
        <div class="text-sm opacity-90">训练次数</div>
      </div>
      <div class="stat-card bg-gradient-to-r from-green-500 to-green-600 text-white rounded-lg p-4 text-center">
        <div class="text-3xl font-bold">{{ formatDuration(stats.totalDuration) }}</div>
        <div class="text-sm opacity-90">总时长</div>
      </div>
      <div class="stat-card bg-gradient-to-r from-orange-500 to-red-500 text-white rounded-lg p-4 text-center">
        <div class="text-3xl font-bold">{{ stats.totalCalories }}</div>
        <div class="text-sm opacity-90">消耗热量(kcal)</div>
      </div>
    </div>

    <!-- 记录列表 -->
    <div v-loading="loading" class="logs-list">
      <el-empty v-if="!loading && logs.length === 0" description="暂无训练记录" />
      
      <div class="timeline">
        <div 
          v-for="log in logs" 
          :key="log.id"
          class="log-item bg-white rounded-lg p-4 mb-4 shadow-sm"
        >
          <div class="flex justify-between items-start">
            <div>
              <h3 class="font-medium">{{ log.exerciseName || '自由训练' }}</h3>
              <div class="flex items-center gap-4 text-sm text-gray-500 mt-2">
                <span class="flex items-center gap-1">
                  <el-icon><Calendar /></el-icon>
                  {{ formatDate(log.trainingDate) }}
                </span>
                <span class="flex items-center gap-1">
                  <el-icon><Timer /></el-icon>
                  {{ log.duration || 0 }}分钟
                </span>
                <span class="flex items-center gap-1">
                  <el-icon><Sunny /></el-icon>
                  {{ log.caloriesBurned || 0 }}kcal
                </span>
              </div>
            </div>
            <div class="text-right flex flex-col items-end gap-2">
              <div v-if="log.sets" class="text-sm text-gray-600">
                {{ log.sets }}组 x {{ log.reps }}次
              </div>
              <div v-if="log.weight" class="text-sm text-gray-600">
                {{ log.weight }}kg
              </div>
              <el-button
                v-if="log.id"
                type="primary"
                plain
                size="small"
                @click="openAiReview(log.id)"
              >
                <el-icon class="mr-1"><ChatDotRound /></el-icon>
                AI 点评
              </el-button>
            </div>
          </div>
          <div v-if="log.note" class="mt-2 text-sm text-gray-500 bg-gray-50 p-2 rounded">
            {{ log.note }}
          </div>
        </div>
      </div>
    </div>

    <AiReviewDialog v-model="aiReviewOpen" :training-log-id="aiReviewLogId" />
  </div>
</template>
