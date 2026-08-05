<template>
  <view class="page">
    <!-- 动作库入口 -->
    <view class="section">
      <view class="section-header">
        <text class="section-title">动作库</text>
        <text class="section-more" @click="goTo('/pages/training/exercise-list')">查看全部</text>
      </view>
      <scroll-view scroll-x class="exercise-scroll">
        <view
          v-for="ex in exercises"
          :key="ex.id"
          class="exercise-card"
          @click="goToDetail(ex.id)"
        >
          <image :src="ex.coverImage || '/static/default-exercise.png'" class="ex-cover" mode="aspectFill" />
          <text class="ex-name">{{ ex.name }}</text>
          <view class="ex-meta">
            <text class="ex-tag">{{ ex.muscleGroupName || ex.muscleGroup || '-' }}</text>
            <text class="ex-tag">{{ ex.difficultyName || difficultyText(ex.difficulty) }}</text>
          </view>
        </view>
      </scroll-view>
    </view>

    <!-- 训练计划 -->
    <view class="section">
      <view class="section-header">
        <text class="section-title">我的训练</text>
        <view class="section-actions">
          <text class="section-more" @click="goTo('/pages/training/history')">训练记录</text>
          <text class="section-more" @click="goTo('/pages/training/plan')">训练计划</text>
        </view>
      </view>
      <view v-if="!currentPlan" class="empty-plan" @click="goTo('/pages/training/plan')">
        <text>暂无训练计划</text>
        <text class="hint">点击生成 AI 训练计划</text>
      </view>
      <view v-else class="plan-card" @click="goTo('/pages/training/plan')">
        <text class="plan-title">{{ currentPlan.title || '我的计划' }}</text>
        <text class="plan-status">{{ planStatusText(currentPlan.status) }}</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getExercisePage, getCurrentPlan } from '@/api/training'

const exercises = ref([])
const currentPlan = ref(null)

onMounted(() => {
  loadExercises()
  loadPlan()
})

async function loadExercises() {
  try {
    const res = await getExercisePage({ pageNum: 1, pageSize: 8 })
    exercises.value = res.data?.records || []
  } catch (e) {}
}

async function loadPlan() {
  try {
    const res = await getCurrentPlan()
    currentPlan.value = res.data
  } catch (e) {
    currentPlan.value = null
  }
}

function difficultyText(d) {
  const map = { 1: '入门', 2: '进阶', 3: '高级' }
  return map[d] || '-'
}

function planStatusText(s) {
  const map = { 0: '草稿', 1: '进行中', 2: '已完成', 3: '已放弃' }
  return map[s] || '-'
}

function goTo(url) {
  uni.navigateTo({ url })
}

function goToDetail(id) {
  uni.navigateTo({ url: `/pages/training/exercise-detail?id=${id}` })
}
</script>

<style lang="scss" scoped>
.page {
  min-height: 100vh;
  background: #f5f5f5;
  padding: 16px;
}

.section {
  margin-bottom: 16px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.section-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.section-title {
  font-size: 16px;
  font-weight: bold;
  color: #333;
}

.section-more {
  font-size: 12px;
  color: #3b82f6;
}

.exercise-scroll {
  white-space: nowrap;
}

.exercise-card {
  display: inline-block;
  width: 140px;
  margin-right: 12px;
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
}

.ex-cover {
  width: 100%;
  height: 100px;
}

.ex-name {
  display: block;
  padding: 8px;
  font-size: 14px;
  color: #333;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.ex-meta {
  padding: 0 8px 8px;
  display: flex;
  gap: 8px;
}

.ex-tag {
  font-size: 11px;
  color: #999;
}

.empty-plan {
  padding: 40px;
  background: #fff;
  border-radius: 12px;
  text-align: center;
  color: #999;
}

.hint {
  display: block;
  font-size: 12px;
  margin-top: 8px;
}

.plan-card {
  padding: 16px;
  background: #fff;
  border-radius: 12px;
}

.plan-title {
  font-size: 16px;
  font-weight: bold;
  color: #333;
}

.plan-status {
  display: block;
  font-size: 12px;
  color: #666;
  margin-top: 4px;
}
</style>
