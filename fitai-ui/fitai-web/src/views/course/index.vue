<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getCoursePage, getCategoryTree } from '@/api/course'

const router = useRouter()

const courses = ref([])
const categories = ref([])
const loading = ref(false)
const total = ref(0)

const query = ref({
  pageNum: 1,
  pageSize: 12,
  categoryId: null,
  difficulty: null,
  isFree: null,
  keyword: ''
})

const difficultyOptions = [
  { value: 1, label: '零基础' },
  { value: 2, label: '初级' },
  { value: 3, label: '中级' },
  { value: 4, label: '高级' }
]

onMounted(async () => {
  // 加载分类
  try {
    const data = await getCategoryTree()
    // 过滤掉 id 为 null 的项
    categories.value = (data || []).filter(cat => cat.id != null)
  } catch (e) {}
  
  // 加载课程
  loadCourses()
})

async function loadCourses() {
  loading.value = true
  try {
    const res = await getCoursePage(query.value)
    courses.value = res.records || []
    total.value = Number(res.total) || 0
  } catch (e) {}
  loading.value = false
}

function handleFilter() {
  query.value.pageNum = 1
  loadCourses()
}

function handlePageChange(page) {
  query.value.pageNum = page
  loadCourses()
}

function goToDetail(id) {
  router.push(`/course/${id}`)
}
</script>

<template>
  <div class="max-w-7xl mx-auto px-4 py-6 pb-24 md:pb-6">
    <!-- 页面标题 -->
    <h1 class="text-2xl font-bold mb-6">视频课程</h1>
    
    <!-- 筛选 -->
    <div class="bg-white rounded-xl p-4 mb-6 flex flex-wrap items-center gap-3">
      <el-input
        v-model="query.keyword"
        placeholder="搜索课程"
        clearable
        style="width: 200px"
        @clear="handleFilter"
        @keyup.enter="handleFilter"
      />
      <el-select 
        v-model="query.categoryId" 
        placeholder="全部分类" 
        clearable 
        style="width: 140px"
        @change="handleFilter"
      >
        <el-option
          v-for="cat in categories"
          :key="cat.id"
          :value="cat.id"
          :label="cat.name"
        />
      </el-select>
      <el-select 
        v-model="query.difficulty" 
        placeholder="全部难度" 
        clearable 
        style="width: 120px"
        @change="handleFilter"
      >
        <el-option
          v-for="opt in difficultyOptions"
          :key="opt.value"
          :value="opt.value"
          :label="opt.label"
        />
      </el-select>
      <el-select 
        v-model="query.isFree" 
        placeholder="全部类型" 
        clearable 
        style="width: 120px"
        @change="handleFilter"
      >
        <el-option :value="true" label="免费课程" />
        <el-option :value="false" label="VIP课程" />
      </el-select>
      <el-button type="primary" @click="handleFilter">搜索</el-button>
    </div>
    
    <!-- 课程列表 -->
    <div v-loading="loading">
      <div class="grid grid-cols-2 md:grid-cols-4 gap-4">
        <div
          v-for="course in courses"
          :key="course.id"
          class="bg-white rounded-xl overflow-hidden shadow-sm hover:shadow-md transition-shadow cursor-pointer"
          @click="goToDetail(course.id)"
        >
          <div class="aspect-video bg-gray-200 relative">
            <img
              :src="course.coverImage || '/default-course.jpg'"
              :alt="course.title"
              class="w-full h-full object-cover"
            />
            <div class="absolute bottom-2 right-2 bg-black/50 text-white text-xs px-2 py-0.5 rounded">
              {{ course.duration }}分钟
            </div>
          </div>
          <div class="p-3">
            <h3 class="font-medium text-sm line-clamp-2">{{ course.title }}</h3>
            <div class="flex items-center justify-between mt-2">
              <el-tag size="small" :type="course.isFree ? 'success' : 'warning'">
                {{ course.isFree ? '免费' : 'VIP' }}
              </el-tag>
              <span class="text-xs text-gray-400">{{ course.viewCount }}次播放</span>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 分页 -->
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
