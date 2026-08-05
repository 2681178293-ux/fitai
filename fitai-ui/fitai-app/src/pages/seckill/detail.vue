<template>
  <view class="page">
    <view v-if="loading" class="loading">加载中...</view>
    <view v-else-if="detail" class="content">
      <text class="title">{{ detail.name }}</text>
      <view class="sku-list">
        <view v-for="sku in (detail.skuList || [])" :key="sku.id" class="sku-card" @click="handleRush(sku)">
          <image :src="sku.image || sku.coverImage" class="sku-img" mode="aspectFill" />
          <view class="sku-info">
            <text class="sku-name">{{ sku.skuName || sku.name }}</text>
            <view class="price-row">
              <text class="price">¥{{ sku.seckillPrice || sku.price }}</text>
              <text class="origin">¥{{ sku.price }}</text>
            </view>
            <view class="stock">库存 {{ sku.seckillStock || sku.stock }}</view>
          </view>
          <view class="rush-btn">立即抢购</view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getActivityDetail, doSeckill } from '@/api/seckill'
import { useUserStore } from '@/store/user'

const detail = ref(null)
const loading = ref(true)
const userStore = useUserStore()

onMounted(async () => {
  const pages = getCurrentPages()
  const opts = pages[pages.length - 1]?.options || {}
  const id = opts.id ? parseInt(opts.id) : 0
  if (!id) { loading.value = false; return }
  try {
    const res = await getActivityDetail(id)
    detail.value = res.data
  } catch (e) {}
  loading.value = false
})

async function handleRush(sku) {
  if (!userStore.isLoggedIn) {
    uni.navigateTo({ url: '/pages/login/index' })
    return
  }
  try {
    const res = await doSeckill({ activityId: detail.value.id, seckillSkuId: sku.id })
    const orderNo = res.data
    if (orderNo) {
      uni.navigateTo({ url: `/pages/order/confirm?type=seckill&orderNo=${orderNo}` })
    }
  } catch (e) {
    uni.showToast({ title: e.msg || '抢购失败', icon: 'none' })
  }
}
</script>

<style lang="scss" scoped>
.page { padding: 16px; background: #f5f5f5; min-height: 100vh; }
.loading { padding: 60px; text-align: center; color: #999; }
.title { font-size: 18px; font-weight: bold; margin-bottom: 16px; }
.sku-list { display: flex; flex-direction: column; gap: 12px; }
.sku-card { display: flex; align-items: center; background: #fff; border-radius: 12px; padding: 12px; }
.sku-img { width: 80px; height: 80px; border-radius: 8px; margin-right: 12px; }
.sku-info { flex: 1; }
.sku-name { font-size: 14px; color: #333; }
.price-row { margin-top: 4px; }
.price { color: #ef4444; font-weight: bold; }
.origin { font-size: 12px; color: #999; text-decoration: line-through; margin-left: 8px; }
.stock { font-size: 12px; color: #999; margin-top: 4px; }
.rush-btn { padding: 8px 16px; background: #ef4444; color: #fff; border-radius: 8px; font-size: 13px; }
</style>
