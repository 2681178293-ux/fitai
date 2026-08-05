<template>
  <view class="page">
    <scroll-view scroll-y class="feed" :style="{ height: listHeight + 'px' }" @scrolltolower="loadMore">
      <view v-for="p in list" :key="p.id" class="post">
        <view class="post-header">
          <image :src="p.userAvatar || '/static/default-avatar.png'" class="avatar" mode="aspectFill" />
          <view class="user-info">
            <text class="nickname">{{ p.userNickname || '用户' }}</text>
            <text class="time">{{ formatTime(p.createTime) }}</text>
          </view>
        </view>
        <text class="content">{{ p.content }}</text>
        <view class="post-footer">
          <text class="like">❤ {{ p.likeCount || 0 }}</text>
          <text class="comment">💬 {{ p.commentCount || 0 }}</text>
        </view>
      </view>
      <view v-if="list.length === 0 && !loading" class="empty">暂无动态</view>
    </scroll-view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getFeed } from '@/api/community'

const list = ref([])
const loading = ref(false)
const pageNum = ref(1)
const listHeight = ref(500)

function formatTime(t) {
  if (!t) return ''
  const d = new Date(t)
  const now = new Date()
  const diff = now - d
  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return Math.floor(diff/60000) + '分钟前'
  if (diff < 86400000) return Math.floor(diff/3600000) + '小时前'
  return d.toLocaleDateString()
}

onMounted(() => {
  listHeight.value = uni.getSystemInfoSync().windowHeight - 44
  load()
})

async function load(append = false) {
  if (loading.value) return
  loading.value = true
  try {
    const res = await getFeed({ pageNum: pageNum.value, pageSize: 10 })
    const data = res.data || {}
    const rows = data.records || []
    list.value = append ? [...list.value, ...rows] : rows
  } catch (e) {}
  loading.value = false
}

function loadMore() {
  pageNum.value++
  load(true)
}

</script>

<style lang="scss" scoped>
.page { background: #f5f5f5; min-height: 100vh; }
.feed { padding: 12px; }
.post { background: #fff; border-radius: 12px; padding: 12px; margin-bottom: 12px; }
.post-header { display: flex; align-items: center; margin-bottom: 8px; }
.avatar { width: 40px; height: 40px; border-radius: 50%; margin-right: 12px; }
.nickname { font-size: 14px; font-weight: bold; color: #333; }
.time { display: block; font-size: 11px; color: #999; }
.content { font-size: 14px; color: #333; }
.post-footer { margin-top: 8px; font-size: 12px; color: #999; }
.empty { padding: 60px; text-align: center; color: #999; }
</style>
