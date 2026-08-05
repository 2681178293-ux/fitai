<template>
  <view class="page">
    <view v-if="list.length === 0 && !loading" class="empty-wrap">
      <text>暂无会员订单</text>
    </view>
    <view v-else>
      <view v-for="o in list" :key="o.id" class="order-card">
        <view class="row">
          <text class="name">{{ o.planName || '会员套餐' }}</text>
          <text class="status" :class="'s' + o.status">{{ statusText(o.status) }}</text>
        </view>
        <text class="no">订单号 {{ o.orderNo }}</text>
        <text class="amt">¥{{ o.amount }}</text>
        <view v-if="o.status === 0" class="pay-row">
          <view class="btn-pay" @click="goPay(o)">去支付</view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { getMemberOrders } from '@/api/member'
import { useUserStore } from '@/store/user'

const userStore = useUserStore()
const list = ref([])
const loading = ref(true)

onMounted(async () => {
  if (!userStore.isLoggedIn) {
    uni.navigateTo({ url: '/pages/login/index' })
    return
  }
  await load()
})

onShow(() => {
  if (userStore.isLoggedIn) {
    load()
  }
})

async function load() {
  loading.value = true
  try {
    const res = await getMemberOrders()
    list.value = res.data || []
  } catch (e) {
    uni.showToast({ title: e.msg || '加载失败', icon: 'none' })
  }
  loading.value = false
}

function statusText(s) {
  const m = { 0: '待支付', 1: '已支付', 2: '已取消', 3: '已退款' }
  return m[s] ?? '未知'
}

function goPay(o) {
  const subject = encodeURIComponent(`FitAI会员-${o.planName || ''}`)
  uni.navigateTo({
    url: `/pages/order/pay?orderNo=${encodeURIComponent(o.orderNo)}&amount=${o.amount}&orderType=member&subject=${subject}`
  })
}
</script>

<style scoped>
.page {
  min-height: 100vh;
  background: #f5f5f5;
  padding: 12px;
}
.empty-wrap {
  padding: 60px;
  text-align: center;
  color: #999;
}
.order-card {
  background: #fff;
  border-radius: 12px;
  padding: 14px;
  margin-bottom: 10px;
}
.row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.name {
  font-size: 16px;
  font-weight: 600;
  color: #111;
}
.status {
  font-size: 13px;
}
.status.s0 {
  color: #f59e0b;
}
.status.s1 {
  color: #22c55e;
}
.no {
  display: block;
  font-size: 12px;
  color: #666;
  margin-top: 8px;
}
.amt {
  display: block;
  font-size: 18px;
  color: #ef4444;
  font-weight: bold;
  margin-top: 6px;
}
.pay-row {
  margin-top: 12px;
  display: flex;
  justify-content: flex-end;
}
.btn-pay {
  padding: 8px 20px;
  background: linear-gradient(135deg, #ef4444, #f97316);
  color: #fff;
  border-radius: 20px;
  font-size: 14px;
}
</style>
