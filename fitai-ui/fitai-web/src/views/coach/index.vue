<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getCoachList } from '@/api/coach'

const router = useRouter()

const coaches = ref([])
const loading = ref(false)
const total = ref(0)

const query = ref({
  pageNum: 1,
  pageSize: 12,
  specialty: null,
  keyword: ''
})

const specialtyOptions = [
  '增肌塑形',
  '减脂瘦身',
  '体态矫正',
  '康复训练',
  '力量训练',
  '有氧训练',
  '瑜伽',
  '普拉提',
  '搏击'
]

onMounted(() => {
  loadCoaches()
})

async function loadCoaches() {
  loading.value = true
  try {
    const res = await getCoachList(query.value)
    coaches.value = res.records || []
    total.value = Number(res.total) || 0
  } catch (e) {}
  loading.value = false
}

function handleFilter() {
  query.value.pageNum = 1
  loadCoaches()
}

function handlePageChange(page) {
  query.value.pageNum = page
  loadCoaches()
}

function splitTags(specialty) {
  if (!specialty) return []
  return String(specialty)
    .split(/[,，]/)
    .map(t => t.trim())
    .filter(t => t)
}

function formatRating(rating) {
  const n = Number(rating)
  return Number.isFinite(n) && n > 0 ? n.toFixed(1) : '暂无'
}

function formatPrice(price) {
  const n = Number(price)
  return Number.isFinite(n) ? n.toFixed(2) : '0.00'
}

function goToDetail(id) {
  router.push(`/coach/${id}`)
}

function goToMyBooking() {
  router.push('/coach/my-booking')
}
</script>

<template>
  <div class="max-w-7xl mx-auto px-4 py-6 pb-24 md:pb-6">
    <!-- 页面标题 -->
    <div class="flex items-center justify-between mb-6">
      <h1 class="text-2xl font-bold">私教教练</h1>
      <el-button text type="primary" @click="goToMyBooking">我的预约 &gt;</el-button>
    </div>

    <!-- 筛选 -->
    <div class="bg-white rounded-xl p-4 mb-6 flex flex-wrap items-center gap-3">
      <el-input
        v-model="query.keyword"
        placeholder="搜索教练姓名"
        clearable
        style="width: 200px"
        @clear="handleFilter"
        @keyup.enter="handleFilter"
      />
      <el-select
        v-model="query.specialty"
        placeholder="全部专长"
        clearable
        style="width: 140px"
        @change="handleFilter"
      >
        <el-option
          v-for="opt in specialtyOptions"
          :key="opt"
          :value="opt"
          :label="opt"
        />
      </el-select>
      <el-button type="primary" @click="handleFilter">搜索</el-button>
    </div>

    <!-- 教练列表 -->
    <div v-loading="loading">
      <el-empty v-if="!loading && coaches.length === 0" description="暂无教练" />

      <div v-else class="grid grid-cols-1 md:grid-cols-3 gap-4">
        <div
          v-for="coach in coaches"
          :key="coach.id"
          class="bg-white rounded-xl p-4 shadow-sm hover:shadow-md transition-shadow cursor-pointer flex gap-4"
          @click="goToDetail(coach.id)"
        >
          <el-avatar :size="64" :src="coach.avatar" />
          <div class="flex-1 min-w-0">
            <div class="flex items-center gap-2">
              <span class="font-medium">{{ coach.name }}</span>
              <span class="text-xs text-gray-400">{{ coach.title || '健身教练' }}</span>
            </div>
            <div class="flex flex-wrap gap-1 mt-2">
              <el-tag
                v-for="tag in splitTags(coach.specialty)"
                :key="tag"
                size="small"
                type="success"
              >
                {{ tag }}
              </el-tag>
            </div>
            <div class="flex items-center justify-between mt-3 text-xs text-gray-500">
              <span>
                <span class="text-amber-500">评分 {{ formatRating(coach.rating) }}</span>
                <span class="ml-2">学员 {{ coach.studentCount || 0 }}</span>
              </span>
              <span class="text-red-500 font-medium">¥{{ formatPrice(coach.price) }}/课时</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 分页 -->
      <div v-if="total > 0" class="flex justify-center mt-8">
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
