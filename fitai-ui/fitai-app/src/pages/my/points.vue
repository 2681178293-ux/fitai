<template>
  <view class="page">
    <view class="head-card">
      <text class="head-label">当前积分</text>
      <text class="head-value">{{ balance }}</text>
    </view>

    <view class="section-title">积分明细</view>
    <view v-if="loading" class="hint">加载中...</view>
    <view v-else-if="logs.length === 0" class="hint">暂无记录</view>
    <view v-else>
      <view v-for="row in logs" :key="row.id" class="row">
        <view class="row-left">
          <text class="row-title">{{ typeLabel(row.type) }} · {{ sourceLabel(row.source) }}</text>
          <text v-if="row.remark" class="row-sub">{{ row.remark }}</text>
          <text class="row-time">{{ row.createTime }}</text>
        </view>
        <text class="row-pts" :class="{ neg: row.points < 0 }">
          {{ row.points > 0 ? '+' : '' }}{{ row.points }}
        </text>
      </view>
      <view v-if="hasMore" class="more" @click="loadMore">加载更多</view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getPointsBalance, getPointsLogs } from '@/api/marketing'
import { getToken } from '@/utils/authStorage'

const balance = ref(0)
const loading = ref(true)
const logs = ref([])
const pageNum = ref(1)
const hasMore = ref(true)

function typeLabel(t) {
  const m = { earn: '获得', spend: '消费', expire: '过期' }
  return m[t] || t || '变动'
}

function sourceLabel(s) {
  const m = { order: '订单', checkin: '签到', activity: '活动' }
  return m[s] || s || '其他'
}

async function loadBalance() {
  try {
    const res = await getPointsBalance()
    const d = res.data || {}
    balance.value = d.balance ?? d.points ?? 0
  } catch (e) {
    balance.value = 0
  }
}

async function loadLogs(reset) {
  if (reset) {
    pageNum.value = 1
    logs.value = []
    hasMore.value = true
  }
  loading.value = true
  try {
    const res = await getPointsLogs({ pageNum: pageNum.value, pageSize: 20 })
    const page = res.data || {}
    const records = page.records || page.list || []
    if (reset) {
      logs.value = records
    } else {
      logs.value = logs.value.concat(records)
    }
    const total = page.total ?? 0
    hasMore.value = logs.value.length < total
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

function loadMore() {
  if (!hasMore.value || loading.value) return
  pageNum.value += 1
  loadLogs(false)
}

onMounted(async () => {
  if (!getToken()) {
    uni.navigateTo({ url: '/pages/login/index' })
    return
  }
  await loadBalance()
  await loadLogs(true)
})
</script>

<style lang="scss" scoped>
.page {
  min-height: 100vh;
  background: #f5f5f5;
  padding-bottom: 24px;
}
.head-card {
  margin: 12px;
  padding: 24px;
  background: linear-gradient(135deg, #3b82f6, #2563eb);
  border-radius: 16px;
  color: #fff;
}
.head-label {
  font-size: 14px;
  opacity: 0.9;
}
.head-value {
  display: block;
  font-size: 36px;
  font-weight: bold;
  margin-top: 8px;
}
.section-title {
  padding: 8px 16px;
  font-size: 15px;
  font-weight: 600;
  color: #333;
}
.hint {
  text-align: center;
  padding: 24px;
  color: #999;
}
.row {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin: 0 12px 8px;
  padding: 14px;
  background: #fff;
  border-radius: 12px;
}
.row-title {
  font-size: 14px;
  color: #111;
}
.row-sub {
  display: block;
  font-size: 12px;
  color: #6b7280;
  margin-top: 4px;
}
.row-time {
  display: block;
  font-size: 12px;
  color: #9ca3af;
  margin-top: 6px;
}
.row-pts {
  font-size: 16px;
  font-weight: 600;
  color: #16a34a;
}
.row-pts.neg {
  color: #ef4444;
}
.more {
  text-align: center;
  padding: 16px;
  color: #2563eb;
  font-size: 14px;
}
</style>
