<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getOrderById, createPayOrder, cancelOrder, confirmReceive } from '@/api/order'
import { getPcPayReturnBaseUrl } from '@/utils/payReturnUrl'
import { Location, Timer, Check, Close, Box } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const order = ref(null)
const payCountdown = ref(0)

let countdownTimer = null

// 订单状态配置
const statusConfig = {
  unpaid: { label: '待支付', color: 'warning', icon: Timer },
  paid: { label: '待发货', color: 'primary', icon: Box },
  shipped: { label: '待收货', color: 'primary', icon: Box },
  received: { label: '已完成', color: 'success', icon: Check },
  cancelled: { label: '已取消', color: 'info', icon: Close }
}

// 当前状态配置
const currentStatus = computed(() => {
  if (!order.value) return statusConfig.unpaid
  return statusConfig[order.value.status] || statusConfig.unpaid
})

// 加载订单详情
const loadOrderDetail = async () => {
  const orderId = route.params.id
  loading.value = true
  try {
    const data = await getOrderById(orderId)
    order.value = data
    
    // 如果是待支付订单，计算倒计时
    if (data.status === 'unpaid') {
      calculatePayCountdown()
    }
  } catch (error) {
    console.error('加载订单详情失败:', error)
    ElMessage.error('加载失败')
  } finally {
    loading.value = false
  }
}

// 计算支付倒计时（30分钟）
const calculatePayCountdown = () => {
  if (!order.value) return
  
  const createTime = new Date(order.value.createTime).getTime()
  const expireTime = createTime + 30 * 60 * 1000 // 30分钟
  const now = Date.now()
  
  payCountdown.value = Math.max(0, Math.floor((expireTime - now) / 1000))
  
  if (payCountdown.value > 0) {
    countdownTimer = setInterval(() => {
      payCountdown.value--
      if (payCountdown.value <= 0) {
        clearInterval(countdownTimer)
        // 刷新订单状态
        loadOrderDetail()
      }
    }, 1000)
  }
}

// 格式化倒计时
const formatCountdown = computed(() => {
  const minutes = Math.floor(payCountdown.value / 60)
  const seconds = payCountdown.value % 60
  return `${String(minutes).padStart(2, '0')}:${String(seconds).padStart(2, '0')}`
})

// 支付订单
const handlePay = async () => {
  try {
    // 调用支付接口获取支付表单
    const data = await createPayOrder({
      orderNo: order.value.orderNo,
      orderType: 'product',
      amount: order.value.payAmount,
      payType: 'alipay',
      subject: `商品订单-${order.value.orderNo}`,
      returnUrl: getPcPayReturnBaseUrl()
    })
    
    if (data.payForm) {
      // 打开新窗口并写入支付表单
      const payWindow = window.open('', '_blank')
      if (payWindow) {
        payWindow.document.write(data.payForm)
        payWindow.document.close()
        
        // 提示用户在新窗口完成支付
        ElMessage.info('请在新窗口完成支付')
        
        // 定时轮询支付状态
        const pollInterval = setInterval(async () => {
          try {
            await loadOrderDetail()
            if (order.value.status !== 'unpaid') {
              clearInterval(pollInterval)
              ElMessage.success('支付成功')
            }
          } catch (e) {
            console.error('轮询支付状态失败', e)
          }
        }, 3000) // 每3秒轮询一次
        
        // 60秒后停止轮询
        setTimeout(() => {
          clearInterval(pollInterval)
        }, 60000)
      } else {
        ElMessage.error('无法打开支付窗口，请检查浏览器是否拦截了弹窗')
      }
    }
  } catch (error) {
    ElMessage.error(error.message || '创建支付订单失败')
  }
}

// 取消订单
const handleCancel = async () => {
  try {
    await ElMessageBox.confirm('确定要取消订单吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await cancelOrder(order.value.id)
    ElMessage.success('订单已取消')
    loadOrderDetail()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '取消失败')
    }
  }
}

// 确认收货
const handleConfirm = async () => {
  try {
    await ElMessageBox.confirm('确认已收到商品？', '提示', {
      confirmButtonText: '确认收货',
      cancelButtonText: '取消',
      type: 'info'
    })
    
    await confirmReceive(order.value.id)
    ElMessage.success('已确认收货')
    loadOrderDetail()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '操作失败')
    }
  }
}

onMounted(() => {
  loadOrderDetail()
})

onUnmounted(() => {
  if (countdownTimer) {
    clearInterval(countdownTimer)
  }
})
</script>

<template>
  <div class="order-detail-page" v-loading="loading">
    <template v-if="order">
      <!-- 订单状态 -->
      <div class="status-card bg-gradient-to-r from-blue-500 to-blue-600 text-white p-6 rounded-lg">
        <div class="flex items-center gap-3">
          <el-icon class="text-3xl"><component :is="currentStatus.icon" /></el-icon>
          <div>
            <h2 class="text-xl font-bold">{{ currentStatus.label }}</h2>
            <p v-if="order.status === 'unpaid' && payCountdown > 0" class="text-sm opacity-90 mt-1">
              请在 {{ formatCountdown }} 内完成支付
            </p>
          </div>
        </div>
      </div>

      <!-- 收货地址 -->
      <div class="address-card bg-white rounded-lg p-4 mt-4 shadow-sm">
        <div class="flex items-start gap-3">
          <el-icon class="text-xl text-gray-400 mt-1"><Location /></el-icon>
          <div>
            <div class="flex items-center gap-2">
              <span class="font-medium">{{ order.addressSnapshot?.name }}</span>
              <span class="text-gray-500">{{ order.addressSnapshot?.phone }}</span>
            </div>
            <p class="text-gray-600 mt-1">
              {{ order.addressSnapshot?.province }}{{ order.addressSnapshot?.city }}{{ order.addressSnapshot?.district }}{{ order.addressSnapshot?.detail }}
            </p>
          </div>
        </div>
      </div>

      <!-- 商品列表 -->
      <div class="products-card bg-white rounded-lg p-4 mt-4 shadow-sm">
        <h3 class="font-medium mb-3">商品信息</h3>
        
        <div v-for="item in order.items" :key="item.id" class="product-item flex gap-3 py-3 border-b last:border-b-0">
          <img :src="item.skuImage || '/placeholder.png'" :alt="item.skuName" class="w-20 h-20 object-cover rounded">
          <div class="flex-1">
            <h4 class="font-medium line-clamp-2">{{ item.skuName }}</h4>
            <p v-if="item.specs" class="text-sm text-gray-500 mt-1">
              {{ typeof item.specs === 'object' ? Object.values(item.specs).join(' / ') : item.specs }}
            </p>
            <div class="flex justify-between items-center mt-2">
              <span class="text-red-500">¥{{ item.price }}</span>
              <span class="text-gray-500">x{{ item.quantity }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 订单信息 -->
      <div class="info-card bg-white rounded-lg p-4 mt-4 shadow-sm">
        <h3 class="font-medium mb-3">订单信息</h3>
        
        <div class="info-row flex justify-between py-2">
          <span class="text-gray-500">订单编号</span>
          <span class="font-mono">{{ order.orderNo }}</span>
        </div>
        <div class="info-row flex justify-between py-2">
          <span class="text-gray-500">创建时间</span>
          <span>{{ order.createTime }}</span>
        </div>
        <div v-if="order.payTime" class="info-row flex justify-between py-2">
          <span class="text-gray-500">支付时间</span>
          <span>{{ order.payTime }}</span>
        </div>
        <div v-if="order.remark" class="info-row flex justify-between py-2">
          <span class="text-gray-500">订单备注</span>
          <span>{{ order.remark }}</span>
        </div>
      </div>

      <!-- 金额信息 -->
      <div class="amount-card bg-white rounded-lg p-4 mt-4 shadow-sm">
        <div class="flex justify-between py-2">
          <span class="text-gray-500">商品金额</span>
          <span>¥{{ order.totalAmount }}</span>
        </div>
        <div class="flex justify-between py-2">
          <span class="text-gray-500">运费</span>
          <span>¥{{ order.freightAmount || '0.00' }}</span>
        </div>
        <div v-if="order.couponAmount > 0" class="flex justify-between py-2">
          <span class="text-gray-500">优惠券</span>
          <span class="text-red-500">-¥{{ order.couponAmount }}</span>
        </div>
        <div class="flex justify-between py-3 border-t mt-2">
          <span class="font-medium">实付金额</span>
          <span class="text-red-500 text-xl font-bold">¥{{ order.payAmount }}</span>
        </div>
      </div>

      <!-- 订单操作：紧跟金额明细下方，避免固定底栏遮挡与视觉下沉 -->
      <div class="action-card bg-white rounded-lg p-4 mt-4 shadow-sm">
        <div class="flex flex-wrap justify-end gap-3">
          <template v-if="order.status === 'unpaid'">
            <el-button @click="handleCancel">取消订单</el-button>
            <el-button type="danger" @click="handlePay">立即支付</el-button>
          </template>
          <template v-else-if="order.status === 'shipped'">
            <el-button type="primary" @click="handleConfirm">确认收货</el-button>
          </template>
          <template v-else>
            <el-button @click="router.push('/mall')">继续购物</el-button>
          </template>
        </div>
      </div>
    </template>
  </div>
</template>

<style scoped>
.order-detail-page {
  padding: 16px;
  padding-bottom: 24px;
  max-width: 600px;
  margin: 0 auto;
  background: #f5f5f5;
  min-height: 100vh;
}

.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>
