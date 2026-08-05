<template>
  <view class="page">
    <view class="intro">
      <text class="intro-title">我的课程学习进度</text>
      <text class="intro-desc">进入课程页观看视频后，会在此累计播放进度；学完将标记为已完成。</text>
    </view>

    <view v-if="loading" class="loading">加载中…</view>
    <view v-else-if="list.length === 0" class="empty-wrap">
      <text class="empty-icon">📚</text>
      <text class="empty-title">暂无学习记录</text>
      <text class="empty-hint">去「课程」选一门课，打开详情播放视频后即可在此看到进度</text>
    </view>
    <view v-else class="list">
      <view v-for="r in list" :key="r.id" class="item" @click="goCourse(r.id)">
        <image
          v-if="r.coverImage"
          :src="r.coverImage"
          class="cover"
          mode="aspectFill"
        />
        <view v-else class="cover cover-placeholder">
          <text class="ph">课</text>
        </view>
        <view class="info">
          <text class="name">{{ r.title || '课程' }}</text>
          <view class="meta-row">
            <text v-if="r.duration" class="meta">{{ r.duration }} 分钟</text>
            <text v-if="r.difficultyName" class="meta">{{ r.difficultyName }}</text>
          </view>
          <view class="bar-wrap">
            <view class="bar-bg">
              <view class="bar-fill" :style="{ width: progressPercent(r) + '%' }" />
            </view>
            <text class="pct">{{ progressPercent(r) }}%</text>
          </view>
          <text class="sub">{{ progressSubText(r) }}</text>
        </view>
        <view v-if="r.userCompleted" class="done-badge">已学完</view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { getLearningHistory } from '@/api/course'
import { useUserStore } from '@/store/user'

const userStore = useUserStore()
const list = ref([])
const loading = ref(true)

onMounted(() => {
  if (!userStore.isLoggedIn) {
    uni.navigateTo({ url: '/pages/login/index' })
    return
  }
  load()
})

onShow(() => {
  if (userStore.isLoggedIn) {
    load()
  }
})

async function load() {
  loading.value = true
  try {
    const res = await getLearningHistory({ pageNum: 1, pageSize: 50 })
    list.value = res.data?.records || []
  } catch (e) {
    uni.showToast({ title: e.msg || '加载失败', icon: 'none' })
    list.value = []
  }
  loading.value = false
}

function progressPercent(course) {
  if (!course) return 0
  if (course.userCompleted) return 100
  const totalSec = (course.duration || 0) * 60
  if (totalSec <= 0) return 0
  const sec = course.userProgress || 0
  return Math.min(100, Math.round((sec / totalSec) * 100))
}

function progressSubText(course) {
  if (!course) return ''
  if (course.userCompleted) return '已完成本课程'
  const sec = course.userProgress || 0
  if (sec <= 0) return '尚未开始或进度未上报'
  const m = Math.floor(sec / 60)
  const s = sec % 60
  const learned = m > 0 ? `已学约 ${m} 分${s ? s + ' 秒' : ''}` : `已学 ${s} 秒`
  return learned
}

function goCourse(id) {
  if (!id) return
  uni.navigateTo({ url: `/pages/course/detail?id=${id}` })
}
</script>

<style scoped>
.page {
  min-height: 100vh;
  background: #f0f2f5;
  padding: 14px;
  padding-bottom: calc(24px + env(safe-area-inset-bottom));
}

.intro {
  background: linear-gradient(135deg, #eff6ff 0%, #fff 100%);
  border-radius: 14px;
  padding: 14px 16px;
  margin-bottom: 16px;
  border: 1px solid #e0e7ff;
}

.intro-title {
  display: block;
  font-size: 15px;
  font-weight: 700;
  color: #1e293b;
  margin-bottom: 6px;
}

.intro-desc {
  font-size: 12px;
  color: #64748b;
  line-height: 1.5;
}

.loading {
  padding: 48px;
  text-align: center;
  color: #94a3b8;
}

.empty-wrap {
  padding: 40px 20px;
  text-align: center;
}

.empty-icon {
  font-size: 40px;
  display: block;
  margin-bottom: 12px;
}

.empty-title {
  display: block;
  font-size: 16px;
  font-weight: 600;
  color: #334155;
  margin-bottom: 8px;
}

.empty-hint {
  font-size: 13px;
  color: #94a3b8;
  line-height: 1.55;
}

.list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.item {
  position: relative;
  display: flex;
  gap: 12px;
  background: #fff;
  border-radius: 14px;
  padding: 12px;
  box-shadow: 0 2px 12px rgba(15, 23, 42, 0.06);
}

.cover {
  width: 100px;
  height: 72px;
  border-radius: 10px;
  flex-shrink: 0;
  background: #e2e8f0;
}

.cover-placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
}

.ph {
  font-size: 22px;
  color: #94a3b8;
  font-weight: 700;
}

.info {
  flex: 1;
  min-width: 0;
}

.name {
  font-size: 15px;
  font-weight: 700;
  color: #0f172a;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.meta-row {
  margin-top: 4px;
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.meta {
  font-size: 11px;
  color: #94a3b8;
}

.bar-wrap {
  margin-top: 8px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.bar-bg {
  flex: 1;
  height: 6px;
  background: #e2e8f0;
  border-radius: 3px;
  overflow: hidden;
}

.bar-fill {
  height: 100%;
  background: linear-gradient(90deg, #3b82f6, #6366f1);
  border-radius: 3px;
  transition: width 0.2s;
}

.pct {
  font-size: 12px;
  font-weight: 700;
  color: #2563eb;
  width: 36px;
  text-align: right;
}

.sub {
  display: block;
  margin-top: 4px;
  font-size: 11px;
  color: #64748b;
}

.done-badge {
  position: absolute;
  top: 10px;
  right: 10px;
  font-size: 10px;
  padding: 2px 8px;
  background: #dcfce7;
  color: #15803d;
  border-radius: 6px;
  font-weight: 600;
}
</style>
