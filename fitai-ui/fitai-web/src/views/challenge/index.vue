<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getActiveChallenges } from '@/api/challenge'
import { Trophy, Timer, User } from '@element-plus/icons-vue'

const router = useRouter()
const loading = ref(false)
const challenges = ref([])

// 加载挑战列表
const loadChallenges = async () => {
  loading.value = true
  try {
    const { data } = await getActiveChallenges()
    challenges.value = data || []
  } catch (error) {
    console.error('加载挑战赛失败:', error)
  } finally {
    loading.value = false
  }
}

// 跳转详情
const goToDetail = (id) => {
  router.push(`/challenge/${id}`)
}

// 获取挑战类型图标
const getTypeIcon = (type) => {
  const icons = {
    plank: '🏋️',
    pushup: '💪',
    running: '🏃',
    checkin: '✅'
  }
  return icons[type] || '🎯'
}

// 格式化时间
const formatDate = (date) => {
  if (!date) return ''
  return new Date(date).toLocaleDateString()
}

onMounted(() => {
  loadChallenges()
})
</script>

<template>
  <div class="challenge-page p-4">
    <div class="page-header mb-6">
      <h1 class="text-2xl font-bold flex items-center gap-2">
        <el-icon class="text-yellow-500"><Trophy /></el-icon>
        挑战赛
      </h1>
      <p class="text-gray-500 mt-1">参与挑战，突破自我</p>
    </div>

    <div v-loading="loading" class="challenges-list">
      <el-empty v-if="!loading && challenges.length === 0" description="暂无进行中的挑战赛" />
      
      <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
        <div 
          v-for="challenge in challenges" 
          :key="challenge.id"
          class="challenge-card bg-white rounded-xl overflow-hidden shadow-sm cursor-pointer hover:shadow-lg transition-shadow"
          @click="goToDetail(challenge.id)"
        >
          <div class="relative">
            <img 
              :src="challenge.coverImage || '/placeholder.png'" 
              :alt="challenge.name"
              class="w-full h-40 object-cover"
            />
            <span class="absolute top-3 left-3 text-3xl">
              {{ getTypeIcon(challenge.type) }}
            </span>
          </div>
          
          <div class="p-4">
            <h3 class="font-bold text-lg">{{ challenge.name }}</h3>
            <p class="text-gray-500 text-sm mt-1 line-clamp-2">{{ challenge.description }}</p>
            
            <div class="flex items-center justify-between mt-4">
              <div class="flex items-center gap-4 text-sm text-gray-500">
                <span class="flex items-center gap-1">
                  <el-icon><Timer /></el-icon>
                  {{ challenge.targetDays || '∞' }}天
                </span>
                <span class="flex items-center gap-1">
                  <el-icon><User /></el-icon>
                  {{ challenge.participantCount || 0 }}人参与
                </span>
              </div>
              <el-button type="primary" size="small">参与</el-button>
            </div>
          </div>
        </div>
      </div>
    </div>
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
