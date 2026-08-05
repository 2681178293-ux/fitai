<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getExercisePage } from '@/api/training'
import { VideoPlay, ArrowLeft } from '@element-plus/icons-vue'

const router = useRouter()

const exercises = ref([])
const total = ref(0)
const loading = ref(false)

const query = ref({
  pageNum: 1,
  pageSize: 12,
  keyword: '',
  difficulty: null,
  muscleGroup: null
})

const difficultyOptions = [
  { value: 1, label: '入门' },
  { value: 2, label: '进阶' },
  { value: 3, label: '高级' }
]

async function loadExercises() {
  loading.value = true
  try {
    const res = await getExercisePage(query.value)
    exercises.value = res.records || []
    total.value = Number(res.total) || 0
  } catch (e) {
    exercises.value = []
  } finally {
    loading.value = false
  }
}

function handleFilter() {
  query.value.pageNum = 1
  loadExercises()
}

function handlePageChange(page) {
  query.value.pageNum = page
  loadExercises()
}

function goDetail(id) {
  router.push(`/training/exercise/${id}`)
}

function goBack() {
  router.push('/training')
}

onMounted(() => {
  loadExercises()
})
</script>

<template>
  <div class="max-w-6xl mx-auto px-4 py-6 pb-24 md:pb-6">
    <!-- 头部 -->
    <div class="flex items-center gap-4 mb-6">
      <el-button text @click="goBack" class="flex items-center gap-1">
        <el-icon><ArrowLeft /></el-icon>
        返回训练
      </el-button>
      <h1 class="text-2xl font-bold text-gray-800">动作库</h1>
    </div>

    <!-- 筛选 -->
    <div class="bg-white rounded-xl p-4 mb-6 flex flex-wrap items-center gap-3">
      <el-input
        v-model="query.keyword"
        placeholder="搜索动作"
        clearable
        style="width: 180px"
        @clear="handleFilter"
        @keyup.enter="handleFilter"
      />
      <el-select
        v-model="query.difficulty"
        placeholder="难度"
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
      <el-button type="primary" @click="handleFilter">搜索</el-button>
    </div>

    <!-- 列表 -->
    <div v-loading="loading" class="min-h-[200px]">
      <div v-if="!loading && exercises.length === 0" class="text-center py-16 text-gray-500">
        暂无动作
      </div>
      <div v-else class="exercise-grid">
        <div
          v-for="exercise in exercises"
          :key="exercise.id"
          class="exercise-card cursor-pointer"
          @click="goDetail(exercise.id)"
        >
          <div class="exercise-cover">
            <img
              :src="exercise.coverImage || 'https://images.unsplash.com/photo-1571019614242-c5c5dee9f50b?w=300'"
              :alt="exercise.name"
            />
            <div class="exercise-overlay">
              <el-button type="primary" size="small" circle>
                <el-icon><VideoPlay /></el-icon>
              </el-button>
            </div>
          </div>
          <div class="exercise-info">
            <h3>{{ exercise.name }}</h3>
            <div class="exercise-tags">
              <el-tag size="small" type="info">{{ exercise.muscleGroupName }}</el-tag>
              <el-tag size="small">{{ exercise.difficultyName }}</el-tag>
            </div>
          </div>
        </div>
      </div>

      <!-- 分页 -->
      <div v-if="total > query.pageSize" class="mt-6 flex justify-center">
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

<style scoped>
.exercise-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

@media (max-width: 768px) {
  .exercise-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

.exercise-card {
  background: white;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  transition: all 0.3s;
}

.exercise-card:hover {
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
  transform: translateY(-4px);
}

.exercise-cover {
  position: relative;
  aspect-ratio: 4/3;
  overflow: hidden;
}

.exercise-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s;
}

.exercise-card:hover .exercise-cover img {
  transform: scale(1.1);
}

.exercise-overlay {
  position: absolute;
  inset: 0;
  background: rgba(0, 0, 0, 0.3);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s;
}

.exercise-card:hover .exercise-overlay {
  opacity: 1;
}

.exercise-info {
  padding: 12px;
}

.exercise-info h3 {
  font-size: 14px;
  font-weight: 600;
  color: #1f2937;
  margin-bottom: 8px;
}

.exercise-tags {
  display: flex;
  gap: 4px;
  flex-wrap: wrap;
}
</style>
