<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getProductPage } from '@/api/product'

const router = useRouter()

const products = ref([])
const loading = ref(false)
const total = ref(0)

const categoryAgg = ref([])
const brandAgg = ref([])
const priceRangeAgg = ref([])

const query = ref({
  pageNum: 1,
  pageSize: 12,
  categoryId: null,
  keyword: '',
  brand: null,
  minPrice: null,
  maxPrice: null,
  sortField: null,
  sortOrder: null
})

const activeSort = ref('')

onMounted(() => {
  loadProducts()
})

async function loadProducts() {
  loading.value = true
  try {
    const res = await getProductPage(query.value)
    products.value = res.records || []
    total.value = Number(res.total) || 0
    categoryAgg.value = res.categoryAgg || []
    brandAgg.value = res.brandAgg || []
    priceRangeAgg.value = res.priceRangeAgg || []
  } catch (e) {}
  loading.value = false
}

function handleFilter() {
  query.value.pageNum = 1
  loadProducts()
}

function selectCategory(catKey) {
  const id = catKey ? Number(catKey) : null
  query.value.categoryId = query.value.categoryId === id ? null : id
  handleFilter()
}

function selectBrand(brandKey) {
  query.value.brand = query.value.brand === brandKey ? null : brandKey
  handleFilter()
}

function selectPriceRange(rangeKey) {
  if (query.value._priceRangeKey === rangeKey) {
    query.value.minPrice = null
    query.value.maxPrice = null
    query.value._priceRangeKey = null
  } else {
    const parts = rangeKey.split('-')
    if (rangeKey.endsWith('+')) {
      query.value.minPrice = Number(parts[0])
      query.value.maxPrice = null
    } else {
      query.value.minPrice = Number(parts[0]) || null
      query.value.maxPrice = Number(parts[1]) || null
    }
    query.value._priceRangeKey = rangeKey
  }
  handleFilter()
}

function handleSort(field) {
  if (activeSort.value === field + '_desc') {
    activeSort.value = field + '_asc'
    query.value.sortField = field
    query.value.sortOrder = 'asc'
  } else if (activeSort.value === field + '_asc') {
    activeSort.value = ''
    query.value.sortField = null
    query.value.sortOrder = null
  } else {
    activeSort.value = field + '_desc'
    query.value.sortField = field
    query.value.sortOrder = 'desc'
  }
  handleFilter()
}

function clearAllFilters() {
  query.value.categoryId = null
  query.value.brand = null
  query.value.minPrice = null
  query.value.maxPrice = null
  query.value._priceRangeKey = null
  query.value.keyword = ''
  activeSort.value = ''
  query.value.sortField = null
  query.value.sortOrder = null
  handleFilter()
}

function handlePageChange(page) {
  query.value.pageNum = page
  loadProducts()
}

function goToDetail(id) {
  router.push(`/mall/product/${id}`)
}

const hasActiveFilter = () => {
  return query.value.categoryId || query.value.brand || query.value._priceRangeKey || query.value.keyword
}

function sortIcon(field) {
  if (activeSort.value === field + '_asc') return '↑'
  if (activeSort.value === field + '_desc') return '↓'
  return '↕'
}
</script>

<template>
  <div class="max-w-7xl mx-auto px-4 py-6 pb-24 md:pb-6">
    <h1 class="text-2xl font-bold mb-6">健身装备商城</h1>
    
    <!-- 秒杀活动入口 -->
    <div 
      class="bg-gradient-to-r from-red-500 to-orange-500 rounded-xl p-4 mb-6 cursor-pointer hover:shadow-lg transition-all"
      @click="router.push('/seckill')"
    >
      <div class="flex items-center justify-between text-white">
        <div class="flex items-center gap-3">
          <span class="text-2xl">🔥</span>
          <div>
            <h2 class="text-lg font-bold">限时秒杀</h2>
            <p class="text-sm opacity-90">超低价抢购，手慢无！</p>
          </div>
        </div>
        <el-button type="warning" round>立即查看</el-button>
      </div>
    </div>
    
    <!-- 搜索栏 -->
    <div class="bg-white rounded-xl p-4 mb-4 flex flex-wrap items-center gap-3">
      <el-input
        v-model="query.keyword"
        placeholder="搜索商品"
        clearable
        style="width: 240px"
        @clear="handleFilter"
        @keyup.enter="handleFilter"
      />
      <el-button type="primary" @click="handleFilter">搜索</el-button>
      <el-button v-if="hasActiveFilter()" text type="info" @click="clearAllFilters">清除筛选</el-button>
    </div>

    <!-- 聚合筛选面板 -->
    <div class="bg-white rounded-xl p-4 mb-6 space-y-3 text-sm">
      <!-- 分类 -->
      <div class="flex items-start gap-2" v-if="categoryAgg.length">
        <span class="shrink-0 text-gray-500 leading-7 w-14">分类</span>
        <div class="flex flex-wrap gap-2">
          <span
            v-for="cat in categoryAgg"
            :key="cat.key"
            class="px-3 py-1 rounded-full cursor-pointer transition-colors"
            :class="query.categoryId === Number(cat.key)
              ? 'bg-blue-500 text-white'
              : 'bg-gray-100 text-gray-700 hover:bg-gray-200'"
            @click="selectCategory(cat.key)"
          >{{ cat.label }} ({{ cat.docCount }})</span>
        </div>
      </div>

      <!-- 品牌 -->
      <div class="flex items-start gap-2" v-if="brandAgg.length">
        <span class="shrink-0 text-gray-500 leading-7 w-14">品牌</span>
        <div class="flex flex-wrap gap-2">
          <span
            v-for="b in brandAgg"
            :key="b.key"
            class="px-3 py-1 rounded-full cursor-pointer transition-colors"
            :class="query.brand === b.key
              ? 'bg-blue-500 text-white'
              : 'bg-gray-100 text-gray-700 hover:bg-gray-200'"
            @click="selectBrand(b.key)"
          >{{ b.label }} ({{ b.docCount }})</span>
        </div>
      </div>

      <!-- 价格区间 -->
      <div class="flex items-start gap-2" v-if="priceRangeAgg.length">
        <span class="shrink-0 text-gray-500 leading-7 w-14">价格</span>
        <div class="flex flex-wrap gap-2">
          <span
            v-for="pr in priceRangeAgg"
            :key="pr.key"
            class="px-3 py-1 rounded-full cursor-pointer transition-colors"
            :class="query._priceRangeKey === pr.key
              ? 'bg-blue-500 text-white'
              : 'bg-gray-100 text-gray-700 hover:bg-gray-200'"
            @click="selectPriceRange(pr.key)"
          >{{ pr.label }} ({{ pr.docCount }})</span>
        </div>
      </div>

      <!-- 排序 -->
      <div class="flex items-center gap-2 pt-1 border-t border-gray-100">
        <span class="shrink-0 text-gray-500 w-14">排序</span>
        <div class="flex gap-2">
          <span
            class="px-3 py-1 rounded-full cursor-pointer transition-colors"
            :class="activeSort.startsWith('salesCount') ? 'bg-blue-500 text-white' : 'bg-gray-100 text-gray-700 hover:bg-gray-200'"
            @click="handleSort('salesCount')"
          >销量 {{ sortIcon('salesCount') }}</span>
          <span
            class="px-3 py-1 rounded-full cursor-pointer transition-colors"
            :class="activeSort.startsWith('minPrice') ? 'bg-blue-500 text-white' : 'bg-gray-100 text-gray-700 hover:bg-gray-200'"
            @click="handleSort('minPrice')"
          >价格 {{ sortIcon('minPrice') }}</span>
        </div>
      </div>
    </div>
    
    <!-- 商品列表 -->
    <div v-loading="loading">
      <div class="grid grid-cols-2 md:grid-cols-4 gap-4">
        <div
          v-for="product in products"
          :key="product.id"
          class="bg-white rounded-xl overflow-hidden shadow-sm hover:shadow-md transition-shadow cursor-pointer"
          @click="goToDetail(product.id)"
        >
          <div class="aspect-square bg-gray-200">
            <img
              :src="product.coverImage || '/default-product.jpg'"
              :alt="product.name"
              class="w-full h-full object-cover"
            />
          </div>
          <div class="p-3">
            <h3 class="font-medium text-sm line-clamp-2">{{ product.name }}</h3>
            <div class="flex items-center justify-between mt-2">
              <span class="text-red-500 font-bold">¥{{ product.minPrice }}</span>
              <span class="text-xs text-gray-400">已售{{ product.salesCount }}</span>
            </div>
          </div>
        </div>
      </div>
      
      <el-empty v-if="!loading && products.length === 0" description="暂无商品" />

      <div class="flex justify-center mt-8">
        <el-pagination
          v-model:current-page="query.pageNum"
          :page-size="query.pageSize"
          :total="total"
          layout="prev, pager, next"
          @current-change="handlePageChange"
        />
      </div>
    </div>
  </div>
</template>
