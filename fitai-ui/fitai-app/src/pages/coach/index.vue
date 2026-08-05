<template>
  <view class="page">
    <!-- 筛选 -->
    <view class="filter-bar">
      <input
        v-model="query.keyword"
        class="search-input"
        placeholder="搜索教练姓名"
        @confirm="handleSearch"
      />
      <picker :range="specialtyOptions" @change="onSpecialtyChange">
        <view class="filter-select">
          <text>{{ currentSpecialtyLabel }}</text>
          <text class="arrow">▼</text>
        </view>
      </picker>
      <view class="search-btn" @click="handleSearch">搜索</view>
    </view>

    <!-- 教练列表 -->
    <scroll-view
      scroll-y
      class="coach-list"
      :style="{ height: listHeight + 'px' }"
      @scrolltolower="loadMore"
    >
      <view v-if="loading && coaches.length === 0" class="loading-wrap">
        <text>加载中...</text>
      </view>
      <view v-else-if="coaches.length === 0" class="empty-wrap">
        <text>暂无教练</text>
      </view>
      <view v-else>
        <view
          v-for="c in coaches"
          :key="c.id"
          class="coach-item"
          @click="goToDetail(c.id)"
        >
          <image :src="c.avatar || '/static/default-avatar.png'" class="coach-avatar" mode="aspectFill" />
          <view class="coach-info">
            <view class="coach-head">
              <text class="coach-name">{{ c.name }}</text>
              <text class="coach-title">{{ c.title || '健身教练' }}</text>
            </view>
            <view class="tag-row" v-if="splitTags(c.specialty).length">
              <text v-for="t in splitTags(c.specialty)" :key="t" class="tag">{{ t }}</text>
            </view>
            <view class="coach-meta">
              <text class="rating">评分 {{ formatRating(c.rating) }}</text>
              <text class="students">学员 {{ c.studentCount || 0 }}</text>
            </view>
          </view>
          <view class="coach-price">
            <text class="price-num">¥{{ formatPrice(c.price) }}</text>
            <text class="price-unit">/课时</text>
          </view>
        </view>
        <view v-if="loading" class="more-hint">加载中...</view>
        <view v-else-if="coaches.length >= total" class="more-hint">没有更多了</view>
      </view>
    </scroll-view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getCoachList } from '@/api/coach'

const SPECIALTIES = ['增肌塑形', '减脂瘦身', '体态矫正', '康复训练', '力量训练', '有氧训练', '瑜伽', '普拉提', '搏击']

const query = ref({ pageNum: 1, pageSize: 10, specialty: '', keyword: '' })
const coaches = ref([])
const total = ref(0)
const loading = ref(false)
const listHeight = ref(500)

const specialtyOptions = ref(['全部专长', ...SPECIALTIES])
const currentSpecialtyLabel = ref('全部专长')

onMounted(() => {
  const sys = uni.getSystemInfoSync()
  listHeight.value = sys.windowHeight - 120
  loadCoaches()
})

async function loadCoaches(append = false) {
  if (loading.value) return
  loading.value = true
  try {
    const res = await getCoachList(query.value)
    const data = res.data || {}
    const list = data.records || []
    total.value = Number(data.total) || 0
    coaches.value = append ? [...coaches.value, ...list] : list
  } catch (e) {
    uni.showToast({ title: '加载失败', icon: 'none' })
  }
  loading.value = false
}

function splitTags(specialty) {
  if (!specialty) return []
  return String(specialty)
    .split(/[,，]/)
    .map((t) => t.trim())
    .filter((t) => t)
}

function formatRating(rating) {
  const n = Number(rating)
  return Number.isFinite(n) && n > 0 ? n.toFixed(1) : '暂无'
}

function formatPrice(price) {
  const n = Number(price)
  return Number.isFinite(n) ? n.toFixed(2) : '0.00'
}

function onSpecialtyChange(e) {
  const idx = Number(e.detail.value)
  const label = specialtyOptions.value[idx]
  currentSpecialtyLabel.value = label
  query.value.specialty = idx === 0 ? '' : label
  query.value.pageNum = 1
  loadCoaches()
}

function handleSearch() {
  query.value.pageNum = 1
  loadCoaches()
}

function loadMore() {
  if (loading.value) return
  if (coaches.value.length >= total.value) return
  query.value.pageNum++
  loadCoaches(true)
}

function goToDetail(id) {
  uni.navigateTo({ url: `/pages/coach/detail?id=${id}` })
}
</script>

<style lang="scss" scoped>
.page {
  min-height: 100vh;
  background: #f5f5f5;
}

.filter-bar {
  display: flex;
  padding: 12px;
  gap: 12px;
  background: #fff;
}

.search-input {
  flex: 1;
  height: 36px;
  padding: 0 12px;
  background: #f5f5f5;
  border-radius: 8px;
  font-size: 14px;
}

.filter-select {
  display: flex;
  align-items: center;
  padding: 0 12px;
  min-width: 90px;
  height: 36px;
  background: #f5f5f5;
  border-radius: 8px;
  font-size: 14px;
}

.arrow {
  font-size: 10px;
  color: #666;
  margin-left: 4px;
}

.search-btn {
  padding: 0 16px;
  height: 36px;
  line-height: 36px;
  background: #3b82f6;
  color: #fff;
  border-radius: 8px;
  font-size: 14px;
}

.coach-list {
  padding: 12px;
}

.loading-wrap,
.empty-wrap {
  padding: 60px;
  text-align: center;
  color: #999;
}

.more-hint {
  padding: 14px;
  text-align: center;
  color: #9ca3af;
  font-size: 12px;
}

.coach-item {
  display: flex;
  align-items: center;
  gap: 12px;
  background: #fff;
  border-radius: 12px;
  padding: 12px;
  margin-bottom: 10px;
}

.coach-avatar {
  width: 60px;
  height: 60px;
  border-radius: 30px;
  background: #f3f4f6;
}

.coach-info {
  flex: 1;
  overflow: hidden;
}

.coach-head {
  display: flex;
  align-items: center;
  gap: 8px;
}

.coach-name {
  font-size: 16px;
  font-weight: 600;
  color: #111;
}

.coach-title {
  font-size: 12px;
  color: #6b7280;
}

.tag-row {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  margin-top: 6px;
}

.tag {
  padding: 2px 8px;
  background: #dbeafe;
  color: #2563eb;
  border-radius: 10px;
  font-size: 11px;
}

.coach-meta {
  display: flex;
  gap: 12px;
  margin-top: 6px;
  font-size: 12px;
  color: #9ca3af;
}

.rating {
  color: #f59e0b;
}

.coach-price {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
}

.price-num {
  font-size: 16px;
  color: #ef4444;
  font-weight: 600;
}

.price-unit {
  font-size: 11px;
  color: #9ca3af;
}
</style>
