<template>
  <view class="page">
    <view v-if="loading" class="loading">加载中...</view>
    <view v-else-if="detail" class="content">
      <image :src="detail.coverImage || '/static/default-exercise.png'" class="cover" mode="aspectFill" />
      <view class="info">
        <text class="name">{{ detail.name }}</text>
        <view class="tags">
          <text class="tag">{{ detail.muscleGroupName || detail.muscleGroup || '-' }}</text>
          <text class="tag">{{ detail.equipmentName || detail.equipment || '-' }}</text>
          <text class="tag">{{ diffText(detail.difficulty) }}</text>
        </view>
        <text v-if="detail.caloriesPerMin" class="cal">约 {{ detail.caloriesPerMin }} 卡/分钟</text>
      </view>
      <view v-if="detail.description" class="block">
        <text class="block-title">动作描述</text>
        <text class="block-text">{{ detail.description }}</text>
      </view>
      <view v-if="detail.tips" class="block">
        <text class="block-title">动作要点</text>
        <text class="block-text">{{ detail.tips }}</text>
      </view>
      <view v-if="detail.commonMistakes" class="block">
        <text class="block-title">常见错误</text>
        <text class="block-text">{{ detail.commonMistakes }}</text>
      </view>
      <view v-if="detail.videoUrl" class="video-area">
        <video :src="detail.videoUrl" controls class="video" />
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getExerciseById } from '@/api/training'

const detail = ref(null)
const loading = ref(true)

function diffText(d) {
  return { 1: '入门', 2: '进阶', 3: '高级' }[d] || '-'
}

onMounted(async () => {
  const pages = getCurrentPages()
  const page = pages[pages.length - 1]
  const opts = page.options || page.$page?.options || {}
  const id = opts.id ? parseInt(opts.id) : 0
  if (!id) {
    uni.showToast({ title: '参数错误', icon: 'none' })
    loading.value = false
    return
  }
  try {
    const res = await getExerciseById(id)
    detail.value = res.data
  } catch (e) {
    uni.showToast({ title: '加载失败', icon: 'none' })
  }
  loading.value = false
})
</script>

<style lang="scss" scoped>
.page {
  min-height: 100vh;
  background: #f5f5f5;
}

.loading {
  padding: 60px;
  text-align: center;
  color: #999;
}

.content {
  padding-bottom: 20px;
}

.cover {
  width: 100%;
  height: 220px;
  background: #e5e7eb;
}

.info {
  padding: 16px;
  background: #fff;
}

.name {
  font-size: 18px;
  font-weight: bold;
  color: #333;
}

.tags {
  display: flex;
  gap: 8px;
  margin-top: 8px;
}

.tag {
  font-size: 12px;
  color: #666;
  padding: 2px 8px;
  background: #f3f4f6;
  border-radius: 4px;
}

.cal {
  display: block;
  font-size: 12px;
  color: #666;
  margin-top: 8px;
}

.block {
  margin-top: 12px;
  padding: 16px;
  background: #fff;
}

.block-title {
  font-size: 14px;
  font-weight: bold;
  color: #333;
}

.block-text {
  display: block;
  font-size: 14px;
  color: #666;
  margin-top: 8px;
  white-space: pre-wrap;
}

.video-area {
  margin-top: 12px;
  padding: 16px;
  background: #fff;
}

.video {
  width: 100%;
  height: 200px;
}
</style>
