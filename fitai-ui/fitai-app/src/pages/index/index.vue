<template>
  <view class="page">
    <!-- 自定义导航栏 -->
    <view class="navbar" :style="{ paddingTop: statusBarHeight + 'px' }">
      <view class="navbar-content">
        <text class="logo">FitAI</text>
        <view class="navbar-right">
          <view class="icon-btn" @click="goToMessage">
            <IconifyIcon icon="mdi:bell-outline" :size="22" color="#fff" />
            <view class="badge" v-if="unreadCount > 0">{{ unreadCount > 99 ? '99+' : unreadCount }}</view>
          </view>
        </view>
      </view>
    </view>

    <!-- 页面内容 -->
    <scroll-view scroll-y class="content" :style="{ paddingTop: navbarHeight + 'px' }">
      <!-- 轮播图 -->
      <swiper class="banner" circular autoplay :interval="4000">
        <swiper-item v-for="item in displayBanners" :key="item.id" @click="handleBannerClick(item)">
          <image :src="item.imageUrl" mode="aspectFill" class="banner-img" />
        </swiper-item>
      </swiper>

      <!-- 快捷入口 -->
      <view class="quick-entry">
        <view class="entry-item" @click="goTo('/pages/training/exercise-list')">
          <view class="entry-icon bg-blue">
            <IconifyIcon icon="mdi:dumbbell" :size="24" color="#fff" />
          </view>
          <text class="entry-text">动作库</text>
        </view>
        <view class="entry-item" @click="goTo('/pages/training/plan')">
          <view class="entry-icon bg-cyan">
            <IconifyIcon icon="mdi:clipboard-list-outline" :size="24" color="#fff" />
          </view>
          <text class="entry-text">训练计划</text>
        </view>
        <view class="entry-item" @click="goTo('/pages/my/checkin')">
          <view class="entry-icon bg-green">
            <IconifyIcon icon="mdi:calendar-check" :size="24" color="#fff" />
          </view>
          <text class="entry-text">每日签到</text>
        </view>
        <view class="entry-item" @click="goTo('/pages/ai/index')">
          <view class="entry-icon bg-purple">
            <IconifyIcon icon="mdi:robot-outline" :size="24" color="#fff" />
          </view>
          <text class="entry-text">AI助手</text>
        </view>
        <view class="entry-item" @click="goTo('/pages/my/member')">
          <view class="entry-icon bg-orange">
            <IconifyIcon icon="mdi:crown" :size="24" color="#fff" />
          </view>
          <text class="entry-text">会员中心</text>
        </view>
        <view class="entry-item" @click="goTo('/pages/diet/index')">
          <view class="entry-icon bg-teal">
            <IconifyIcon icon="mdi:food-apple-outline" :size="24" color="#fff" />
          </view>
          <text class="entry-text">饮食</text>
        </view>
      </view>

      <!-- 推荐课程 -->
      <view class="section">
        <view class="section-header">
          <text class="section-title">推荐课程</text>
          <text class="section-more" @click="goTo('/pages/course/index')">更多 ></text>
        </view>
        <scroll-view scroll-x class="course-list">
          <view class="course-item" v-for="course in recommendCourses" :key="course.id" @click="goToCourse(course.id)">
            <image :src="course.coverImage" mode="aspectFill" class="course-cover" />
            <view class="course-info">
              <text class="course-title">{{ course.title }}</text>
              <view class="course-meta">
                <text class="course-duration">{{ course.duration }}分钟</text>
                <text class="course-calories">{{ course.calories }}卡</text>
              </view>
            </view>
          </view>
        </scroll-view>
      </view>

      <!-- 热门商品 -->
      <view class="section">
        <view class="section-header">
          <text class="section-title">热门好物</text>
          <text class="section-more" @click="goTo('/pages/shop/index')">更多 ></text>
        </view>
        <view class="product-grid">
          <view class="product-item" v-for="product in hotProducts" :key="product.id" @click="goToProduct(product.id)">
            <image :src="product.coverImage" mode="aspectFill" class="product-cover" />
            <text class="product-name">{{ product.name }}</text>
            <view class="product-price">
              <text class="price-symbol">¥</text>
              <text class="price-value">{{ product.minPrice }}</text>
            </view>
          </view>
        </view>
      </view>
    </scroll-view>
  </view>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import http from '@/utils/request'

// 默认轮播图（无数据时展示）
const DEFAULT_BANNER = { id: 'default', imageUrl: '/static/banner-default.png', linkType: null, linkValue: null }

// 状态栏高度
const statusBarHeight = ref(0)
const navbarHeight = ref(88)
const unreadCount = ref(0)
const banners = ref([])
const displayBanners = computed(() => {
  if (banners.value && banners.value.length > 0) return banners.value
  return [DEFAULT_BANNER]
})
const recommendCourses = ref([])
const hotProducts = ref([])

onMounted(() => {
  // 获取状态栏高度
  const systemInfo = uni.getSystemInfoSync()
  statusBarHeight.value = systemInfo.statusBarHeight
  navbarHeight.value = systemInfo.statusBarHeight + 44
  
  // 加载数据
  loadBanners()
  loadRecommendCourses()
  loadHotProducts()
  loadUnreadCount()
})

async function loadBanners() {
  try {
    const res = await http.get('/banner/list', { position: 'home' })
    banners.value = res.data || []
  } catch (e) {
    console.error('加载轮播图失败', e)
  }
}

async function loadRecommendCourses() {
  try {
    const res = await http.get('/course/page', { pageNum: 1, pageSize: 6 })
    recommendCourses.value = res.data?.records || []
  } catch (e) {
    console.error('加载推荐课程失败', e)
  }
}

async function loadHotProducts() {
  try {
    const res = await http.get('/product/page', { pageNum: 1, pageSize: 4 })
    hotProducts.value = res.data?.records || []
  } catch (e) {
    console.error('加载热门商品失败', e)
  }
}

async function loadUnreadCount() {
  try {
    const res = await http.get('/message/unread-count')
    unreadCount.value = res.data?.count || 0
  } catch (e) {
    console.error('加载未读消息失败', e)
  }
}

/** 与 pages.json tabBar.list 中 pagePath 一致（勿用 navigateTo 打开） */
const TABBAR_PAGE_PATHS = new Set([
  'pages/index/index',
  'pages/course/index',
  'pages/ai/index',
  'pages/shop/index',
  'pages/my/index'
])

function isTabBarUrl(url) {
  if (!url || typeof url !== 'string') return false
  const path = url.split('?')[0].replace(/^\//, '')
  return TABBAR_PAGE_PATHS.has(path)
}

function goTo(url) {
  if (isTabBarUrl(url)) {
    const path = url.split('?')[0]
    uni.switchTab({ url: path.startsWith('/') ? path : `/${path}` })
    return
  }
  uni.navigateTo({ url })
}

function goToMessage() {
  uni.navigateTo({ url: '/pages/my/message' })
}

function goToCourse(id) {
  uni.navigateTo({ url: `/pages/course/detail?id=${id}` })
}

function goToProduct(id) {
  uni.navigateTo({ url: `/pages/shop/detail?id=${id}` })
}

function handleBannerClick(item) {
  if (!item.linkType || item.id === 'default') return
  if (item.linkType === 'url') {
    // #ifdef H5
    window.open(item.linkValue)
    // #endif
  } else if (item.linkType === 'page') {
    goTo(item.linkValue)
  } else if (item.linkType === 'product') {
    goToProduct(item.linkValue)
  } else if (item.linkType === 'course') {
    goToCourse(item.linkValue)
  }
}
</script>

<style lang="scss" scoped>
.page {
  min-height: 100vh;
  background-color: #f5f5f5;
}

.navbar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
  z-index: 100;
}

.navbar-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 44px;
  padding: 0 16px;
}

.logo {
  font-size: 20px;
  font-weight: bold;
  color: #fff;
}

.icon-btn {
  position: relative;
  padding: 8px;
}

.badge {
  position: absolute;
  top: 4px;
  right: 4px;
  min-width: 16px;
  height: 16px;
  padding: 0 4px;
  font-size: 10px;
  color: #fff;
  background-color: #ef4444;
  border-radius: 8px;
  text-align: center;
  line-height: 16px;
}

.content {
  padding-bottom: 20px;
}

.banner {
  height: 180px;
  margin: 12px;
  border-radius: 12px;
  overflow: hidden;
}

.banner-img {
  width: 100%;
  height: 100%;
}

.quick-entry {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px 8px;
  padding: 16px;
  margin: 0 12px;
  background-color: #fff;
  border-radius: 12px;
}

.entry-item {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.entry-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 8px;
}

.entry-icon :deep(.iconify-icon) {
  display: flex;
  align-items: center;
  justify-content: center;
}

.bg-blue { background: linear-gradient(135deg, #3b82f6, #60a5fa); }
.bg-cyan { background: linear-gradient(135deg, #06b6d4, #22d3ee); }
.bg-green { background: linear-gradient(135deg, #22c55e, #4ade80); }
.bg-purple { background: linear-gradient(135deg, #8b5cf6, #a78bfa); }
.bg-orange { background: linear-gradient(135deg, #f59e0b, #fbbf24); }
.bg-teal { background: linear-gradient(135deg, #0d9488, #14b8a6); }

.entry-text {
  font-size: 12px;
  color: #666;
}

.section {
  margin: 16px 12px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.section-title {
  font-size: 16px;
  font-weight: bold;
  color: #333;
}

.section-more {
  font-size: 12px;
  color: #999;
}

.course-list {
  white-space: nowrap;
}

.course-item {
  display: inline-block;
  width: 150px;
  margin-right: 12px;
  background-color: #fff;
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
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.course-meta {
  display: flex;
  gap: 8px;
  margin-top: 4px;
}

.course-duration,
.course-calories {
  font-size: 12px;
  color: #999;
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

.product-item {
  background-color: #fff;
  border-radius: 12px;
  overflow: hidden;
  padding-bottom: 12px;
}

.product-cover {
  width: 100%;
  height: 150px;
}

.product-name {
  display: block;
  padding: 8px 8px 0;
  font-size: 14px;
  color: #333;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.product-price {
  padding: 4px 8px 0;
  color: #ef4444;
}

.price-symbol {
  font-size: 12px;
}

.price-value {
  font-size: 16px;
  font-weight: bold;
}
</style>
