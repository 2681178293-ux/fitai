<script setup>
import { ref, onMounted, watch } from 'vue'
import request from '@/utils/request'
import { Trophy, Sunny, Calendar, Medal } from '@element-plus/icons-vue'

const activeTab = ref('training')
const period = ref('week')
const loading = ref(false)
const rankingList = ref([])

// 周期选项
const periodOptions = {
  training: [
    { value: 'day', label: '今日' },
    { value: 'week', label: '本周' },
    { value: 'month', label: '本月' }
  ],
  checkin: [
    { value: 'week', label: '本周' },
    { value: 'month', label: '本月' },
    { value: 'total', label: '总榜' }
  ]
}

// 加载排行榜
const loadRanking = async () => {
  loading.value = true
  try {
    const url = activeTab.value === 'training' 
      ? '/ranking/training' 
      : '/ranking/checkin'
    const { data } = await request.get(url, { params: { period: period.value, limit: 50 } })
    rankingList.value = data || []
  } catch (error) {
    console.error('加载排行榜失败:', error)
    rankingList.value = []
  } finally {
    loading.value = false
  }
}

// 获取排名样式
const getRankClass = (rank) => {
  if (rank === 1) return 'gold'
  if (rank === 2) return 'silver'
  if (rank === 3) return 'bronze'
  return ''
}

// 切换 Tab 时重置周期
watch(activeTab, () => {
  period.value = activeTab.value === 'training' ? 'week' : 'month'
  loadRanking()
})

watch(period, () => {
  loadRanking()
})

onMounted(() => {
  loadRanking()
})
</script>

<template>
  <div class="ranking-page p-4">
    <div class="page-header mb-6">
      <h1 class="text-2xl font-bold flex items-center gap-2">
        <el-icon class="text-yellow-500"><Trophy /></el-icon>
        排行榜
      </h1>
      <p class="text-gray-500 mt-1">和健身达人一起进步</p>
    </div>

    <!-- Tab 切换 -->
    <el-tabs v-model="activeTab" class="mb-4">
      <el-tab-pane label="训练排行" name="training">
        <template #label>
          <span class="flex items-center gap-1">
            <el-icon><Sunny /></el-icon>
            训练排行
          </span>
        </template>
      </el-tab-pane>
      <el-tab-pane label="打卡排行" name="checkin">
        <template #label>
          <span class="flex items-center gap-1">
            <el-icon><Calendar /></el-icon>
            打卡排行
          </span>
        </template>
      </el-tab-pane>
    </el-tabs>

    <!-- 周期选择 -->
    <div class="period-selector mb-4">
      <el-radio-group v-model="period" size="small">
        <el-radio-button 
          v-for="opt in periodOptions[activeTab]" 
          :key="opt.value" 
          :value="opt.value"
        >
          {{ opt.label }}
        </el-radio-button>
      </el-radio-group>
    </div>

    <!-- 排行榜列表 -->
    <div v-loading="loading" class="ranking-list bg-white rounded-xl shadow-sm overflow-hidden">
      <el-empty v-if="!loading && rankingList.length === 0" description="暂无排行数据" />
      
      <!-- 前三名 -->
      <div v-if="rankingList.length >= 3" class="top-three flex justify-center items-end gap-4 py-6 px-4 bg-gradient-to-b from-yellow-50 to-white">
        <!-- 第二名 -->
        <div class="top-item text-center">
          <el-avatar :src="rankingList[1]?.avatar" :size="60" class="border-4 border-gray-300">
            {{ rankingList[1]?.nickname?.[0] || '2' }}
          </el-avatar>
          <div class="mt-2 font-medium">{{ rankingList[1]?.nickname }}</div>
          <div class="text-sm text-gray-500">
            {{ activeTab === 'training' ? rankingList[1]?.score + '分' : rankingList[1]?.days + '天' }}
          </div>
          <div class="rank-medal silver mt-1">🥈</div>
        </div>
        
        <!-- 第一名 -->
        <div class="top-item text-center -mt-4">
          <div class="relative">
            <el-avatar :src="rankingList[0]?.avatar" :size="80" class="border-4 border-yellow-400">
              {{ rankingList[0]?.nickname?.[0] || '1' }}
            </el-avatar>
            <span class="absolute -top-2 -right-2 text-2xl">👑</span>
          </div>
          <div class="mt-2 font-bold text-lg">{{ rankingList[0]?.nickname }}</div>
          <div class="text-sm text-gray-500">
            {{ activeTab === 'training' ? rankingList[0]?.score + '分' : rankingList[0]?.days + '天' }}
          </div>
          <div class="rank-medal gold mt-1">🥇</div>
        </div>
        
        <!-- 第三名 -->
        <div class="top-item text-center">
          <el-avatar :src="rankingList[2]?.avatar" :size="60" class="border-4 border-amber-600">
            {{ rankingList[2]?.nickname?.[0] || '3' }}
          </el-avatar>
          <div class="mt-2 font-medium">{{ rankingList[2]?.nickname }}</div>
          <div class="text-sm text-gray-500">
            {{ activeTab === 'training' ? rankingList[2]?.score + '分' : rankingList[2]?.days + '天' }}
          </div>
          <div class="rank-medal bronze mt-1">🥉</div>
        </div>
      </div>

      <!-- 其他排名 -->
      <div class="other-ranks">
        <div 
          v-for="item in rankingList.slice(3)" 
          :key="item.rank"
          class="rank-item flex items-center gap-3 px-4 py-3 border-b last:border-b-0"
        >
          <div class="rank-number w-8 text-center font-bold text-gray-400">
            {{ item.rank }}
          </div>
          <el-avatar :src="item.avatar" :size="40">
            {{ item.nickname?.[0] || 'U' }}
          </el-avatar>
          <div class="flex-1">
            <div class="font-medium">{{ item.nickname }}</div>
          </div>
          <div class="text-right">
            <div class="font-bold text-blue-500">
              {{ activeTab === 'training' ? item.score : item.days }}
            </div>
            <div class="text-xs text-gray-400">
              {{ activeTab === 'training' ? '分' : '天' }}
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.top-item {
  min-width: 100px;
}

.rank-medal {
  font-size: 24px;
}
</style>
