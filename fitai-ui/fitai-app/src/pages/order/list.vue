<template>
  <view class="page">
    <view class="tabs">
      <view
        v-for="t in tabs"
        :key="t.value || 'all'"
        class="tab"
        :class="{ active: activeTab === t.value }"
        @click="changeTab(t.value)"
      >
        {{ t.label }}
      </view>
    </view>

    <view v-if="loading" class="hint">加载中...</view>
    <view v-else-if="displayList.length === 0" class="hint">暂无订单</view>
    <view v-else class="list">
      <view
        v-for="order in displayList"
        :key="order.orderType + '-' + (order.id || order.orderNo)"
        class="card"
        @click="goDetail(order)"
      >
        <view class="card-head">
          <text class="no">{{ order.orderNo }}</text>
          <text class="st" :class="statusClass(order)">{{ statusText(order) }}</text>
        </view>
        <view class="goods">
          <template v-if="order.orderType === 'seckill'">
            <image :src="order.skuImage || '/static/banner-default.png'" class="thumb" mode="aspectFill" />
            <view class="ginfo">
              <text class="gname">{{ order.skuName || '秒杀商品' }}</text>
              <text class="gprice">¥{{ order.payAmount ?? order.seckillPrice }}</text>
            </view>
          </template>
          <template v-else>
            <image
              :src="(order.items && order.items[0] && order.items[0].skuImage) || '/static/banner-default.png'"
              class="thumb"
              mode="aspectFill"
            />
            <view class="ginfo">
              <text class="gname">{{ firstSkuName(order) }}</text>
              <text class="gprice">¥{{ order.payAmount }}</text>
            </view>
          </template>
        </view>
        <view class="card-foot">
          <text class="time">{{ order.createTime }}</text>
          <view v-if="canPay(order)" class="btn-pay" @click.stop="goPay(order)">去支付</view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getOrderList } from '@/api/order'
import { getMySeckillOrders } from '@/api/seckill'

const tabs = [
  { value: '', label: '全部' },
  { value: 'unpaid', label: '待付款' },
  { value: 'paid', label: '待发货' },
  { value: 'shipped', label: '待收货' },
  { value: 'completed', label: '已完成' }
]

const loading = ref(false)
const activeTab = ref('')
const orders = ref([])

const displayList = computed(() => orders.value.filter((o) => orderInTab(o, activeTab.value)))

function orderInTab(o, tab) {
  if (!tab) return true
  if (o.orderType === 'seckill') {
    const st = o.rawStatus
    if (tab === 'unpaid') return st === 0
    if (tab === 'completed') return st === 1
    return false
  }
  if (tab === 'unpaid') return o.status === 'unpaid'
  if (tab === 'paid') return o.status === 'paid'
  if (tab === 'shipped') return o.status === 'shipped'
  if (tab === 'completed') return o.status === 'completed' || o.status === 'received'
  return true
}

function firstSkuName(order) {
  const it = order.items && order.items[0]
  return it?.skuName || '商品'
}

function statusText(order) {
  if (order.orderType === 'seckill') {
    return order.statusName || (order.rawStatus === 0 ? '待付款' : order.rawStatus === 1 ? '已完成' : '已取消')
  }
  const map = {
    unpaid: '待付款',
    paid: '待发货',
    shipped: '待收货',
    completed: '已完成',
    received: '已完成',
    cancelled: '已取消'
  }
  return map[order.status] || order.status || '-'
}

function statusClass(order) {
  if (order.orderType === 'seckill') {
    if (order.rawStatus === 0) return 'warn'
    if (order.rawStatus === 1) return 'ok'
    return 'muted'
  }
  if (order.status === 'unpaid') return 'warn'
  if (order.status === 'cancelled') return 'muted'
  return 'ok'
}

function canPay(order) {
  if (order.orderType === 'seckill') return order.rawStatus === 0
  return order.status === 'unpaid'
}

async function load() {
  loading.value = true
  try {
    const [orderRes, seckillList] = await Promise.all([
      getOrderList({ pageNum: 1, pageSize: 50, status: activeTab.value || undefined }),
      getMySeckillOrders().catch(() => ({ data: [] }))
    ])
    const page = orderRes.data || {}
    const productRows = page.records || []
    const productOrders = productRows.map((o) => ({ ...o, orderType: 'product' }))
    const rawSeckill = Array.isArray(seckillList.data) ? seckillList.data : []
    const seckillOrders = rawSeckill.map((o) => ({
      orderType: 'seckill',
      id: 'seckill_' + o.orderNo,
      orderNo: o.orderNo,
      payAmount: o.seckillPrice ?? o.totalAmount,
      seckillPrice: o.seckillPrice,
      skuName: o.skuName,
      skuImage: o.skuImage,
      statusName: o.statusText || (o.status === 0 ? '待付款' : o.status === 1 ? '已完成' : '已取消'),
      rawStatus: o.status,
      createTime: o.createTime,
      items: [{ skuName: o.skuName, skuImage: o.skuImage, price: o.seckillPrice, quantity: o.quantity || 1 }]
    }))
    const merged = [...productOrders, ...seckillOrders].sort(
      (a, b) => new Date(b.createTime) - new Date(a.createTime)
    )
    orders.value = merged
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

function changeTab(v) {
  activeTab.value = v
  load()
}

function goDetail(order) {
  if (order.orderType === 'seckill') {
    uni.navigateTo({
      url: `/pages/order/detail?seckillNo=${encodeURIComponent(order.orderNo)}`
    })
  } else {
    uni.navigateTo({
      url: `/pages/order/detail?id=${encodeURIComponent(order.id)}`
    })
  }
}

function goPay(order) {
  if (order.orderType === 'seckill') {
    const amt = order.payAmount ?? order.seckillPrice ?? 0
    uni.navigateTo({
      url: `/pages/order/pay?orderType=seckill&orderNo=${encodeURIComponent(order.orderNo)}&amount=${amt}&subject=${encodeURIComponent('秒杀订单')}`
    })
  } else {
    uni.navigateTo({
      url: `/pages/order/pay?orderNo=${encodeURIComponent(order.orderNo)}&amount=${order.payAmount}&subject=${encodeURIComponent('订单支付')}`
    })
  }
}

onMounted(() => {
  load()
})
</script>

<style lang="scss" scoped>
.page {
  min-height: 100vh;
  background: #f5f5f5;
}
.tabs {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  padding: 10px 12px;
  background: #fff;
  border-bottom: 1px solid #eee;
}
.tab {
  padding: 6px 12px;
  font-size: 13px;
  color: #666;
  border-radius: 16px;
  background: #f3f4f6;
}
.tab.active {
  background: #dbeafe;
  color: #2563eb;
  font-weight: 600;
}
.hint {
  text-align: center;
  padding: 40px;
  color: #999;
}
.list {
  padding: 10px 12px 24px;
}
.card {
  background: #fff;
  border-radius: 12px;
  padding: 12px;
  margin-bottom: 10px;
}
.card-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}
.no {
  font-size: 12px;
  color: #6b7280;
}
.st {
  font-size: 13px;
}
.st.warn {
  color: #d97706;
}
.st.ok {
  color: #16a34a;
}
.st.muted {
  color: #9ca3af;
}
.goods {
  display: flex;
  gap: 10px;
}
.thumb {
  width: 72px;
  height: 72px;
  border-radius: 8px;
}
.ginfo {
  flex: 1;
}
.gname {
  font-size: 14px;
  color: #111;
  line-height: 1.4;
}
.gprice {
  margin-top: 8px;
  font-size: 16px;
  color: #ef4444;
  font-weight: 600;
}
.card-foot {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 10px;
  padding-top: 10px;
  border-top: 1px solid #f3f4f6;
}
.time {
  font-size: 12px;
  color: #9ca3af;
}
.btn-pay {
  padding: 6px 14px;
  background: #ef4444;
  color: #fff;
  border-radius: 8px;
  font-size: 13px;
}
</style>
