<template>
  <view class="page">
    <view class="header">
      <text class="title">限时秒杀</text>
      <text class="desc">超值优惠，限时抢购</text>
    </view>
    <view v-if="loading" class="loading">加载中...</view>
    <view v-else-if="list.length === 0" class="empty">暂无秒杀活动</view>
    <view v-else class="list">
      <view v-for="act in list" :key="act.id" class="card" @click="goDetail(act.id)">
        <view class="card-header">
          <text class="act-name">{{ act.name }}</text>
          <text class="countdown">{{ countdown(act) }}</text>
        </view>
        <scroll-view scroll-x class="skus">
          <view v-for="sku in (act.skuList || [])" :key="sku.id" class="sku-item" @click.stop="goDetail(act.id)">
            <image :src="sku.image || sku.coverImage" class="sku-img" mode="aspectFill" />
            <text class="sku-price">¥{{ sku.seckillPrice || sku.price }}</text>
            <text class="sku-origin">¥{{ sku.price }}</text>
          </view>
        </scroll-view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getActiveActivities } from '@/api/seckill'

const list = ref([])
const loading = ref(true)

function countdown(act) {
  const now = Date.now()
  const end = new Date(act.endTime).getTime()
  if (now >= end) return '已结束'
  const d = Math.floor((end - now) / 1000)
  const h = Math.floor(d / 3600)
  const m = Math.floor((d % 3600) / 60)
  const s = d % 60
  return `${String(h).padStart(2,'0')}:${String(m).padStart(2,'0')}:${String(s).padStart(2,'0')}`
}

onMounted(async () => {
  try {
    const res = await getActiveActivities()
    list.value = res.data || []
  } catch (e) {}
  loading.value = false
})

function goDetail(id) {
  uni.navigateTo({ url: `/pages/seckill/detail?id=${id}` })
}
</script>

<style lang="scss" scoped>
.page { padding: 16px; background: #f5f5f5; min-height: 100vh; }
.header { margin-bottom: 16px; }
.title { font-size: 20px; font-weight: bold; color: #333; }
.desc { display: block; font-size: 12px; color: #999; }
.loading, .empty { padding: 60px; text-align: center; color: #999; }
.card { background: #fff; border-radius: 12px; margin-bottom: 12px; overflow: hidden; }
.card-header { padding: 12px; background: linear-gradient(135deg,#ef4444,#f97316); color: #fff; display: flex; justify-content: space-between; }
.act-name { font-weight: bold; }
.skus { white-space: nowrap; padding: 12px; }
.sku-item { display: inline-block; width: 100px; margin-right: 12px; text-align: center; }
.sku-img { width: 100px; height: 100px; border-radius: 8px; }
.sku-price { color: #ef4444; font-weight: bold; }
.sku-origin { font-size: 11px; color: #999; text-decoration: line-through; }
</style>
