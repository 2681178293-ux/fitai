<template>
  <view class="page">
    <view v-if="!isLoggedIn" class="tips">请先登录后查看消息</view>
    <template v-else>
      <view class="toolbar">
        <text class="hint">系统通知、订单与活动消息</text>
        <text v-if="list.length" class="link" @click="markAll">全部已读</text>
      </view>
      <view v-if="loading && list.length === 0" class="loading">加载中…</view>
      <view v-else-if="list.length === 0" class="empty">暂无消息</view>
      <scroll-view
        v-else
        scroll-y
        class="list-wrap"
        @scrolltolower="loadMore"
      >
        <view
          v-for="item in list"
          :key="item.id"
          class="msg-item"
          :class="{ unread: item.isRead === 0 }"
          @click="openItem(item)"
        >
          <view class="msg-top">
            <text class="type-tag">{{ typeLabel(item.type) }}</text>
            <text v-if="item.isRead === 0" class="dot">未读</text>
          </view>
          <text class="msg-title">{{ item.title || '通知' }}</text>
          <text class="msg-time">{{ formatTime(item.createTime) }}</text>
        </view>
        <view v-if="loadingMore" class="more-hint">加载更多…</view>
        <view v-else-if="!hasMore && list.length" class="more-hint">没有更多了</view>
      </scroll-view>
    </template>

    <view v-if="current" class="mask" @click="closeDetail">
      <view class="sheet" @click.stop>
        <text class="sheet-title">{{ current.title }}</text>
        <text class="sheet-time">{{ formatTime(current.createTime) }}</text>
        <scroll-view scroll-y class="sheet-body">
          <text class="sheet-content">{{ current.content || '无正文' }}</text>
        </scroll-view>
        <view class="sheet-actions">
          <view class="sheet-btn ghost" @click="removeCurrent">删除</view>
          <view class="sheet-btn primary" @click="closeDetail">关闭</view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import {
  getMessageList,
  markMessageRead,
  markAllMessagesRead,
  deleteMessage
} from '@/api/message'
import { useUserStore } from '@/store/user'

const userStore = useUserStore()
const isLoggedIn = computed(() => userStore.isLoggedIn)
const list = ref([])
const loading = ref(true)
const loadingMore = ref(false)
const pageNum = ref(1)
const hasMore = ref(true)
const current = ref(null)

onMounted(() => {
  if (!isLoggedIn.value) {
    loading.value = false
    return
  }
  refresh()
})

onShow(() => {
  if (isLoggedIn.value) {
    refresh()
  }
})

async function refresh() {
  if (!isLoggedIn.value) {
    list.value = []
    loading.value = false
    return
  }
  pageNum.value = 1
  hasMore.value = true
  list.value = []
  await loadPage(true)
}

async function loadPage(isFirst) {
  if (!isLoggedIn.value) {
    loading.value = false
    loadingMore.value = false
    return
  }
  if (isFirst) {
    loading.value = true
  } else {
    loadingMore.value = true
  }
  try {
    const res = await getMessageList({ pageNum: pageNum.value, pageSize: 15 })
    const page = res.data || {}
    const records = page.records || []
    if (pageNum.value === 1) {
      list.value = records
    } else {
      list.value = list.value.concat(records)
    }
    const size = page.size || 15
    const total = page.total != null ? page.total : null
    if (total != null && page.current != null) {
      hasMore.value = page.current * size < total
    } else {
      hasMore.value = records.length >= size
    }
  } catch (e) {
    if (isFirst) {
      uni.showToast({ title: e.msg || '加载失败', icon: 'none' })
    }
  } finally {
    loading.value = false
    loadingMore.value = false
  }
}

function loadMore() {
  if (!hasMore.value || loadingMore.value || loading.value) return
  pageNum.value += 1
  loadPage(false)
}

function typeLabel(t) {
  const m = { system: '系统', order: '订单', activity: '活动', ai: 'AI' }
  return m[t] || '消息'
}

function formatTime(t) {
  if (!t) return ''
  const s = String(t)
  return s.length >= 16 ? s.slice(0, 16).replace('T', ' ') : s
}

async function openItem(item) {
  current.value = item
  if (item.isRead === 0) {
    try {
      await markMessageRead(item.id)
      item.isRead = 1
    } catch (e) {}
  }
}

function closeDetail() {
  current.value = null
}

async function removeCurrent() {
  if (!current.value) return
  const id = current.value.id
  try {
    await deleteMessage(id)
    list.value = list.value.filter((x) => x.id !== id)
    current.value = null
    uni.showToast({ title: '已删除', icon: 'none' })
  } catch (e) {
    uni.showToast({ title: e.msg || '删除失败', icon: 'none' })
  }
}

async function markAll() {
  try {
    await markAllMessagesRead()
    list.value = list.value.map((x) => ({ ...x, isRead: 1 }))
    uni.showToast({ title: '已全部标为已读', icon: 'none' })
  } catch (e) {
    uni.showToast({ title: e.msg || '操作失败', icon: 'none' })
  }
}
</script>

<style scoped>
.page {
  min-height: 100vh;
  background: #f0f2f5;
}

.tips {
  padding: 48px 24px;
  text-align: center;
  color: #64748b;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background: #fff;
  border-bottom: 1px solid #f1f5f9;
}

.hint {
  font-size: 12px;
  color: #94a3b8;
}

.link {
  font-size: 13px;
  color: #2563eb;
  font-weight: 600;
}

.loading,
.empty {
  padding: 48px;
  text-align: center;
  color: #94a3b8;
}

.list-wrap {
  max-height: calc(100vh - 52px);
  padding: 12px;
}

.msg-item {
  background: #fff;
  border-radius: 12px;
  padding: 14px;
  margin-bottom: 10px;
  box-shadow: 0 1px 6px rgba(15, 23, 42, 0.06);
}

.msg-item.unread {
  border-left: 3px solid #3b82f6;
}

.msg-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.type-tag {
  font-size: 11px;
  padding: 2px 8px;
  background: #eff6ff;
  color: #2563eb;
  border-radius: 4px;
}

.dot {
  font-size: 11px;
  color: #3b82f6;
  font-weight: 600;
}

.msg-title {
  font-size: 15px;
  font-weight: 600;
  color: #0f172a;
  display: block;
}

.msg-time {
  display: block;
  margin-top: 8px;
  font-size: 12px;
  color: #94a3b8;
}

.more-hint {
  text-align: center;
  padding: 16px;
  font-size: 12px;
  color: #94a3b8;
}

.mask {
  position: fixed;
  inset: 0;
  background: rgba(15, 23, 42, 0.45);
  z-index: 500;
  display: flex;
  align-items: flex-end;
}

.sheet {
  width: 100%;
  max-height: 75vh;
  background: #fff;
  border-radius: 16px 16px 0 0;
  padding: 16px;
  padding-bottom: calc(16px + env(safe-area-inset-bottom));
}

.sheet-title {
  font-size: 17px;
  font-weight: 700;
  color: #0f172a;
}

.sheet-time {
  display: block;
  margin-top: 6px;
  font-size: 12px;
  color: #94a3b8;
}

.sheet-body {
  max-height: 42vh;
  margin-top: 12px;
}

.sheet-content {
  font-size: 14px;
  color: #334155;
  line-height: 1.6;
  white-space: pre-wrap;
  word-break: break-word;
}

.sheet-actions {
  display: flex;
  gap: 12px;
  margin-top: 16px;
  padding-top: 12px;
  border-top: 1px solid #f1f5f9;
}

.sheet-btn {
  flex: 1;
  text-align: center;
  padding: 12px;
  border-radius: 10px;
  font-size: 15px;
  font-weight: 600;
}

.sheet-btn.ghost {
  background: #f1f5f9;
  color: #64748b;
}

.sheet-btn.primary {
  background: #2563eb;
  color: #fff;
}
</style>
