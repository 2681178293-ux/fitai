<template>
  <view class="page">
    <view class="intro">
      <text class="intro-title">排行榜</text>
      <text class="intro-desc">数据来自 Redis 排行；训练/签到行为需业务侧写入分数后才有名次。若为空表示暂无人上榜。</text>
    </view>

    <view class="tabs">
      <view
        class="tab"
        :class="{ active: mainTab === 'training' }"
        @click="mainTab = 'training'; load()"
      >训练榜</view>
      <view
        class="tab"
        :class="{ active: mainTab === 'checkin' }"
        @click="mainTab = 'checkin'; load()"
      >签到榜</view>
    </view>

    <view class="period-row">
      <text
        v-for="p in periods"
        :key="p.v"
        class="period"
        :class="{ active: period === p.v }"
        @click="period = p.v; load()"
      >{{ p.l }}</text>
    </view>

    <view v-if="loading" class="loading">加载中…</view>
    <view v-else-if="list.length === 0" class="empty">暂无排行数据</view>
    <scroll-view v-else scroll-y class="list">
      <view v-for="row in list" :key="row.userId" class="row-item" :class="'rank-' + rankClass(row.rank)">
        <view class="rank-num">{{ row.rank }}</view>
        <image
          class="avatar"
          :src="row.avatar || '/static/default-avatar.png'"
          mode="aspectFill"
        />
        <view class="info">
          <text class="nickname">{{ row.nickname || '用户' }}</text>
          <text class="score">{{ scoreLabel }} {{ row[scoreKey] ?? 0 }}</text>
        </view>
      </view>
    </scroll-view>
  </view>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import { getTrainingRanking, getCheckinRanking } from '@/api/ranking'

const mainTab = ref('training')
const period = ref('week')
const list = ref([])
const loading = ref(true)

const periods = computed(() => {
  if (mainTab.value === 'training') {
    return [{ v: 'week', l: '本周' }, { v: 'month', l: '本月' }]
  }
  return [{ v: 'week', l: '本周' }, { v: 'month', l: '本月' }]
})

const scoreKey = computed(() => (mainTab.value === 'training' ? 'score' : 'days'))
const scoreLabel = computed(() => (mainTab.value === 'training' ? '训练分' : '打卡'))

watch(mainTab, () => {
  period.value = 'week'
})

onMounted(() => {
  load()
})

function rankClass(r) {
  if (r === 1) return '1'
  if (r === 2) return '2'
  if (r === 3) return '3'
  return '0'
}

async function load() {
  loading.value = true
  list.value = []
  try {
    const params = { period: period.value, limit: 50 }
    const res =
      mainTab.value === 'training'
        ? await getTrainingRanking(params)
        : await getCheckinRanking(params)
    list.value = res.data || []
  } catch (e) {
    uni.showToast({ title: e.msg || '加载失败（需 Redis 与排行写入）', icon: 'none' })
  }
  loading.value = false
}
</script>

<style scoped>
.page {
  min-height: 100vh;
  background: #f0f2f5;
  padding: 12px;
  padding-bottom: calc(24px + env(safe-area-inset-bottom));
}

.intro {
  background: #fff;
  border-radius: 12px;
  padding: 14px;
  margin-bottom: 12px;
}

.intro-title {
  display: block;
  font-size: 17px;
  font-weight: 700;
  color: #0f172a;
}

.intro-desc {
  display: block;
  margin-top: 8px;
  font-size: 12px;
  color: #64748b;
  line-height: 1.5;
}

.tabs {
  display: flex;
  background: #fff;
  border-radius: 12px;
  padding: 4px;
  margin-bottom: 10px;
}

.tab {
  flex: 1;
  text-align: center;
  padding: 10px;
  font-size: 14px;
  color: #64748b;
  border-radius: 8px;
}

.tab.active {
  background: linear-gradient(135deg, #2563eb, #4f46e5);
  color: #fff;
  font-weight: 600;
}

.period-row {
  display: flex;
  gap: 8px;
  margin-bottom: 12px;
}

.period {
  padding: 6px 14px;
  font-size: 13px;
  background: #fff;
  border-radius: 999px;
  color: #64748b;
}

.period.active {
  background: #dbeafe;
  color: #1d4ed8;
  font-weight: 600;
}

.loading,
.empty {
  padding: 40px;
  text-align: center;
  color: #94a3b8;
}

.list {
  max-height: calc(100vh - 280px);
}

.row-item {
  display: flex;
  align-items: center;
  padding: 12px;
  background: #fff;
  border-radius: 12px;
  margin-bottom: 8px;
}

.rank-num {
  width: 28px;
  font-size: 16px;
  font-weight: 800;
  color: #94a3b8;
}

.rank-1 .rank-num {
  color: #ca8a04;
}
.rank-2 .rank-num {
  color: #64748b;
}
.rank-3 .rank-num {
  color: #b45309;
}

.avatar {
  width: 40px;
  height: 40px;
  border-radius: 20px;
  margin-right: 12px;
  background: #e2e8f0;
}

.info {
  flex: 1;
  min-width: 0;
}

.nickname {
  font-size: 15px;
  font-weight: 600;
  color: #0f172a;
  display: block;
}

.score {
  font-size: 12px;
  color: #64748b;
  margin-top: 4px;
  display: block;
}
</style>
