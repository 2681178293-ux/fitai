<script setup>
import { ref, onMounted, onUnmounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { getActiveActivities } from '@/api/seckill'
import { Timer, ShoppingCart } from '@element-plus/icons-vue'

const router = useRouter()
const loading = ref(false)
const activities = ref([])

// 倒计时定时器
let countdownTimer = null

// 加载活动列表
const loadActivities = async () => {
  loading.value = true
  try {
    const data = await getActiveActivities()
    activities.value = data || []
  } catch (error) {
    console.error('加载秒杀活动失败:', error)
  } finally {
    loading.value = false
  }
}

// 计算倒计时
const getCountdown = (activity) => {
  const now = new Date().getTime()
  const start = new Date(activity.startTime).getTime()
  const end = new Date(activity.endTime).getTime()
  
  if (now < start) {
    // 未开始，计算距离开始的时间
    return { type: 'start', diff: start - now }
  } else if (now < end) {
    // 进行中，计算距离结束的时间
    return { type: 'end', diff: end - now }
  }
  return { type: 'ended', diff: 0 }
}

// 格式化倒计时
const formatCountdown = (diff) => {
  if (diff <= 0) return '00:00:00'
  
  const hours = Math.floor(diff / (1000 * 60 * 60))
  const minutes = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60))
  const seconds = Math.floor((diff % (1000 * 60)) / 1000)
  
  return `${String(hours).padStart(2, '0')}:${String(minutes).padStart(2, '0')}:${String(seconds).padStart(2, '0')}`
}

// 跳转详情
const goToDetail = (activityId) => {
  router.push(`/seckill/${activityId}`)
}

// 刷新倒计时
const refreshCountdown = () => {
  // 触发响应式更新
  activities.value = [...activities.value]
}

onMounted(() => {
  loadActivities()
  // 每秒更新倒计时
  countdownTimer = setInterval(refreshCountdown, 1000)
})

onUnmounted(() => {
  if (countdownTimer) {
    clearInterval(countdownTimer)
  }
})
</script>

<template>
  <div class="seckill-page">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1 class="text-2xl font-bold flex items-center gap-2">
        <el-icon class="text-red-500"><Timer /></el-icon>
        限时秒杀
      </h1>
      <p class="text-gray-500 mt-1">超值优惠，限时抢购</p>
    </div>

    <!-- 活动列表 -->
    <div v-loading="loading" class="activities-list mt-6">
      <el-empty v-if="!loading && activities.length === 0" description="暂无秒杀活动" />
      
      <div v-for="activity in activities" :key="activity.id" class="activity-card mb-6">
        <!-- 活动头部 -->
        <div class="activity-header flex justify-between items-center p-4 bg-gradient-to-r from-red-500 to-orange-500 text-white rounded-t-lg">
          <div>
            <h2 class="text-lg font-bold">{{ activity.name }}</h2>
            <p class="text-sm opacity-90">{{ activity.description }}</p>
          </div>
          <div class="text-right">
            <template v-if="getCountdown(activity).type === 'start'">
              <p class="text-sm">距开始</p>
              <p class="text-xl font-mono font-bold">{{ formatCountdown(getCountdown(activity).diff) }}</p>
            </template>
            <template v-else-if="getCountdown(activity).type === 'end'">
              <p class="text-sm">距结束</p>
              <p class="text-xl font-mono font-bold">{{ formatCountdown(getCountdown(activity).diff) }}</p>
            </template>
            <template v-else>
              <p class="text-lg">已结束</p>
            </template>
          </div>
        </div>

        <!-- 商品列表 -->
        <div class="activity-products p-4 bg-white rounded-b-lg shadow">
          <div class="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4">
            <div 
              v-for="sku in activity.skuList" 
              :key="sku.id" 
              class="product-item cursor-pointer hover:shadow-lg transition-shadow rounded-lg overflow-hidden border"
              @click="goToDetail(activity.id)"
            >
              <div class="relative">
                <img :src="sku.skuImage || '/placeholder.png'" :alt="sku.skuName" class="w-full h-36 object-cover">
                <!-- 折扣标签 -->
                <span class="absolute top-2 left-2 bg-red-500 text-white text-xs px-2 py-1 rounded">
                  {{ sku.discount }}折
                </span>
                <!-- 已抢光标签 -->
                <div v-if="sku.soldOut" class="absolute inset-0 bg-black/50 flex items-center justify-center">
                  <span class="text-white text-lg font-bold">已抢光</span>
                </div>
              </div>
              <div class="p-3">
                <h3 class="text-sm font-medium line-clamp-2 h-10">{{ sku.skuName }}</h3>
                <div class="flex items-baseline gap-2 mt-2">
                  <span class="text-red-500 text-lg font-bold">¥{{ sku.seckillPrice }}</span>
                  <span class="text-gray-400 text-xs line-through">¥{{ sku.originalPrice }}</span>
                </div>
                <!-- 进度条 -->
                <div class="mt-2">
                  <el-progress 
                    :percentage="sku.soldPercent" 
                    :show-text="false"
                    :stroke-width="8"
                    color="#ef4444"
                  />
                  <p class="text-xs text-gray-500 mt-1">
                    已抢 {{ sku.soldPercent }}%
                  </p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.seckill-page {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.activity-card {
  border-radius: 8px;
  overflow: hidden;
}

.product-item {
  background: white;
}

.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>
