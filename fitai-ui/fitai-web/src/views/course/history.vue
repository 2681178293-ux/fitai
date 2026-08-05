<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getUserHistory } from '@/api/course'
import { VideoPlay, Clock, Check } from '@element-plus/icons-vue'

const router = useRouter()
const loading = ref(false)
const records = ref([])

// 加载学习记录
const loadHistory = async () => {
  loading.value = true
  try {
    const { data } = await getUserHistory()
    records.value = data || []
  } catch (error) {
    console.error('加载学习记录失败:', error)
  } finally {
    loading.value = false
  }
}

// 继续学习
const continueLearning = (record) => {
  router.push(`/course/${record.courseId}`)
}

// 格式化时间
const formatTime = (time) => {
  if (!time) return ''
  return new Date(time).toLocaleString()
}

// 格式化进度
const formatProgress = (progress) => {
  if (!progress) return 0
  return Math.min(100, Math.round(progress))
}

onMounted(() => {
  loadHistory()
})
</script>

<template>
  <div class="history-page p-4">
    <h1 class="text-xl font-bold mb-4 flex items-center gap-2">
      <el-icon class="text-blue-500"><VideoPlay /></el-icon>
      学习记录
    </h1>

    <div v-loading="loading" class="records-list">
      <el-empty v-if="!loading && records.length === 0" description="暂无学习记录" />
      
      <div 
        v-for="record in records" 
        :key="record.id"
        class="record-card bg-white rounded-lg p-4 mb-4 shadow-sm flex gap-4 cursor-pointer hover:shadow-md transition-shadow"
        @click="continueLearning(record)"
      >
        <img 
          :src="record.courseCover || '/placeholder.png'" 
          :alt="record.courseName"
          class="w-32 h-20 object-cover rounded"
        />
        <div class="flex-1">
          <h3 class="font-medium line-clamp-1">{{ record.courseName }}</h3>
          <div class="flex items-center gap-2 text-sm text-gray-500 mt-1">
            <el-icon><Clock /></el-icon>
            <span>{{ formatTime(record.lastWatchTime) }}</span>
          </div>
          <div class="mt-2">
            <div class="flex items-center gap-2">
              <el-progress 
                :percentage="formatProgress(record.progress)" 
                :show-text="false"
                :stroke-width="6"
                class="flex-1"
              />
              <span class="text-sm text-gray-500">{{ formatProgress(record.progress) }}%</span>
              <el-tag v-if="record.completed" size="small" type="success">
                <el-icon><Check /></el-icon> 已完成
              </el-tag>
            </div>
          </div>
        </div>
        <el-button type="primary" text>
          {{ record.completed ? '再次观看' : '继续学习' }}
        </el-button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.line-clamp-1 {
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>
