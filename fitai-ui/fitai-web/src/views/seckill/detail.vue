<script setup>
import { ref, onMounted, onUnmounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getActivityDetail, doSeckill, paySeckillOrder } from '@/api/seckill'
import { getPcPayReturnBaseUrl } from '@/utils/payReturnUrl'
import { useUserStore } from '@/stores/user'
import { Timer, ShoppingCart, Clock } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)
const rushing = ref(false)
const activity = ref(null)
const selectedSku = ref(null)
const orderNo = ref(null)
const showPayDialog = ref(false)
const payCountdown = ref(15 * 60) // 15分钟支付倒计时

let countdownTimer = null
let payTimer = null

// 加载活动详情
const loadActivityDetail = async () => {
  const activityId = route.params.id
  loading.value = true
  try {
    const data = await getActivityDetail(activityId)
    activity.value = data
    if (data?.skuList && data.skuList.length > 0) {
      selectedSku.value = data.skuList[0]
    }
  } catch (error) {
    console.error('加载活动详情失败:', error)
    // 错误已由 request 拦截器统一提示
  } finally {
    loading.value = false
  }
}

// 获取活动状态
const activityStatus = computed(() => {
  if (!activity.value) return 'loading'
  const now = new Date().getTime()
  const start = new Date(activity.value.startTime).getTime()
  const end = new Date(activity.value.endTime).getTime()
  
  if (now < start) return 'waiting'
  if (now < end) return 'active'
  return 'ended'
})

// 倒计时
const countdown = computed(() => {
  if (!activity.value) return { hours: 0, minutes: 0, seconds: 0 }
  
  const now = new Date().getTime()
  const start = new Date(activity.value.startTime).getTime()
  const end = new Date(activity.value.endTime).getTime()
  
  let diff = 0
  if (now < start) {
    diff = start - now
  } else if (now < end) {
    diff = end - now
  }
  
  return {
    hours: Math.floor(diff / (1000 * 60 * 60)),
    minutes: Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60)),
    seconds: Math.floor((diff % (1000 * 60)) / 1000)
  }
})

// 选择商品
const selectSku = (sku) => {
  selectedSku.value = sku
}

// 执行秒杀
const handleRush = async () => {
  if (rushing.value) return
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }

  if (!selectedSku.value) {
    ElMessage.warning('请选择商品')
    return
  }

  if (selectedSku.value.soldOut) {
    ElMessage.warning('商品已售罄')
    return
  }

  if (activityStatus.value !== 'active') {
    ElMessage.warning('活动未开始或已结束')
    return
  }

  rushing.value = true
  try {
    const data = await doSeckill({
      activityId: activity.value.id,
      seckillSkuId: selectedSku.value.id
    })
    
    orderNo.value = data
    ElMessage.success('秒杀成功！')
    
    // 显示支付弹窗
    showPayDialog.value = true
    startPayCountdown()
    
    // 刷新页面数据
    loadActivityDetail()
    
  } catch {
    // 错误已由 request 拦截器统一提示，此处不再重复弹窗
  } finally {
    rushing.value = false
  }
}

// 开始支付倒计时
const startPayCountdown = () => {
  payCountdown.value = 15 * 60
  payTimer = setInterval(() => {
    payCountdown.value--
    if (payCountdown.value <= 0) {
      clearInterval(payTimer)
      showPayDialog.value = false
      ElMessage.warning('订单已超时取消')
    }
  }, 1000)
}

// 格式化支付倒计时
const formatPayCountdown = computed(() => {
  const minutes = Math.floor(payCountdown.value / 60)
  const seconds = payCountdown.value % 60
  return `${String(minutes).padStart(2, '0')}:${String(seconds).padStart(2, '0')}`
})

// 支付订单
const handlePay = async () => {
  try {
    const result = await paySeckillOrder(orderNo.value, getPcPayReturnBaseUrl())
    
    // 如果返回支付表单，跳转到支付宝
    if (result && result.payForm) {
      showPayDialog.value = false
      if (payTimer) clearInterval(payTimer)
      
      // 创建临时表单提交到支付宝
      const div = document.createElement('div')
      div.innerHTML = result.payForm
      document.body.appendChild(div)
      document.forms[document.forms.length - 1].submit()
    } else {
      ElMessage.success('支付成功！')
      showPayDialog.value = false
      if (payTimer) clearInterval(payTimer)
      router.push('/order')
    }
  } catch (error) {
    ElMessage.error(error.message || '支付失败')
  }
}

// 关闭支付弹窗
const closePayDialog = () => {
  ElMessageBox.confirm('确定放弃支付吗？订单将在15分钟后自动取消', '提示', {
    confirmButtonText: '继续支付',
    cancelButtonText: '放弃支付',
    type: 'warning'
  }).then(() => {
    // 继续支付
  }).catch(() => {
    showPayDialog.value = false
    if (payTimer) clearInterval(payTimer)
  })
}

// 刷新倒计时显示
const refreshCountdown = () => {
  // 触发响应式更新
  if (activity.value) {
    activity.value = { ...activity.value }
  }
}

onMounted(() => {
  loadActivityDetail()
  countdownTimer = setInterval(refreshCountdown, 1000)
})

onUnmounted(() => {
  if (countdownTimer) clearInterval(countdownTimer)
  if (payTimer) clearInterval(payTimer)
})
</script>

<template>
  <div class="seckill-detail-page" v-loading="loading">
    <template v-if="activity">
      <!-- 活动信息 -->
      <div class="activity-banner bg-gradient-to-r from-red-500 to-orange-500 text-white p-6 rounded-lg">
        <h1 class="text-2xl font-bold">{{ activity.name }}</h1>
        <p class="mt-2 opacity-90">{{ activity.description }}</p>
        
        <!-- 倒计时 -->
        <div class="countdown-box mt-4 flex items-center gap-4">
          <el-icon><Timer /></el-icon>
          <span v-if="activityStatus === 'waiting'">距开始：</span>
          <span v-else-if="activityStatus === 'active'">距结束：</span>
          <span v-else>已结束</span>
          
          <template v-if="activityStatus !== 'ended'">
            <div class="flex gap-2">
              <span class="countdown-item">{{ String(countdown.hours).padStart(2, '0') }}</span>
              <span>:</span>
              <span class="countdown-item">{{ String(countdown.minutes).padStart(2, '0') }}</span>
              <span>:</span>
              <span class="countdown-item">{{ String(countdown.seconds).padStart(2, '0') }}</span>
            </div>
          </template>
        </div>
      </div>

      <!-- 商品列表 -->
      <div class="product-list mt-6">
        <h2 class="text-lg font-bold mb-4">秒杀商品</h2>
        
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div 
            v-for="sku in activity.skuList" 
            :key="sku.id"
            :class="['product-card', { 'selected': selectedSku?.id === sku.id, 'sold-out': sku.soldOut }]"
            @click="selectSku(sku)"
          >
            <div class="flex gap-4">
              <div class="relative">
                <img :src="sku.skuImage || '/placeholder.png'" :alt="sku.skuName" class="w-32 h-32 object-cover rounded">
                <span class="absolute top-1 left-1 bg-red-500 text-white text-xs px-2 py-0.5 rounded">
                  {{ sku.discount }}折
                </span>
              </div>
              <div class="flex-1">
                <h3 class="font-medium">{{ sku.skuName }}</h3>
                <div class="flex items-baseline gap-2 mt-2">
                  <span class="text-red-500 text-xl font-bold">¥{{ sku.seckillPrice }}</span>
                  <span class="text-gray-400 text-sm line-through">¥{{ sku.originalPrice }}</span>
                </div>
                <p class="text-sm text-gray-500 mt-1">限购 {{ sku.limitPerUser }} 件</p>
                
                <!-- 进度条 -->
                <div class="mt-3">
                  <el-progress 
                    :percentage="sku.soldPercent" 
                    :show-text="false"
                    :stroke-width="10"
                    color="#ef4444"
                  />
                  <div class="flex justify-between text-xs text-gray-500 mt-1">
                    <span>已抢 {{ sku.soldPercent }}%</span>
                    <span>剩余 {{ sku.availableStock }} 件</span>
                  </div>
                </div>
              </div>
            </div>

            <!-- 已抢光遮罩 -->
            <div v-if="sku.soldOut" class="sold-out-mask">
              <span>已抢光</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 底部操作栏 -->
      <div class="fixed bottom-0 left-0 right-0 bg-white border-t p-4 flex justify-between items-center">
        <div v-if="selectedSku">
          <p class="text-sm text-gray-500">已选：{{ selectedSku.skuName }}</p>
          <p class="text-red-500 text-xl font-bold">¥{{ selectedSku.seckillPrice }}</p>
        </div>
        <el-button 
          type="danger" 
          size="large"
          :loading="rushing"
          :disabled="!selectedSku || selectedSku.soldOut || activityStatus !== 'active'"
          @click="handleRush"
        >
          <template v-if="activityStatus === 'waiting'">即将开始</template>
          <template v-else-if="activityStatus === 'ended'">已结束</template>
          <template v-else-if="selectedSku?.soldOut">已抢光</template>
          <template v-else>立即抢购</template>
        </el-button>
      </div>
    </template>

    <!-- 支付弹窗 -->
    <el-dialog
      v-model="showPayDialog"
      title="秒杀成功"
      width="400px"
      :close-on-click-modal="false"
      :before-close="closePayDialog"
    >
      <div class="text-center">
        <el-icon class="text-6xl text-green-500 mb-4"><ShoppingCart /></el-icon>
        <p class="text-lg font-bold">恭喜您抢购成功！</p>
        <p class="text-gray-500 mt-2">请在 {{ formatPayCountdown }} 内完成支付</p>
        
        <div class="mt-4 p-4 bg-gray-50 rounded">
          <p class="text-sm text-gray-500">订单号</p>
          <p class="font-mono">{{ orderNo }}</p>
          <p class="text-red-500 text-xl font-bold mt-2">¥{{ selectedSku?.seckillPrice }}</p>
        </div>
      </div>
      
      <template #footer>
        <el-button @click="closePayDialog">稍后支付</el-button>
        <el-button type="primary" @click="handlePay">立即支付</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.seckill-detail-page {
  padding: 20px;
  padding-bottom: 100px;
  max-width: 800px;
  margin: 0 auto;
}

.countdown-item {
  background: rgba(255, 255, 255, 0.2);
  padding: 4px 8px;
  border-radius: 4px;
  font-family: monospace;
  font-size: 1.25rem;
  font-weight: bold;
}

.product-card {
  position: relative;
  padding: 16px;
  border: 2px solid #e5e7eb;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s;
}

.product-card:hover {
  border-color: #f87171;
}

.product-card.selected {
  border-color: #ef4444;
  background: #fef2f2;
}

.product-card.sold-out {
  opacity: 0.6;
  cursor: not-allowed;
}

.sold-out-mask {
  position: absolute;
  inset: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 10px;
}

.sold-out-mask span {
  color: white;
  font-size: 1.5rem;
  font-weight: bold;
}
</style>
