<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { getBannerList } from '@/api/banner'
import { getCoursePage } from '@/api/course'
import { getProductPage } from '@/api/product'

const router = useRouter()
const userStore = useUserStore()

// 轮播图
const banners = ref([])
const displayBanners = computed(() => {
  if (banners.value?.length > 0) return banners.value
  return [{ id: 'default', imageUrl: '', isPlaceholder: true }]
})

// 推荐课程
const courses = ref([])

// 推荐商品
const products = ref([])

onMounted(async () => {
  // 加载轮播图
  try {
    const list = await getBannerList({ position: 'home' })
    banners.value = list || []
  } catch (e) {
    banners.value = []
  }

  // 加载推荐课程
  try {
    const courseRes = await getCoursePage({ pageNum: 1, pageSize: 4 })
    courses.value = courseRes.records || []
  } catch (e) {}
  
  // 加载推荐商品
  try {
    const productRes = await getProductPage({ pageNum: 1, pageSize: 4 })
    products.value = productRes.records || []
  } catch (e) {}
})

function handleBannerClick(item) {
  if (item?.isPlaceholder) {
    router.push('/training')
    return
  }
  if (!item?.linkType) return
  if (item.linkType === 'url' && item.linkValue) {
    window.open(item.linkValue)
  } else if (item.linkType === 'page' && item.linkValue) {
    router.push(item.linkValue)
  } else if (item.linkType === 'product' && item.linkValue) {
    router.push(`/mall/product/${item.linkValue}`)
  } else if (item.linkType === 'course' && item.linkValue) {
    router.push(`/course/${item.linkValue}`)
  }
}

function goToCourse(id) {
  router.push(`/course/${id}`)
}

function goToProduct(id) {
  router.push(`/mall/product/${id}`)
}

function goToTraining() {
  router.push('/training')
}
</script>

<template>
  <div class="pb-20 md:pb-0">
    <!-- 轮播图 -->
    <section class="w-full">
      <el-carousel :interval="4000" arrow="hover" height="420px" class="banner-carousel">
        <el-carousel-item v-for="item in displayBanners" :key="item.id">
          <div
            class="banner-item cursor-pointer"
            :class="{ 'banner-placeholder': item.isPlaceholder }"
            :style="item.imageUrl ? { backgroundImage: `url(${item.imageUrl})` } : {}"
            @click="handleBannerClick(item)"
          >
            <div v-if="item.isPlaceholder" class="banner-placeholder-content">
              <h1 class="text-4xl md:text-5xl font-bold mb-4">AI 私教，智能训练</h1>
              <p class="text-lg md:text-xl opacity-90 mb-8">根据你的目标和条件，生成专属训练计划</p>
              <el-button type="primary" size="large" class="!bg-white !text-emerald-600 !border-0">
                开始训练
              </el-button>
            </div>
          </div>
        </el-carousel-item>
      </el-carousel>
    </section>
    
    <!-- 功能入口 -->
    <section class="max-w-7xl mx-auto px-4 py-8">
      <div class="grid grid-cols-4 gap-4">
        <router-link to="/training" class="flex flex-col items-center p-4 bg-white rounded-xl shadow-sm hover:shadow-md transition-shadow">
          <div class="w-12 h-12 bg-emerald-100 rounded-full flex items-center justify-center mb-2">
            <span class="text-2xl">💪</span>
          </div>
          <span class="text-sm text-gray-700">AI训练</span>
        </router-link>
        <router-link to="/course" class="flex flex-col items-center p-4 bg-white rounded-xl shadow-sm hover:shadow-md transition-shadow">
          <div class="w-12 h-12 bg-blue-100 rounded-full flex items-center justify-center mb-2">
            <span class="text-2xl">🎬</span>
          </div>
          <span class="text-sm text-gray-700">视频课程</span>
        </router-link>
        <router-link to="/mall" class="flex flex-col items-center p-4 bg-white rounded-xl shadow-sm hover:shadow-md transition-shadow">
          <div class="w-12 h-12 bg-orange-100 rounded-full flex items-center justify-center mb-2">
            <span class="text-2xl">🛒</span>
          </div>
          <span class="text-sm text-gray-700">装备商城</span>
        </router-link>
        <router-link to="/profile" class="flex flex-col items-center p-4 bg-white rounded-xl shadow-sm hover:shadow-md transition-shadow">
          <div class="w-12 h-12 bg-purple-100 rounded-full flex items-center justify-center mb-2">
            <span class="text-2xl">📊</span>
          </div>
          <span class="text-sm text-gray-700">我的数据</span>
        </router-link>
      </div>
    </section>
    
    <!-- 推荐课程 -->
    <section class="max-w-7xl mx-auto px-4 py-8">
      <div class="flex items-center justify-between mb-4">
        <h2 class="text-xl font-bold">推荐课程</h2>
        <router-link to="/course" class="text-sm text-emerald-500">查看更多 &gt;</router-link>
      </div>
      <div class="grid grid-cols-2 md:grid-cols-4 gap-4">
        <div
          v-for="course in courses"
          :key="course.id"
          class="bg-white rounded-xl overflow-hidden shadow-sm hover:shadow-md transition-shadow cursor-pointer"
          @click="goToCourse(course.id)"
        >
          <div class="aspect-video bg-gray-200">
            <img
              :src="course.coverImage || '/default-course.jpg'"
              :alt="course.title"
              class="w-full h-full object-cover"
            />
          </div>
          <div class="p-3">
            <h3 class="font-medium text-sm line-clamp-1">{{ course.title }}</h3>
            <div class="flex items-center gap-2 mt-2 text-xs text-gray-500">
              <span>{{ course.duration }}分钟</span>
              <span>·</span>
              <span>{{ course.difficultyName }}</span>
            </div>
          </div>
        </div>
      </div>
    </section>
    
    <!-- 推荐商品 -->
    <section class="max-w-7xl mx-auto px-4 py-8">
      <div class="flex items-center justify-between mb-4">
        <h2 class="text-xl font-bold">热门装备</h2>
        <router-link to="/mall" class="text-sm text-emerald-500">查看更多 &gt;</router-link>
      </div>
      <div class="grid grid-cols-2 md:grid-cols-4 gap-4">
        <div
          v-for="product in products"
          :key="product.id"
          class="bg-white rounded-xl overflow-hidden shadow-sm hover:shadow-md transition-shadow cursor-pointer"
          @click="goToProduct(product.id)"
        >
          <div class="aspect-square bg-gray-200">
            <img
              :src="product.coverImage || '/default-product.jpg'"
              :alt="product.name"
              class="w-full h-full object-cover"
            />
          </div>
          <div class="p-3">
            <h3 class="font-medium text-sm line-clamp-1">{{ product.name }}</h3>
            <div class="flex items-center gap-2 mt-2">
              <span class="text-red-500 font-bold">¥{{ product.minPrice }}</span>
              <span class="text-xs text-gray-400">已售{{ product.salesCount }}</span>
            </div>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<style scoped>
.banner-carousel {
  border-radius: 0;
}
.banner-carousel :deep(.el-carousel__container) {
  height: 420px;
}
.banner-item {
  width: 100%;
  height: 100%;
  background-size: 100% 100%;
  background-repeat: no-repeat;
  background-position: center;
  background-color: #10b981;
}
.banner-placeholder {
  background: linear-gradient(135deg, #10b981 0%, #06b6d4 100%);
}
.banner-placeholder-content {
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: white;
  text-align: center;
  padding: 0 1rem;
}
</style>
