<template>
  <view class="page">
    <!-- 秒杀入口 -->
    <view class="seckill-banner" @click="goTo('/pages/seckill/index')">
      <view class="banner-content">
        <text class="banner-icon">🔥</text>
        <view class="banner-text">
          <text class="banner-title">限时秒杀</text>
          <text class="banner-desc">超低价抢购，手慢无！</text>
        </view>
        <text class="banner-btn">立即查看</text>
      </view>
    </view>

    <!-- 筛选 -->
    <view class="filter-bar">
      <input
        v-model="query.keyword"
        class="search-input"
        placeholder="搜索商品"
        @confirm="handleSearch"
      />
      <picker :range="categoryLabels" range-key="name" @change="onCategoryChange">
        <view class="filter-select">
          <text>{{ currentCategoryLabel }}</text>
          <text class="iconfont icon-arrow-down"></text>
        </view>
      </picker>
      <view class="search-btn" @click="handleSearch">搜索</view>
    </view>

    <!-- 商品列表 -->
    <scroll-view
      scroll-y
      class="product-list"
      :style="{ height: listHeight + 'px' }"
      @scrolltolower="loadMore"
    >
      <view v-if="loading && products.length === 0" class="loading-wrap">
        <text>加载中...</text>
      </view>
      <view v-else-if="products.length === 0" class="empty-wrap">
        <text>暂无商品</text>
      </view>
      <view v-else class="product-grid">
        <view
          v-for="p in products"
          :key="p.id"
          class="product-item"
          @click="goToDetail(p.id)"
        >
          <image :src="p.coverImage || '/static/default-product.png'" class="product-img" mode="aspectFill" />
          <text class="product-name">{{ p.name }}</text>
          <view class="product-price">
            <text class="price-symbol">¥</text>
            <text class="price-value">{{ p.minPrice || p.price || '0' }}</text>
          </view>
        </view>
      </view>
    </scroll-view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getProductPage, getCategoryTree } from '@/api/product'

const query = ref({ pageNum: 1, pageSize: 12, categoryId: null, keyword: '' })
const products = ref([])
const categories = ref([])
const total = ref(0)
const loading = ref(false)
const listHeight = ref(500)

const categoryLabels = ref([{ id: null, name: '全部分类' }])
const currentCategoryLabel = ref('全部分类')

onMounted(() => {
  const sys = uni.getSystemInfoSync()
  listHeight.value = sys.windowHeight - 220
  loadCategories()
  loadProducts()
})

async function loadCategories() {
  try {
    const res = await getCategoryTree()
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

async function loadProducts(append = false) {
  if (loading.value) return
  loading.value = true
  try {
    const res = await getProductPage(query.value)
    const data = res.data || {}
    const list = data.records || []
    total.value = data.total || 0
    products.value = append ? [...products.value, ...list] : list
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
  loadProducts()
}

function handleSearch() {
  query.value.pageNum = 1
  loadProducts()
}

function loadMore() {
  if (products.value.length >= total.value) return
  query.value.pageNum++
  loadProducts(true)
}

function goTo(url) {
  uni.navigateTo({ url })
}

function goToDetail(id) {
  uni.navigateTo({ url: `/pages/shop/detail?id=${id}` })
}
</script>

<style lang="scss" scoped>
.page {
  min-height: 100vh;
  background: #f5f5f5;
}

.seckill-banner {
  margin: 12px;
  padding: 16px;
  background: linear-gradient(135deg, #ef4444, #f97316);
  border-radius: 12px;
}

.banner-content {
  display: flex;
  align-items: center;
  color: #fff;
}

.banner-icon {
  font-size: 28px;
  margin-right: 12px;
}

.banner-text {
  flex: 1;
}

.banner-title {
  font-size: 18px;
  font-weight: bold;
}

.banner-desc {
  font-size: 12px;
  opacity: 0.9;
}

.banner-btn {
  padding: 6px 16px;
  background: rgba(255,255,255,0.3);
  border-radius: 20px;
  font-size: 13px;
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

.search-btn {
  padding: 0 16px;
  height: 36px;
  line-height: 36px;
  background: #3b82f6;
  color: #fff;
  border-radius: 8px;
  font-size: 14px;
}

.product-list {
  padding: 12px;
}

.loading-wrap,
.empty-wrap {
  padding: 60px;
  text-align: center;
  color: #999;
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

.product-item {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
}

.product-img {
  width: 100%;
  height: 160px;
}

.product-name {
  display: block;
  padding: 8px;
  font-size: 14px;
  color: #333;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-price {
  padding: 0 8px 8px;
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
