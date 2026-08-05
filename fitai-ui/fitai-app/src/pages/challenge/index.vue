<template>
  <view class="page">
    <view class="header">
      <text class="title">挑战赛</text>
      <text class="desc">参与挑战，赢取奖励</text>
    </view>
    <view v-if="loading" class="loading">加载中…</view>
    <view v-else-if="loadError && list.length === 0" class="empty err">{{ loadError }}</view>
    <view v-else-if="list.length === 0" class="empty">暂无进行中的挑战</view>
    <view v-else class="list">
      <view v-for="c in list" :key="c.id" class="card" @click="goDetail(c.id)">
        <text class="name">{{ c.name }}</text>
        <text class="desc">{{ c.description }}</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { getActiveChallenges } from '@/api/challenge'

const list = ref([])
const loading = ref(true)
const loadError = ref('')

onShow(() => {
  load()
})

async function load() {
  loading.value = true
  loadError.value = ''
  try {
    const res = await getActiveChallenges()
    list.value = res.data || []
  } catch (e) {
    list.value = []
    loadError.value = e.msg || '加载失败'
    if (loadError.value.includes('数据服务') || loadError.value.includes('数据库')) {
      uni.showToast({
        title: '挑战数据表未就绪，请执行 data/sql/migration_challenge_tables.sql',
        icon: 'none',
        duration: 3500
      })
    } else {
      uni.showToast({ title: loadError.value, icon: 'none' })
    }
  }
  loading.value = false
}

function goDetail(id) {
  uni.navigateTo({ url: `/pages/challenge/detail?id=${id}` })
}
</script>

<style scoped>
.page { padding: 16px; }
.header { margin-bottom: 16px; }
.title { font-size: 18px; font-weight: bold; }
.desc { display: block; font-size: 12px; color: #999; }
.loading, .empty { padding: 60px; text-align: center; color: #999; }
.empty.err { color: #b91c1c; font-size: 13px; line-height: 1.5; padding: 24px; }
.card { background: #fff; border-radius: 12px; padding: 16px; margin-bottom: 12px; }
.name { font-weight: bold; }
.desc { font-size: 12px; color: #666; }
</style>
