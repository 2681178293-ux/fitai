<template>
  <view class="page">
    <view class="tabs">
      <view
        v-for="t in tabs"
        :key="t.value === '' ? 'all' : t.value"
        class="tab"
        :class="{ active: activeTab === t.value }"
        @click="changeTab(t.value)"
      >
        {{ t.label }}
      </view>
    </view>

    <scroll-view
      scroll-y
      class="list-wrap"
      :style="{ height: listHeight + 'px' }"
      @scrolltolower="loadMore"
    >
      <view v-if="loading && bookings.length === 0" class="hint">加载中...</view>
      <view v-else-if="bookings.length === 0" class="hint">暂无预约记录</view>
      <view v-else class="list">
        <view v-for="b in bookings" :key="b.id" class="card">
          <view class="card-head">
            <text class="no">{{ b.bookingNo }}</text>
            <text class="st" :class="statusClass(b.status)">{{ statusText(b.status) }}</text>
          </view>
          <view class="card-body">
            <view class="row">
              <text class="row-label">教练</text>
              <text class="row-value">{{ b.coachName || '-' }}</text>
            </view>
            <view class="row">
              <text class="row-label">时间</text>
              <text class="row-value">{{ b.bookingDate }} {{ b.timeSlot }}</text>
            </view>
            <view class="row" v-if="b.remark">
              <text class="row-label">备注</text>
              <text class="row-value">{{ b.remark }}</text>
            </view>
            <view class="row" v-if="b.status === 3 && b.cancelReason">
              <text class="row-label">取消原因</text>
              <text class="row-value">{{ b.cancelReason }}</text>
            </view>
          </view>
          <view class="card-foot">
            <text class="time">{{ b.createTime }}</text>
            <view v-if="canCancel(b)" class="btn-cancel" @click="handleCancel(b)">取消预约</view>
          </view>
        </view>
        <view v-if="loading" class="more-hint">加载中...</view>
        <view v-else-if="bookings.length >= total" class="more-hint">没有更多了</view>
      </view>
    </scroll-view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getMyBookings, cancelBooking } from '@/api/coach'

const tabs = [
  { value: '', label: '全部' },
  { value: 0, label: '待确认' },
  { value: 1, label: '已确认' },
  { value: 2, label: '已完成' },
  { value: 3, label: '已取消' }
]

const activeTab = ref('')
const bookings = ref([])
const total = ref(0)
const loading = ref(false)
const listHeight = ref(500)
const query = ref({ pageNum: 1, pageSize: 10, status: '' })

onMounted(() => {
  const sys = uni.getSystemInfoSync()
  listHeight.value = sys.windowHeight - 56
  load()
})

async function load(append = false) {
  if (loading.value) return
  loading.value = true
  try {
    const res = await getMyBookings(query.value)
    const data = res.data || {}
    const list = data.records || []
    total.value = Number(data.total) || 0
    bookings.value = append ? [...bookings.value, ...list] : list
  } catch (e) {
    uni.showToast({ title: '加载失败', icon: 'none' })
  }
  loading.value = false
}

function changeTab(v) {
  if (activeTab.value === v) return
  activeTab.value = v
  query.value.status = v
  query.value.pageNum = 1
  load()
}

function loadMore() {
  if (loading.value) return
  if (bookings.value.length >= total.value) return
  query.value.pageNum++
  load(true)
}

function statusText(status) {
  const map = { 0: '待确认', 1: '已确认', 2: '已完成', 3: '已取消' }
  return map[status] || '-'
}

function statusClass(status) {
  if (status === 0) return 'warn'
  if (status === 1) return 'ok'
  if (status === 3) return 'muted'
  return 'done'
}

function canCancel(b) {
  return b.status === 0 || b.status === 1
}

function handleCancel(b) {
  uni.showModal({
    title: '提示',
    content: '确定要取消这次预约吗？',
    success: async (res) => {
      if (!res.confirm) return
      try {
        await cancelBooking(b.id)
        uni.showToast({ title: '已取消预约', icon: 'success' })
        query.value.pageNum = 1
        load()
      } catch (e) {
        // request 已统一提示错误信息
      }
    }
  })
}
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
.list-wrap {
  width: 100%;
}
.hint {
  text-align: center;
  padding: 40px;
  color: #999;
}
.more-hint {
  padding: 14px;
  text-align: center;
  color: #9ca3af;
  font-size: 12px;
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
  color: #2563eb;
}
.st.done {
  color: #16a34a;
}
.st.muted {
  color: #9ca3af;
}
.card-body {
  display: flex;
  flex-direction: column;
  gap: 6px;
}
.row {
  display: flex;
  align-items: flex-start;
  gap: 10px;
}
.row-label {
  width: 56px;
  font-size: 13px;
  color: #9ca3af;
}
.row-value {
  flex: 1;
  font-size: 14px;
  color: #111;
  line-height: 1.5;
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
.btn-cancel {
  padding: 6px 14px;
  background: #fff;
  color: #ef4444;
  border: 1px solid #ef4444;
  border-radius: 8px;
  font-size: 13px;
}
</style>
