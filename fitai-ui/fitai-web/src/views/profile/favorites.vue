<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getFavoriteCourses, toggleFavoriteCourse } from '@/api/course'
import { getFavoriteProducts, toggleFavoriteProduct } from '@/api/product'
import { VideoPlay, Star, StarFilled, ShoppingCart } from '@element-plus/icons-vue'

const router = useRouter()
const activeTab = ref('course')
const loading = ref(false)
const courseList = ref([])
const productList = ref([])
const courseTotal = ref(0)
const productTotal = ref(0)
const query = ref({ pageNum: 1, pageSize: 12 })

// 加载课程收藏
const loadCourses = async (append = false) => {
  if (loading.value && !append) return
  loading.value = true
  try {
    const res = await getFavoriteCourses(query.value)
    const records = res.records || []
    courseTotal.value = Number(res.total) || 0
    courseList.value = append ? [...courseList.value, ...records] : records
  } catch (e) {
    ElMessage.error('加载失败')
  } finally {
    loading.value = false
  }
}

// 加载商品收藏
const loadProducts = async (append = false) => {
  if (loading.value && !append) return
  loading.value = true
  try {
    const res = await getFavoriteProducts(query.value)
    const records = res.records || []
    productTotal.value = Number(res.total) || 0
    productList.value = append ? [...productList.value, ...records] : records
  } catch (e) {
    ElMessage.error('加载失败')
  } finally {
    loading.value = false
  }
}

// 取消课程收藏
const handleUnfavoriteCourse = async (item) => {
  try {
    await toggleFavoriteCourse(item.id)
    courseList.value = courseList.value.filter(i => i.id !== item.id)
    courseTotal.value = Math.max(0, courseTotal.value - 1)
    ElMessage.success('已取消收藏')
  } catch (e) {
    ElMessage.error('操作失败')
  }
}

// 取消商品收藏
const handleUnfavoriteProduct = async (item) => {
  try {
    await toggleFavoriteProduct(item.id)
    productList.value = productList.value.filter(i => i.id !== item.id)
    productTotal.value = Math.max(0, productTotal.value - 1)
    ElMessage.success('已取消收藏')
  } catch (e) {
    ElMessage.error('操作失败')
  }
}

// Tab 切换时加载
const loadCurrent = () => {
  query.value.pageNum = 1
  if (activeTab.value === 'course') loadCourses()
  else loadProducts()
}

watch(activeTab, loadCurrent)

const goToCourse = (id) => router.push(`/course/${id}`)
const goToProduct = (id) => router.push(`/mall/product/${id}`)
const goToCourseList = () => router.push('/course')
const goToMall = () => router.push('/mall')

onMounted(() => {
  loadCurrent()
})
</script>

<template>
  <div class="max-w-2xl mx-auto px-4 py-6 pb-24 md:pb-6">
    <h1 class="text-xl font-bold mb-6 flex items-center gap-2">
      <el-icon class="text-amber-500"><StarFilled /></el-icon>
      我的收藏
    </h1>

    <el-tabs v-model="activeTab" class="mb-6">
      <el-tab-pane label="课程" name="course">
        <div v-loading="loading">
          <el-empty v-if="!loading && courseList.length === 0" description="暂无收藏的课程">
            <template #extra>
              <el-button type="primary" @click="goToCourseList">去逛逛</el-button>
            </template>
          </el-empty>

          <div v-else class="space-y-4">
            <div
              v-for="item in courseList"
              :key="item.id"
              class="flex gap-4 p-4 bg-white rounded-xl shadow-sm hover:shadow-md transition-shadow cursor-pointer"
              @click="goToCourse(item.id)"
            >
              <img
                :src="item.coverImage || '/placeholder.png'"
                :alt="item.title"
                class="w-32 h-24 object-cover rounded-lg flex-shrink-0"
              />
              <div class="flex-1 min-w-0">
                <h3 class="font-medium text-gray-900 line-clamp-2">{{ item.title }}</h3>
                <div class="flex items-center gap-3 mt-2 text-sm text-gray-500">
                  <span>{{ item.duration != null ? item.duration + '分钟' : '-' }}</span>
                  <span>{{ item.calories != null ? item.calories + '卡' : '-' }}</span>
                  <span>{{ item.difficultyName || '-' }}</span>
                </div>
                <div class="mt-2 flex items-center gap-2">
                  <el-button type="primary" size="small" link>
                    <el-icon><VideoPlay /></el-icon> 观看
                  </el-button>
                  <el-button
                    type="danger"
                    size="small"
                    link
                    @click.stop="handleUnfavoriteCourse(item)"
                  >
                    <el-icon><Star /></el-icon> 取消收藏
                  </el-button>
                </div>
              </div>
            </div>
          </div>

          <div v-if="courseList.length < courseTotal && courseList.length > 0" class="mt-4 text-center">
            <el-button :loading="loading" @click="query.pageNum++; loadCourses(true)">
              加载更多
            </el-button>
          </div>
        </div>
      </el-tab-pane>

      <el-tab-pane label="商品" name="product">
        <div v-loading="loading">
          <el-empty v-if="!loading && productList.length === 0" description="暂无收藏的商品">
            <template #extra>
              <el-button type="primary" @click="goToMall">去逛逛</el-button>
            </template>
          </el-empty>

          <div v-else class="space-y-4">
            <div
              v-for="item in productList"
              :key="item.id"
              class="flex gap-4 p-4 bg-white rounded-xl shadow-sm hover:shadow-md transition-shadow cursor-pointer"
              @click="goToProduct(item.id)"
            >
              <img
                :src="item.coverImage || '/placeholder.png'"
                :alt="item.name"
                class="w-32 h-24 object-cover rounded-lg flex-shrink-0"
              />
              <div class="flex-1 min-w-0">
                <h3 class="font-medium text-gray-900 line-clamp-2">{{ item.name }}</h3>
                <div class="mt-2 text-red-500 font-bold">¥{{ item.minPrice }}</div>
                <div class="mt-2 flex items-center gap-2">
                  <el-button type="primary" size="small" link>
                    <el-icon><ShoppingCart /></el-icon> 查看
                  </el-button>
                  <el-button
                    type="danger"
                    size="small"
                    link
                    @click.stop="handleUnfavoriteProduct(item)"
                  >
                    <el-icon><Star /></el-icon> 取消收藏
                  </el-button>
                </div>
              </div>
            </div>
          </div>

          <div v-if="productList.length < productTotal && productList.length > 0" class="mt-4 text-center">
            <el-button :loading="loading" @click="query.pageNum++; loadProducts(true)">
              加载更多
            </el-button>
          </div>
        </div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<style scoped>
.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>
