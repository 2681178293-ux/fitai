<template>
  <view class="page">
    <view v-if="loading" class="hint">加载中...</view>
    <template v-else-if="order">
      <view class="status-card">
        <text class="status-main">{{ statusTitle }}</text>
        <text v-if="subStatusText" class="status-sub">{{ subStatusText }}</text>
      </view>

      <!-- 商城订单：地址 -->
      <view v-if="order.orderType === 'product' && order.addressSnapshot" class="card">
        <text class="card-title">收货信息</text>
        <text class="addr-line">
          {{ order.addressSnapshot.name }} {{ order.addressSnapshot.phone }}
        </text>
        <text class="addr-line muted">
          {{ order.addressSnapshot.province }}{{ order.addressSnapshot.city }}{{ order.addressSnapshot.district }}{{ order.addressSnapshot.detail }}
        </text>
      </view>

      <!-- 商品 -->
      <view class="card">
        <text class="card-title">商品信息</text>
        <template v-if="order.orderType === 'seckill'">
          <view class="row">
            <image :src="order.skuImage || '/static/banner-default.png'" class="thumb" mode="aspectFill" />
            <view class="rinfo">
              <text class="rname">{{ order.skuName }}</text>
              <text class="rmeta">¥{{ order.payAmount }} × {{ order.quantity || 1 }}</text>
            </view>
          </view>
        </template>
        <template v-else>
          <view v-for="item in order.items || []" :key="item.id || item.skuId" class="row">
            <image :src="item.skuImage || '/static/banner-default.png'" class="thumb" mode="aspectFill" />
            <view class="rinfo">
              <text class="rname">{{ item.skuName }}</text>
              <text class="rmeta">¥{{ item.price }} × {{ item.quantity }}</text>
            </view>
          </view>
        </template>
      </view>

      <view class="card">
        <text class="card-title">订单信息</text>
        <view class="info-row">
          <text class="k">订单编号</text>
          <text class="v">{{ order.orderNo }}</text>
        </view>
        <view class="info-row">
          <text class="k">创建时间</text>
          <text class="v">{{ order.createTime }}</text>
        </view>
        <view v-if="order.payTime" class="info-row">
          <text class="k">支付时间</text>
          <text class="v">{{ order.payTime }}</text>
        </view>
      </view>

      <view v-if="order.orderType === 'product'" class="card">
        <view class="info-row">
          <text class="k">商品金额</text>
          <text class="v">¥{{ order.totalAmount }}</text>
        </view>
        <view class="info-row">
          <text class="k">实付金额</text>
          <text class="v strong">¥{{ order.payAmount }}</text>
        </view>
      </view>

      <view class="actions">
        <template v-if="order.orderType === 'product'">
          <view v-if="order.status === 'unpaid'" class="btn outline" @click="handleCancel">取消订单</view>
          <view v-if="order.status === 'unpaid'" class="btn primary" @click="goPayProduct">立即支付</view>
          <view v-if="order.status === 'shipped'" class="btn primary" @click="handleConfirm">确认收货</view>
        </template>
        <template v-else>
          <view v-if="order.status === 0" class="btn primary" @click="goPaySeckill">立即支付</view>
        </template>
      </view>
    </template>
    <view v-else class="hint">订单不存在</view>
  </view>
</template>

<script setup>
import { ref, computed } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { getOrderById, cancelOrder, confirmReceive } from '@/api/order'
import { getSeckillOrderDetail } from '@/api/seckill'

const loading = ref(true)
const order = ref(null)

const statusTitle = computed(() => {
  if (!order.value) return ''
  if (order.value.orderType === 'seckill') {
    const s = order.value.status
    if (s === 0) return '待付款'
    if (s === 1) return '已完成'
    return '已取消'
  }
  const map = {
    unpaid: '待付款',
    paid: '待发货',
    shipped: '待收货',
    completed: '已完成',
    received: '已完成',
    cancelled: '已取消'
  }
  return map[order.value.status] || order.value.status || '-'
})

const subStatusText = computed(() => {
  if (!order.value || order.value.orderType !== 'product') return ''
  if (order.value.status === 'unpaid') return '请尽快完成支付'
  if (order.value.status === 'shipped') return '商品正在路上'
  return ''
})

onLoad(async (options) => {
  const id = options.id
  const seckillNo = options.seckillNo ? decodeURIComponent(options.seckillNo) : ''
  loading.value = true
  try {
    if (seckillNo) {
      const res = await getSeckillOrderDetail(seckillNo)
      const d = res.data
      order.value = {
        orderType: 'seckill',
        orderNo: d.orderNo,
        skuName: d.skuName,
        skuImage: d.skuImage,
        payAmount: d.seckillPrice,
        quantity: d.quantity,
        status: d.status,
        statusText: d.statusText,
        createTime: d.createTime
      }
    } else if (id) {
      const res = await getOrderById(id)
      order.value = { orderType: 'product', ...res.data }
    }
  } catch (e) {
    uni.showToast({ title: e.msg || '加载失败', icon: 'none' })
  } finally {
    loading.value = false
  }
})

function goPayProduct() {
  const o = order.value
  if (!o || o.orderType !== 'product') return
  uni.navigateTo({
    url: `/pages/order/pay?orderNo=${encodeURIComponent(o.orderNo)}&amount=${o.payAmount}&subject=${encodeURIComponent('订单支付')}`
  })
}

function goPaySeckill() {
  const o = order.value
  if (!o || o.orderType !== 'seckill') return
  const amt = o.payAmount ?? 0
  uni.navigateTo({
    url: `/pages/order/pay?orderType=seckill&orderNo=${encodeURIComponent(o.orderNo)}&amount=${amt}&subject=${encodeURIComponent('秒杀订单')}`
  })
}

async function handleCancel() {
  const o = order.value
  if (!o || o.orderType !== 'product') return
  const res = await new Promise((resolve) => {
    uni.showModal({
      title: '提示',
      content: '确定取消订单？',
      success: (r) => resolve(r.confirm)
    })
  })
  if (!res) return
  try {
    await cancelOrder(o.id)
    uni.showToast({ title: '已取消', icon: 'success' })
    const reload = await getOrderById(o.id)
    order.value = { orderType: 'product', ...reload.data }
  } catch (e) {
    uni.showToast({ title: e.msg || '操作失败', icon: 'none' })
  }
}

async function handleConfirm() {
  const o = order.value
  if (!o || o.orderType !== 'product') return
  const ok = await new Promise((resolve) => {
    uni.showModal({
      title: '确认收货',
      content: '确认已收到商品？',
      success: (r) => resolve(r.confirm)
    })
  })
  if (!ok) return
  try {
    await confirmReceive(o.id)
    uni.showToast({ title: '已确认收货', icon: 'success' })
    const reload = await getOrderById(o.id)
    order.value = { orderType: 'product', ...reload.data }
  } catch (e) {
    uni.showToast({ title: e.msg || '操作失败', icon: 'none' })
  }
}
</script>

<style lang="scss" scoped>
.page {
  min-height: 100vh;
  padding: 12px;
  padding-bottom: 32px;
  background: #f5f5f5;
}
.hint {
  text-align: center;
  padding: 48px;
  color: #999;
}
.status-card {
  background: linear-gradient(135deg, #3b82f6, #2563eb);
  border-radius: 12px;
  padding: 20px;
  color: #fff;
  margin-bottom: 12px;
}
.status-main {
  font-size: 20px;
  font-weight: bold;
}
.status-sub {
  display: block;
  margin-top: 8px;
  font-size: 13px;
  opacity: 0.9;
}
.card {
  background: #fff;
  border-radius: 12px;
  padding: 14px;
  margin-bottom: 12px;
}
.card-title {
  font-weight: 600;
  margin-bottom: 10px;
  display: block;
}
.addr-line {
  display: block;
  font-size: 14px;
  color: #111;
  line-height: 1.5;
}
.addr-line.muted {
  color: #6b7280;
  font-size: 13px;
  margin-top: 6px;
}
.row {
  display: flex;
  gap: 10px;
  margin-bottom: 10px;
}
.thumb {
  width: 72px;
  height: 72px;
  border-radius: 8px;
}
.rinfo {
  flex: 1;
}
.rname {
  font-size: 14px;
  color: #111;
}
.rmeta {
  margin-top: 6px;
  font-size: 13px;
  color: #ef4444;
}
.info-row {
  display: flex;
  justify-content: space-between;
  padding: 8px 0;
  font-size: 13px;
}
.k {
  color: #9ca3af;
}
.v {
  color: #111;
}
.v.strong {
  color: #ef4444;
  font-weight: bold;
}
.actions {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  justify-content: flex-end;
  margin-top: 8px;
}
.btn {
  padding: 10px 20px;
  border-radius: 8px;
  font-size: 14px;
}
.btn.outline {
  border: 1px solid #d1d5db;
  color: #374151;
}
.btn.primary {
  background: #ef4444;
  color: #fff;
}
</style>
