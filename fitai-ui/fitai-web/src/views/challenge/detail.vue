<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getChallengeDetail, joinChallenge, checkinChallenge, getChallengeRanking } from '@/api/challenge'
import { useUserStore } from '@/stores/user'
import { Trophy, Timer, User, Check } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)
const challenge = ref(null)
const ranking = ref([])
const joined = ref(false)
const actionLoading = ref(false)

// 加载数据
const loadData = async () => {
  const id = route.params.id
  loading.value = true
  try {
    const [challengeRes, rankingRes] = await Promise.all([
      getChallengeDetail(id),
      getChallengeRanking(id, 20)
    ])
    challenge.value = challengeRes.data
    ranking.value = rankingRes.data || []
    
    // 检查是否已参与
    joined.value = ranking.value.some(r => r.userId === userStore.userInfo?.id)
  } catch (error) {
    console.error('加载失败:', error)
    ElMessage.error('加载失败')
  } finally {
    loading.value = false
  }
}

// 参与挑战
const handleJoin = async () => {
  if (!userStore.isLoggedIn) {
    router.push('/login')
    return
  }
  
  actionLoading.value = true
  try {
    await joinChallenge(challenge.value.id)
    ElMessage.success('参与成功！')
    joined.value = true
    loadData()
  } catch (error) {
    ElMessage.error(error.message || '参与失败')
  } finally {
    actionLoading.value = false
  }
}

// 打卡
const handleCheckin = async () => {
  actionLoading.value = true
  try {
    await checkinChallenge(challenge.value.id)
    ElMessage.success('打卡成功！')
    loadData()
  } catch (error) {
    ElMessage.error(error.message || '打卡失败')
  } finally {
    actionLoading.value = false
  }
}

// 格式化日期
const formatDate = (date) => {
  if (!date) return ''
  return new Date(date).toLocaleDateString()
}

onMounted(() => {
  loadData()
})
</script>

<template>
  <div class="challenge-detail-page" v-loading="loading">
    <template v-if="challenge">
      <!-- 头部 -->
      <div class="challenge-header relative">
        <img 
          :src="challenge.coverImage || '/placeholder.png'" 
          :alt="challenge.name"
          class="w-full h-48 object-cover"
        />
        <div class="absolute inset-0 bg-gradient-to-t from-black/60 to-transparent" />
        <div class="absolute bottom-4 left-4 text-white">
          <h1 class="text-2xl font-bold">{{ challenge.name }}</h1>
          <p class="mt-1 opacity-90">{{ challenge.description }}</p>
        </div>
      </div>

      <!-- 挑战信息 -->
      <div class="p-4">
        <div class="info-cards grid grid-cols-3 gap-4 mb-6">
          <div class="info-card bg-white rounded-lg p-3 text-center shadow-sm">
            <div class="text-2xl font-bold text-blue-500">{{ challenge.targetDays || '∞' }}</div>
            <div class="text-sm text-gray-500">目标天数</div>
          </div>
          <div class="info-card bg-white rounded-lg p-3 text-center shadow-sm">
            <div class="text-2xl font-bold text-green-500">{{ challenge.participantCount || 0 }}</div>
            <div class="text-sm text-gray-500">参与人数</div>
          </div>
          <div class="info-card bg-white rounded-lg p-3 text-center shadow-sm">
            <div class="text-2xl font-bold text-orange-500">{{ ranking.filter(r => r.completed).length }}</div>
            <div class="text-sm text-gray-500">完成人数</div>
          </div>
        </div>

        <!-- 操作按钮 -->
        <div class="mb-6">
          <el-button 
            v-if="!joined"
            type="primary" 
            size="large"
            class="w-full"
            :loading="actionLoading"
            @click="handleJoin"
          >
            参与挑战
          </el-button>
          <el-button 
            v-else
            type="success"
            size="large"
            class="w-full"
            :loading="actionLoading"
            @click="handleCheckin"
          >
            <el-icon><Check /></el-icon>
            今日打卡
          </el-button>
        </div>

        <!-- 排行榜 -->
        <div class="ranking-section bg-white rounded-xl p-4 shadow-sm">
          <h2 class="font-bold mb-4 flex items-center gap-2">
            <el-icon class="text-yellow-500"><Trophy /></el-icon>
            排行榜
          </h2>
          
          <el-empty v-if="ranking.length === 0" description="暂无排行数据" :image-size="60" />
          
          <div v-else class="ranking-list">
            <div 
              v-for="(record, index) in ranking" 
              :key="record.id"
              class="ranking-item flex items-center gap-3 py-3 border-b last:border-b-0"
            >
              <div 
                :class="['rank-badge', { 
                  'gold': index === 0, 
                  'silver': index === 1, 
                  'bronze': index === 2 
                }]"
              >
                {{ index + 1 }}
              </div>
              <el-avatar :src="record.user?.avatar" :size="36">
                {{ record.user?.nickname?.[0] || 'U' }}
              </el-avatar>
              <div class="flex-1">
                <div class="font-medium">{{ record.user?.nickname || '用户' }}</div>
                <div class="text-sm text-gray-500">坚持 {{ record.currentDays }} 天</div>
              </div>
              <el-tag v-if="record.completed" size="small" type="success">已完成</el-tag>
            </div>
          </div>
        </div>
      </div>
    </template>
  </div>
</template>

<style scoped>
.rank-badge {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  font-size: 14px;
  background: #f3f4f6;
  color: #6b7280;
}

.rank-badge.gold {
  background: linear-gradient(135deg, #fbbf24, #f59e0b);
  color: white;
}

.rank-badge.silver {
  background: linear-gradient(135deg, #9ca3af, #6b7280);
  color: white;
}

.rank-badge.bronze {
  background: linear-gradient(135deg, #d97706, #b45309);
  color: white;
}
</style>
