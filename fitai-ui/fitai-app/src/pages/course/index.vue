<template>
  <view class="page">
    <!-- 筛选 -->
    <view class="filter-bar">
      <input
        v-model="query.keyword"
        class="search-input"
        placeholder="搜索课程"
        @confirm="handleSearch"
      />
      <picker :range="categoryLabels" range-key="name" @change="onCategoryChange">
        <view class="filter-select">
          <text>{{ currentCategoryLabel }}</text>
          <text class="arrow">▼</text>
        </view>
      </picker>
      <view class="search-btn" @click="handleSearch">搜索</view>
    </view>

    <!-- 课程列表 -->
    <scroll-view
      scroll-y
      class="course-list"
      :style="{ height: listHeight + 'px' }"
      @scrolltolower="loadMore"
    >
      <view v-if="loading && courses.length === 0" class="loading-wrap">
        <text>加载中...</text>
      </view>
      <view v-else-if="courses.length === 0" class="empty-wrap">
        <text>暂无课程</text>
      </view>
      <view v-else class="course-grid">
        <view
          v-for="c in courses"
          :key="c.id"
          class="course-item"
          @click="goToDetail(c.id)"
        >
          <image :src="c.coverImage || '/static/default-course.png'" class="course-cover" mode="aspectFill" />
          <view class="course-info">
            <text class="course-title">{{ c.title }}</text>
            <view class="course-meta">
              <text class="course-duration">{{ c.duration }}分钟</text>
              <text class="course-calories">{{ c.calories }}卡</text>
              <text class="course-difficulty">{{ c.difficultyName || '-' }}</text>
            </view>
          </view>
        </view>
      </view>
    </scroll-view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getCourseList, getCourseCategories } from '@/api/course'

const query = ref({ pageNum: 1, pageSize: 12, categoryId: null, keyword: '' })
const courses = ref([])
const categories = ref([])
const total = ref(0)
const loading = ref(false)
const listHeight = ref(500)

const categoryLabels = ref([{ id: null, name: '全部分类' }])
const currentCategoryLabel = ref('全部分类')

onMounted(() => {
  const sys = uni.getSystemInfoSync()
  listHeight.value = sys.windowHeight - 120
  loadCategories()
  loadCourses()
})

async function loadCategories() {
  try {
    const res = await getCourseCategories()
    const raw = res.data || []
    const list = flattenCategories(raw).filter(c => c.id != null)
    categories.value = list
    categoryLabels.value = [{ id: null, name: '全部分类' }, ...list]
  } catch (e) {}
}

function flattenCategories(arr) {
  if (!Array.isArray(arr)) return []
  return arr.reduce((acc, c) => {
    acc.push({ id: c.id, name: c.name })
    if (c.children?.length) acc.push(...flattenCategories(c.children))
    return acc
  }, [])
}

async function loadCourses(append = false) {
  if (loading.value) return
  loading.value = true
  try {
    const res = await getCourseList(query.value)
    const data = res.data || {}
    const list = data.records || []
    total.value = data.total || 0
    courses.value = append ? [...courses.value, ...list] : list
  } catch (e) {
    uni.showToast({ title: '加载失败', icon: 'none' })
  }
  loading.value = false
}

function onCategoryChange(e) {
  const idx = e.detail.value
  const item = categoryLabels.value[idx]
  query.value.categoryId = item.id
  currentCategoryLabel.value = item.name
  query.value.pageNum = 1
  loadCourses()
}

function handleSearch() {
  query.value.pageNum = 1
  loadCourses()
}

function loadMore() {
  if (courses.value.length >= total.value) return
  query.value.pageNum++
  loadCourses(true)
}

function goToDetail(id) {
  uni.navigateTo({ url: `/pages/course/detail?id=${id}` })
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

.course-list {
  padding: 12px;
}

.loading-wrap,
.empty-wrap {
  padding: 60px;
  text-align: center;
  color: #999;
}

.course-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

.course-item {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
}

.course-cover {
  width: 100%;
  height: 100px;
}

.course-info {
  padding: 8px;
}

.course-title {
  font-size: 14px;
  color: #333;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.course-meta {
  display: flex;
  gap: 8px;
  margin-top: 4px;
  font-size: 12px;
  color: #999;
}

.course-duration,
.course-calories,
.course-difficulty {
  font-size: 12px;
}
</style>
