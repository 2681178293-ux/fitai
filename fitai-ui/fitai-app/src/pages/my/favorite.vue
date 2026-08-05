<template>
  <view class="page">
    <view class="tab-bar">
      <view
        class="tab-item"
        :class="{ active: activeTab === 'course' }"
        @click="activeTab = 'course'; loadTab()"
      >
        课程
      </view>
      <view
        class="tab-item"
        :class="{ active: activeTab === 'product' }"
        @click="activeTab = 'product'; loadTab()"
      >
        商品
      </view>
    </view>

    <view v-if="loading && list.length === 0" class="loading-wrap">
      <text>加载中...</text>
    </view>
    <view v-else-if="list.length === 0" class="empty-wrap">
      <text>暂无收藏</text>
      <text class="empty-desc">{{ activeTab === 'course' ? '去课程页收藏喜欢的课程吧' : '去商城收藏喜欢的商品吧' }}</text>
      <view class="action-btn" @click="goToBrowse">{{ activeTab === 'course' ? '去逛逛' : '去商城' }}</view>
    </view>
    <scroll-view
      v-else
      scroll-y
      class="scroll-list"
      :style="{ height: listHeight + 'px' }"
      @scrolltolower="loadMore"
    >
      <!-- 课程列表 -->
      <view v-if="activeTab === 'course'" class="course-list">
        <view
          v-for="item in list"
          :key="item.id"
          class="course-item"
          @click="goToDetail(item.id)"
        >
          <image :src="item.coverImage || '/static/banner-default.png'" class="course-cover" mode="aspectFill" />
          <view class="course-info">
            <text class="course-title">{{ item.title }}</text>
            <view class="course-meta">
              <text>{{ item.duration }}分钟</text>
              <text>{{ item.calories }}卡</text>
              <text>{{ item.difficultyName || '-' }}</text>
            </view>
            <view class="course-actions">
              <view class="like-btn active" @click.stop="handleUnfavoriteCourse(item)">
                <IconifyIcon icon="mdi:heart" :size="18" color="#ef4444" />
                <text>取消收藏</text>
              </view>
            </view>
          </view>
        </view>
      </view>
      <!-- 商品列表 -->
      <view v-else class="product-list">
        <view
          v-for="item in list"
          :key="item.id"
          class="product-item"
          @click="goToProduct(item.id)"
        >
          <image :src="item.coverImage || '/static/default-product.png'" class="product-cover" mode="aspectFill" />
          <view class="product-info">
            <text class="product-name">{{ item.name }}</text>
            <text class="product-price">¥{{ item.minPrice }}</text>
            <view class="product-actions">
              <view class="like-btn active" @click.stop="handleUnfavoriteProduct(item)">
                <IconifyIcon icon="mdi:star" :size="18" color="#f59e0b" />
                <text>取消收藏</text>
              </view>
            </view>
          </view>
        </view>
      </view>
    </scroll-view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getFavoriteCourses, toggleFavorite } from '@/api/course'
import { getFavoriteProducts, toggleFavoriteProduct } from '@/api/product'

const activeTab = ref('course')
const list = ref([])
const loading = ref(true)
/** 分页加载中，与首屏 loading 分离，避免挡住 Tab 切换后的新请求 */
const loadingMore = ref(false)
const pageNum = ref(1)
const total = ref(0)
const listHeight = ref(500)
/** 切换 Tab 时递增，用于丢弃过期的异步结果，避免串数据 */
let loadEpoch = 0

onMounted(() => {
  const sys = uni.getSystemInfoSync()
  listHeight.value = sys.windowHeight - 44 - 48
  loadTab()
})

function loadTab() {
  loadEpoch += 1
  const epoch = loadEpoch
  pageNum.value = 1
  list.value = []
  loadingMore.value = false
  if (activeTab.value === 'course') loadCourses(false, epoch)
  else loadProducts(false, epoch)
}

async function loadCourses(append = false, epoch = loadEpoch) {
  if (append) {
    if (loadingMore.value) return
    loadingMore.value = true
  } else {
    loading.value = true
  }
  try {
    const res = await getFavoriteCourses({ pageNum: pageNum.value, pageSize: 10 })
    if (epoch !== loadEpoch) return
    const data = res.data || {}
    const records = data.records || []
    total.value = data.total || 0
    list.value = append ? [...list.value, ...records] : records
  } catch (e) {
    if (epoch !== loadEpoch) return
    list.value = append ? list.value : []
    uni.showToast({ title: e.msg || '加载失败', icon: 'none' })
  } finally {
    if (append) {
      loadingMore.value = false
    } else if (epoch === loadEpoch) {
      loading.value = false
    }
  }
}

async function loadProducts(append = false, epoch = loadEpoch) {
  if (append) {
    if (loadingMore.value) return
    loadingMore.value = true
  } else {
    loading.value = true
  }
  try {
    const res = await getFavoriteProducts({ pageNum: pageNum.value, pageSize: 10 })
    if (epoch !== loadEpoch) return
    const data = res.data || {}
    const records = data.records || []
    total.value = data.total || 0
    list.value = append ? [...list.value, ...records] : records
  } catch (e) {
    if (epoch !== loadEpoch) return
    list.value = append ? list.value : []
    uni.showToast({ title: e.msg || '加载失败', icon: 'none' })
  } finally {
    if (append) {
      loadingMore.value = false
    } else if (epoch === loadEpoch) {
      loading.value = false
    }
  }
}

function loadMore() {
  if (list.value.length >= total.value) return
  pageNum.value++
  if (activeTab.value === 'course') loadCourses(true, loadEpoch)
  else loadProducts(true, loadEpoch)
}

async function handleUnfavoriteCourse(item) {
  try {
    await toggleFavorite(item.id)
    list.value = list.value.filter(i => i.id !== item.id)
    total.value = Math.max(0, total.value - 1)
    uni.showToast({ title: '已取消收藏' })
  } catch (e) {
    uni.showToast({ title: '操作失败', icon: 'none' })
  }
}

async function handleUnfavoriteProduct(item) {
  try {
    await toggleFavoriteProduct(item.id)
    list.value = list.value.filter(i => i.id !== item.id)
    total.value = Math.max(0, total.value - 1)
    uni.showToast({ title: '已取消收藏' })
  } catch (e) {
    uni.showToast({ title: '操作失败', icon: 'none' })
  }
}

function goToDetail(id) {
  uni.navigateTo({ url: `/pages/course/detail?id=${id}` })
}

function goToProduct(id) {
  uni.navigateTo({ url: `/pages/shop/detail?id=${id}` })
}

function goToBrowse() {
  if (activeTab.value === 'course') {
    uni.switchTab({ url: '/pages/course/index' })
  } else {
    uni.switchTab({ url: '/pages/shop/index' })
  }
}
</script>

<style lang="scss" scoped>
.page {
  min-height: 100vh;
  background: #f5f5f5;
}

.tab-bar {
  display: flex;
  background: #fff;
  padding: 0 16px;
  height: 48px;
}

.tab-item {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 15px;
  color: #666;
  border-bottom: 3px solid transparent;
}

.tab-item.active {
  font-weight: 500;
  color: #10b981;
  border-bottom-color: #10b981;
}

.loading-wrap,
.empty-wrap {
  padding: 60px;
  text-align: center;
  color: #999;
}

.empty-desc {
  display: block;
  margin-top: 8px;
  font-size: 14px;
}

.action-btn {
  display: inline-block;
  margin-top: 20px;
  padding: 10px 24px;
  background: #3b82f6;
  color: #fff;
  border-radius: 20px;
  font-size: 14px;
}

.scroll-list {
  padding: 12px;
}

.course-list,
.product-list {
  padding: 0;
}

.course-item,
.product-item {
  display: flex;
  gap: 12px;
  padding: 12px;
  margin-bottom: 12px;
  background: #fff;
  border-radius: 12px;
}

.course-cover,
.product-cover {
  width: 120px;
  height: 90px;
  border-radius: 8px;
  background: #f0f0f0;
}

.course-info,
.product-info {
  flex: 1;
  overflow: hidden;
}

.course-title,
.product-name {
  display: block;
  font-size: 15px;
  font-weight: 500;
  color: #333;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.product-price {
  display: block;
  margin-top: 8px;
  font-size: 16px;
  font-weight: 600;
  color: #ef4444;
}

.course-meta {
  margin-top: 8px;
  font-size: 12px;
  color: #999;
}

.course-actions,
.product-actions {
  margin-top: 8px;
}

.like-btn {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #999;
}
.like-btn.active {
  color: #ef4444;
}

.product-item .like-btn.active {
  color: #f59e0b;
}
</style>
