<template>
  <view class="page">
    <view class="filter-bar">
      <input v-model="query.keyword" class="search-input" placeholder="搜索动作" @confirm="onSearch" />
      <view class="filter-btn" @click="onSearch">搜索</view>
    </view>
    <scroll-view scroll-y class="list" :style="{ height: listHeight + 'px' }" @scrolltolower="loadMore">
      <view v-if="loading && list.length === 0" class="loading">加载中...</view>
      <view v-else-if="list.length === 0" class="empty">暂无动作</view>
      <view v-else class="grid">
        <view v-for="ex in list" :key="ex.id" class="card" @click="goDetail(ex.id)">
          <image :src="ex.coverImage || '/static/default-exercise.png'" class="cover" mode="aspectFill" />
          <text class="name">{{ ex.name }}</text>
          <view class="meta">
            <text class="tag">{{ ex.muscleGroupName || ex.muscleGroup || '-' }}</text>
            <text class="tag">{{ diffText(ex.difficulty) }}</text>
          </view>
        </view>
      </view>
    </scroll-view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getExercisePage } from '@/api/training'

const query = ref({ pageNum: 1, pageSize: 12, keyword: '' })
const list = ref([])
const total = ref(0)
const loading = ref(false)
const listHeight = ref(500)

function diffText(d) {
  return { 1: '入门', 2: '进阶', 3: '高级' }[d] || '-'
}

onMounted(() => {
  listHeight.value = uni.getSystemInfoSync().windowHeight - 120
  load()
})

async function load(append = false) {
  if (loading.value) return
  loading.value = true
  try {
    const res = await getExercisePage(query.value)
    const data = res.data || {}
    const rows = data.records || []
    total.value = data.total || 0
    list.value = append ? [...list.value, ...rows] : rows
  } catch (e) {}
  loading.value = false
}

function onSearch() {
  query.value.pageNum = 1
  load()
}

function loadMore() {
  if (list.value.length >= total.value) return
  query.value.pageNum++
  load(true)
}

function goDetail(id) {
  uni.navigateTo({ url: `/pages/training/exercise-detail?id=${id}` })
}
</script>

<style lang="scss" scoped>
.page {
  min-height: 100vh;
  background: #f5f5f5;
  padding: 12px;
}

.filter-bar {
  display: flex;
  gap: 12px;
  margin-bottom: 12px;
}

.search-input {
  flex: 1;
  height: 36px;
  padding: 0 12px;
  background: #fff;
  border-radius: 8px;
  font-size: 14px;
}

.filter-btn {
  padding: 0 16px;
  height: 36px;
  line-height: 36px;
  background: #3b82f6;
  color: #fff;
  border-radius: 8px;
  font-size: 14px;
}

.list {
  padding: 0;
}

.loading,
.empty {
  padding: 60px;
  text-align: center;
  color: #999;
}

.grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

.card {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
}

.cover {
  width: 100%;
  height: 120px;
}

.name {
  display: block;
  padding: 8px;
  font-size: 14px;
  color: #333;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.meta {
  padding: 0 8px 8px;
  display: flex;
  gap: 8px;
}

.tag {
  font-size: 11px;
  color: #999;
}
</style>
