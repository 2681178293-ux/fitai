<template>
  <view class="page">
    <view v-if="loading" class="loading-wrap">
      <text>加载中...</text>
    </view>
    <view v-else-if="detail" class="content">
      <!-- 视频 -->
      <view class="video-wrap">
        <video
          v-if="detail.videoUrl"
          :src="detail.videoUrl"
          controls
          class="video"
          @play="onVideoPlay"
          @timeupdate="onVideoTimeUpdate"
          @ended="onVideoEnded"
        />
        <view v-else class="no-video">
          <text>暂无视频</text>
        </view>
      </view>

      <!-- 课程信息 -->
      <view class="info-card">
        <text class="course-title">{{ detail.title }}</text>
        <view class="course-meta">
          <text>{{ detail.duration }}分钟</text>
          <text>{{ detail.calories }}卡</text>
          <text>{{ detail.difficultyName || '-' }}</text>
          <text>观看 {{ detail.viewCount || 0 }}</text>
          <text>{{ detail.likeCount || 0 }}人点赞</text>
        </view>
        <view class="action-row">
          <view
            class="action-btn"
            :class="{ active: detail.userLiked }"
            @click="handleLike"
          >
            <IconifyIcon
              :icon="detail.userLiked ? 'mdi:heart' : 'mdi:heart-outline'"
              :size="22"
              :color="detail.userLiked ? '#ef4444' : '#999'"
            />
            <text>{{ detail.userLiked ? '已点赞' : '点赞' }}</text>
          </view>
          <view
            class="action-btn"
            :class="{ active: detail.userFavorited }"
            @click="handleFavorite"
          >
            <IconifyIcon
              :icon="detail.userFavorited ? 'mdi:star' : 'mdi:star-outline'"
              :size="22"
              :color="detail.userFavorited ? '#f59e0b' : '#999'"
            />
            <text>{{ detail.userFavorited ? '已收藏' : '收藏' }}</text>
          </view>
        </view>
        <view v-if="detail.description" class="course-desc">{{ detail.description }}</view>
      </view>
    </view>
    <view v-else class="empty-wrap">
      <text>课程不存在</text>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getToken } from '@/utils/authStorage'
import { getCourseDetail, toggleLike, toggleFavorite, updateProgress, completeCourse, recordCoursePlay } from '@/api/course'

const detail = ref(null)
const loading = ref(true)
const likeLoading = ref(false)
const favoriteLoading = ref(false)
const playRecorded = ref(false)
let lastProgressAt = 0

onMounted(async () => {
  const pages = getCurrentPages()
  const current = pages[pages.length - 1]
  const id = current.options?.id
  if (!id) {
    loading.value = false
    return
  }
  try {
    const res = await getCourseDetail(id)
    detail.value = res.data
  } catch (e) {
    uni.showToast({ title: '加载失败', icon: 'none' })
  }
  loading.value = false
})

async function handleLike() {
  if (!detail.value || likeLoading.value) return
  const token = getToken()
  if (!token) {
    uni.navigateTo({ url: '/pages/login/index' })
    return
  }
  likeLoading.value = true
  try {
    const res = await toggleLike(detail.value.id)
    detail.value.userLiked = res.data
    detail.value.likeCount = (detail.value.likeCount || 0) + (res.data ? 1 : -1)
    detail.value.likeCount = Math.max(0, detail.value.likeCount)
    uni.showToast({ title: res.data ? '点赞成功' : '已取消点赞', icon: 'none' })
  } catch (e) {
    uni.showToast({ title: '操作失败', icon: 'none' })
  }
  likeLoading.value = false
}

async function handleFavorite() {
  if (!detail.value || favoriteLoading.value) return
  const token = getToken()
  if (!token) {
    uni.navigateTo({ url: '/pages/login/index' })
    return
  }
  favoriteLoading.value = true
  try {
    const res = await toggleFavorite(detail.value.id)
    detail.value.userFavorited = res.data
    uni.showToast({ title: res.data ? '收藏成功' : '已取消收藏', icon: 'none' })
  } catch (e) {
    uni.showToast({ title: '操作失败', icon: 'none' })
  }
  favoriteLoading.value = false
}

async function onVideoPlay() {
  const token = getToken()
  if (!token || !detail.value || playRecorded.value) return
  playRecorded.value = true
  try {
    await recordCoursePlay(detail.value.id)
  } catch (e) {}
}

function onVideoTimeUpdate(e) {
  const token = getToken()
  if (!token || !detail.value) return
  const sec = Math.floor(Number(e?.detail?.currentTime) || 0)
  const now = Date.now()
  if (now - lastProgressAt < 8000) return
  lastProgressAt = now
  updateProgress(detail.value.id, sec).catch(() => {})
}

function onVideoEnded() {
  const token = getToken()
  if (!token || !detail.value) return
  completeCourse(detail.value.id)
    .then(() => {
      if (detail.value) detail.value.userCompleted = true
      uni.showToast({ title: '已完成课程', icon: 'none' })
    })
    .catch(() => {})
}
</script>

<style scoped>
.page {
  min-height: 100vh;
  background: #f5f5f5;
}

.loading-wrap,
.empty-wrap {
  padding: 60px;
  text-align: center;
  color: #999;
}

.video-wrap {
  width: 100%;
  background: #000;
}

.video {
  width: 100%;
  height: 210px;
}

.no-video {
  height: 210px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #999;
}

.info-card {
  margin: 12px;
  padding: 16px;
  background: #fff;
  border-radius: 12px;
}

.course-title {
  font-size: 18px;
  font-weight: bold;
  color: #333;
}

.course-meta {
  display: flex;
  gap: 12px;
  margin-top: 8px;
  font-size: 12px;
  color: #999;
}

.action-row {
  margin-top: 12px;
}

.action-row {
  display: flex;
  gap: 12px;
  margin-top: 12px;
}

.action-btn {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  background: #f5f5f5;
  border-radius: 20px;
  font-size: 14px;
  color: #666;
}

.action-btn.active {
  background: #fef2f2;
  color: #ef4444;
}

.action-btn:last-child.active {
  background: #fffbeb;
  color: #f59e0b;
}

.course-desc {
  margin-top: 12px;
  font-size: 14px;
  color: #666;
  line-height: 1.6;
}
</style>
