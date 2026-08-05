<template>
  <view class="page">
    <view v-if="loading" class="loading-wrap">
      <text>正在查询支付结果...</text>
    </view>
    <view v-else class="result-card">
      <view :class="['icon-wrap', statusClass]">
        <text class="icon-text">{{ statusIcon }}</text>
      </view>
      <text class="status-title">{{ statusTitle }}</text>
      <view v-if="payResult" class="pay-info">
        <text v-if="payResult.transactionNo">流水号：{{ payResult.transactionNo }}</text>
        <text v-if="payResult.tradeNo">支付宝交易号：{{ payResult.tradeNo }}</text>
      </view>
      <view class="actions">
        <view class="btn secondary" @click="goToMall">继续购物</view>
        <view class="btn primary" @click="goToOrder">{{ orderTypeLabel }}订单</view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import http from '@/utils/request'
import { getUserInfo } from '@/api/user'
import { useUserStore } from '@/store/user'

const userStore = useUserStore()
const loading = ref(true)
const payResult = ref(null)
const status = ref(0) // 0 待支付 1 已支付 2 失败
const routeOptions = ref({})
const orderTypeFromRoute = ref('product')

const orderTypeLabel = computed(() => {
  const t = orderTypeFromRoute.value
  if (t === 'member') return '查看会员'
  return '查看'
})

onLoad((options) => {
  routeOptions.value = options || {}
  if (options.orderType) {
    orderTypeFromRoute.value = decodeURIComponent(options.orderType)
  }
})

const statusClass = computed(() => {
  if (status.value === 1) return 'success'
  if (status.value === 0) return 'pending'
  return 'failed'
})

const statusIcon = computed(() => {
  if (status.value === 1) return '✓'
  if (status.value === 0) return '...'
  return '✕'
})

const statusTitle = computed(() => {
  if (status.value === 1) return '支付成功'
  if (status.value === 0) return '支付处理中'
  return '支付失败'
})

onMounted(async () => {
  const opts = routeOptions.value
  if (Object.keys(opts).length === 0) {
    loading.value = false
    return
  }
  try {
    const queryStr = Object.keys(opts)
      .map(k => `${encodeURIComponent(k)}=${encodeURIComponent(opts[k])}`)
      .join('&')
    const res = await http.get(`/pay/return/alipay?${queryStr}`)
    const data = res.data || {}
    payResult.value = data
    status.value = data.status ?? 0
    if (status.value === 1) {
      try {
        const u = await getUserInfo()
        if (u.data) {
          userStore.setUserInfo(u.data)
        }
      } catch (e) {}
    }
  } catch (e) {
    status.value = 2
  }
  loading.value = false
})

function goToMall() {
  uni.switchTab({ url: '/pages/shop/index' })
}

function goToOrder() {
  if (orderTypeFromRoute.value === 'member') {
    uni.navigateTo({ url: '/pages/my/member-orders' })
    return
  }
  uni.navigateTo({ url: '/pages/order/list' })
}
</script>

<style lang="scss" scoped>
.page {
  min-height: 100vh;
  background: #f5f5f5;
  padding: 40px 20px;
}

.loading-wrap {
  padding: 60px;
  text-align: center;
  color: #999;
}

.result-card {
  background: #fff;
  border-radius: 16px;
  padding: 40px;
  text-align: center;
}

.icon-wrap {
  width: 80px;
  height: 80px;
  line-height: 80px;
  margin: 0 auto 24px;
  border-radius: 50%;
  font-size: 40px;

  &.success {
    background: #dcfce7;
    color: #22c55e;
  }
  &.pending {
    background: #fef3c7;
    color: #f59e0b;
  }
  &.failed {
    background: #fee2e2;
    color: #ef4444;
  }
}

.icon-text {
  font-weight: bold;
}

.status-title {
  display: block;
  font-size: 22px;
  font-weight: bold;
  color: #333;
  margin-bottom: 16px;
}

.pay-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
  font-size: 12px;
  color: #666;
  margin-bottom: 32px;
}

.actions {
  display: flex;
  justify-content: center;
  gap: 16px;
}

.btn {
  padding: 12px 28px;
  border-radius: 24px;
  font-size: 16px;

  &.secondary {
    background: #f3f4f6;
    color: #6b7280;
  }
  &.primary {
    background: linear-gradient(135deg, #ef4444, #f97316);
    color: #fff;
  }
}
</style>
