<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getExerciseById } from '@/api/training'
import { ArrowLeft } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()

const exercise = ref(null)
const loading = ref(true)

onMounted(async () => {
  const id = route.params.id
  try {
    exercise.value = await getExerciseById(id)
  } catch (e) {
    router.push('/training/exercise')
  } finally {
    loading.value = false
  }
})

function goBack() {
  router.push('/training/exercise')
}
</script>

<template>
  <div class="max-w-4xl mx-auto px-4 py-6 pb-24 md:pb-6">
    <div v-if="loading" class="text-center py-16 text-gray-500">
      加载中...
    </div>

    <template v-else-if="exercise">
      <!-- 返回 -->
      <div class="mb-4">
        <el-button text @click="goBack" class="flex items-center gap-1">
          <el-icon><ArrowLeft /></el-icon>
          返回动作库
        </el-button>
      </div>

      <!-- 封面 / 视频 -->
      <div class="rounded-xl overflow-hidden bg-black mb-6">
        <div v-if="exercise.videoUrl" class="aspect-video">
          <video
            :src="exercise.videoUrl"
            controls
            class="w-full h-full object-contain"
          />
        </div>
        <div v-else class="aspect-video relative">
          <img
            :src="exercise.coverImage || 'https://images.unsplash.com/photo-1571019614242-c5c5dee9f50b?w=800'"
            :alt="exercise.name"
            class="w-full h-full object-cover"
          />
          <div class="absolute inset-0 flex items-center justify-center bg-black/30">
            <span class="text-white/80 text-sm">暂无视频</span>
          </div>
        </div>
      </div>

      <!-- 信息卡片 -->
      <div class="bg-white rounded-xl p-6 shadow-sm">
        <h1 class="text-2xl font-bold text-gray-800 mb-4">{{ exercise.name }}</h1>

        <!-- 标签 -->
        <div class="flex flex-wrap gap-2 mb-4">
          <el-tag type="info">{{ exercise.muscleGroupName }}</el-tag>
          <el-tag>{{ exercise.difficultyName }}</el-tag>
          <el-tag v-if="exercise.equipmentName" size="small">{{ exercise.equipmentName }}</el-tag>
          <el-tag v-if="exercise.caloriesPerMin != null" type="success" size="small">
            约 {{ exercise.caloriesPerMin }} 千卡/分钟
          </el-tag>
        </div>

        <!-- 描述 -->
        <div v-if="exercise.description" class="mb-6">
          <h2 class="text-lg font-semibold text-gray-800 mb-2">动作介绍</h2>
          <p class="text-gray-600 whitespace-pre-wrap">{{ exercise.description }}</p>
        </div>

        <!-- 动作要点 -->
        <div v-if="exercise.tips" class="mb-6">
          <h2 class="text-lg font-semibold text-gray-800 mb-2">动作要点</h2>
          <p class="text-gray-600 whitespace-pre-wrap">{{ exercise.tips }}</p>
        </div>

        <!-- 常见错误 -->
        <div v-if="exercise.commonMistakes" class="mb-6">
          <h2 class="text-lg font-semibold text-gray-800 mb-2">常见错误</h2>
          <p class="text-gray-600 whitespace-pre-wrap">{{ exercise.commonMistakes }}</p>
        </div>

        <!-- 无详情时的提示 -->
        <div
          v-if="!exercise.description && !exercise.tips && !exercise.commonMistakes"
          class="text-gray-400 text-sm"
        >
          暂无更多说明，敬请期待。
        </div>
      </div>
    </template>
  </div>
</template>
