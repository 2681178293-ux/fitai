<template>
  <view class="page">
    <view class="tabs">
      <view
        v-for="t in tabs"
        :key="t.value"
        class="tab"
        :class="{ active: activeTab === t.value }"
        @click="switchTab(t.value)"
      >
        {{ t.label }}
      </view>
    </view>

    <view v-if="activeTab === 'receive'" class="panel">
      <view v-if="availLoading" class="hint">加载中...</view>
      <view v-else-if="available.length === 0" class="hint">暂无可领优惠券</view>
      <view v-for="c in available" :key="c.id" class="coupon-card">
        <view class="c-left">
          <text class="c-name">{{ c.name }}</text>
          <text class="c-meta">满 {{ c.minAmount ?? 0 }} 可用 · 剩 {{ c.remainCount ?? 0 }}</text>
          <text class="c-time">{{ formatTime(c.startTime) }} ~ {{ formatTime(c.endTime) }}</text>
        </view>
        <view class="c-btn" @click="onReceive(c)">领取</view>
      </view>
    </view>

    <view v-else class="panel">
      <view v-if="myLoading" class="hint">加载中...</view>
      <view v-else-if="myList.length === 0" class="hint">暂无优惠券</view>
      <view v-for="u in myList" :key="u.id" class="my-card">
        <view class="my-top">
          <text class="my-name">{{ couponTitle(u) }}</text>
          <text class="my-st">{{ statusText(u.status) }}</text>
        </view>
        <text class="my-sub">券 ID：{{ u.couponId }}</text>
        <text v-if="u.expireTime" class="my-sub">有效期至 {{ u.expireTime }}</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getMyCoupons, getAvailableCoupons, receiveCoupon } from '@/api/marketing'
import { getToken } from '@/utils/authStorage'

const tabs = [
  { value: 0, label: '未使用' },
  { value: 1, label: '已使用' },
  { value: 2, label: '已过期' },
  { value: 'receive', label: '领券' }
]

const activeTab = ref(0)
const myLoading = ref(false)
const availLoading = ref(false)
const myList = ref([])
const available = ref([])
const nameById = ref({})

function formatTime(t) {
  if (!t) return '-'
  return String(t).replace('T', ' ').slice(0, 16)
}

function statusText(s) {
  const m = { 0: '未使用', 1: '已使用', 2: '已过期' }
  return m[s] ?? '-'
}

function couponTitle(u) {
  return nameById.value[u.couponId] || `优惠券 #${u.couponId}`
}

async function loadAvailable() {
  availLoading.value = true
  try {
    const res = await getAvailableCoupons()
    const list = Array.isArray(res.data) ? res.data : []
    available.value = list
    const map = { ...nameById.value }
    list.forEach((c) => {
      if (c.id != null) map[c.id] = c.name
    })
    nameById.value = map
  } catch (e) {
    available.value = []
  } finally {
    availLoading.value = false
  }
}

async function loadMy() {
  if (activeTab.value === 'receive') return
  myLoading.value = true
  try {
    const res = await getMyCoupons({ status: activeTab.value })
    myList.value = Array.isArray(res.data) ? res.data : []
  } catch (e) {
    myList.value = []
  } finally {
    myLoading.value = false
  }
}

function switchTab(v) {
  activeTab.value = v
  if (v === 'receive') {
    loadAvailable()
  } else {
    loadMy()
  }
}

async function onReceive(c) {
  try {
    await receiveCoupon(c.id)
    uni.showToast({ title: '领取成功' })
    await loadAvailable()
    if (activeTab.value === 0) loadMy()
  } catch (e) {
    uni.showToast({ title: e.msg || '领取失败', icon: 'none' })
  }
}

onMounted(async () => {
  if (!getToken()) {
    uni.navigateTo({ url: '/pages/login/index' })
    return
  }
  await loadAvailable()
  await loadMy()
})
</script>

<style lang="scss" scoped>
.page {
  min-height: 100vh;
  background: #f5f5f5;
}
.tabs {
  display: flex;
  background: #fff;
  border-bottom: 1px solid #eee;
}
.tab {
  flex: 1;
  text-align: center;
  padding: 12px 4px;
  font-size: 13px;
  color: #666;
}
.tab.active {
  color: #2563eb;
  font-weight: 600;
  border-bottom: 2px solid #2563eb;
}
.panel {
  padding: 12px;
}
.hint {
  text-align: center;
  padding: 40px;
  color: #999;
}
.coupon-card {
  display: flex;
  align-items: center;
  background: #fff;
  border-radius: 12px;
  padding: 14px;
  margin-bottom: 10px;
}
.c-left {
  flex: 1;
}
.c-name {
  font-size: 16px;
  font-weight: 600;
  color: #111;
}
.c-meta,
.c-time {
  display: block;
  font-size: 12px;
  color: #6b7280;
  margin-top: 4px;
}
.c-btn {
  padding: 8px 16px;
  background: #ef4444;
  color: #fff;
  border-radius: 8px;
  font-size: 14px;
}
.my-card {
  background: #fff;
  border-radius: 12px;
  padding: 14px;
  margin-bottom: 10px;
}
.my-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.my-name {
  font-size: 15px;
  font-weight: 600;
  color: #111;
}
.my-st {
  font-size: 13px;
  color: #2563eb;
}
.my-sub {
  display: block;
  font-size: 12px;
  color: #6b7280;
  margin-top: 6px;
}
</style>
