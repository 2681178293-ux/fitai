<template>
  <view class="page">
    <view v-if="loading" class="loading-wrap">
      <text>加载中...</text>
    </view>
    <view v-else-if="detail" class="content">
      <!-- 头部信息卡 -->
      <view class="head-card">
        <image :src="detail.avatar || '/static/default-avatar.png'" class="avatar" mode="aspectFill" />
        <view class="head-info">
          <view class="name-row">
            <text class="name">{{ detail.name }}</text>
            <text class="title">{{ detail.title || '健身教练' }}</text>
          </view>
          <view class="tag-row" v-if="tags.length">
            <text v-for="t in tags" :key="t" class="tag">{{ t }}</text>
          </view>
          <view class="stat-row">
            <view class="stat">
              <text class="stat-value">{{ formatRating(detail.rating) }}</text>
              <text class="stat-label">评分</text>
            </view>
            <view class="stat">
              <text class="stat-value">{{ detail.studentCount || 0 }}</text>
              <text class="stat-label">学员</text>
            </view>
            <view class="stat">
              <text class="stat-value price">¥{{ formatPrice(detail.price) }}</text>
              <text class="stat-label">课时费</text>
            </view>
          </view>
        </view>
      </view>

      <!-- 教练简介 -->
      <view class="card">
        <text class="card-title">教练简介</text>
        <text class="card-text">{{ detail.intro || '这位教练还没有填写简介' }}</text>
      </view>

      <!-- 资质证书 -->
      <view class="card">
        <text class="card-title">资质证书</text>
        <view v-if="certificates.length" class="cert-list">
          <view v-for="(c, i) in certificates" :key="i" class="cert-item">
            <text class="cert-dot">·</text>
            <text class="cert-text">{{ c }}</text>
          </view>
        </view>
        <text v-else class="card-text">暂无证书信息</text>
      </view>

      <view class="placeholder"></view>

      <!-- 底部预约按钮 -->
      <view class="foot-bar">
        <view class="foot-price">
          <text class="foot-price-num">¥{{ formatPrice(detail.price) }}</text>
          <text class="foot-price-unit">/课时</text>
        </view>
        <view class="book-btn" @click="goToBooking">立即预约</view>
      </view>
    </view>
    <view v-else class="empty-wrap">
      <text>教练不存在</text>
    </view>
  </view>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getCoachDetail } from '@/api/coach'

const detail = ref(null)
const loading = ref(true)

const tags = computed(() => {
  const s = detail.value?.specialty
  if (!s) return []
  return String(s)
    .split(/[,，]/)
    .map((t) => t.trim())
    .filter((t) => t)
})

const certificates = computed(() => {
  const c = detail.value?.certificates
  if (!c) return []
  if (Array.isArray(c)) return c.filter((x) => x)
  return String(c)
    .split(/[,，\n]/)
    .map((t) => t.trim())
    .filter((t) => t)
})

onMounted(async () => {
  const pages = getCurrentPages()
  const current = pages[pages.length - 1]
  const id = current.options?.id
  if (!id) {
    loading.value = false
    return
  }
  try {
    const res = await getCoachDetail(id)
    detail.value = res.data
  } catch (e) {
    uni.showToast({ title: '加载失败', icon: 'none' })
  }
  loading.value = false
})

function formatRating(rating) {
  const n = Number(rating)
  return Number.isFinite(n) && n > 0 ? n.toFixed(1) : '暂无'
}

function formatPrice(price) {
  const n = Number(price)
  return Number.isFinite(n) ? n.toFixed(2) : '0.00'
}

function goToBooking() {
  if (!detail.value) return
  uni.navigateTo({
    url: `/pages/coach/booking?coachId=${detail.value.id}&coachName=${encodeURIComponent(detail.value.name || '')}`
  })
}
</script>

<style lang="scss" scoped>
.page {
  min-height: 100vh;
  background: #f5f5f5;
}

.loading-wrap,
.empty-wrap {
  padding: 60px;
  text-align: center;
  color: #999;
}

.head-card {
  display: flex;
  gap: 14px;
  padding: 16px;
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
}

.avatar {
  width: 76px;
  height: 76px;
  border-radius: 38px;
  border: 2px solid rgba(255, 255, 255, 0.4);
  background: #f3f4f6;
}

.head-info {
  flex: 1;
  overflow: hidden;
}

.name-row {
  display: flex;
  align-items: center;
  gap: 8px;
}

.name {
  font-size: 20px;
  font-weight: bold;
  color: #fff;
}

.title {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.85);
}

.tag-row {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  margin-top: 8px;
}

.tag {
  padding: 2px 8px;
  background: rgba(255, 255, 255, 0.2);
  color: #fff;
  border-radius: 10px;
  font-size: 11px;
}

.stat-row {
  display: flex;
  gap: 24px;
  margin-top: 12px;
}

.stat {
  display: flex;
  flex-direction: column;
}

.stat-value {
  font-size: 16px;
  font-weight: 600;
  color: #fff;
}

.stat-value.price {
  color: #fde68a;
}

.stat-label {
  font-size: 11px;
  color: rgba(255, 255, 255, 0.8);
  margin-top: 2px;
}

.card {
  margin: 12px;
  padding: 14px;
  background: #fff;
  border-radius: 12px;
}

.card-title {
  display: block;
  font-size: 15px;
  font-weight: 600;
  color: #111;
  margin-bottom: 8px;
}

.card-text {
  font-size: 14px;
  color: #4b5563;
  line-height: 1.7;
}

.cert-list {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.cert-item {
  display: flex;
  align-items: flex-start;
  gap: 6px;
}

.cert-dot {
  color: #3b82f6;
  font-size: 16px;
  line-height: 1.5;
}

.cert-text {
  flex: 1;
  font-size: 14px;
  color: #4b5563;
  line-height: 1.5;
}

.placeholder {
  height: 80px;
}

.foot-bar {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 16px;
  background: #fff;
  border-top: 1px solid #f3f4f6;
}

.foot-price-num {
  font-size: 18px;
  color: #ef4444;
  font-weight: 600;
}

.foot-price-unit {
  font-size: 12px;
  color: #9ca3af;
}

.book-btn {
  padding: 0 32px;
  height: 42px;
  line-height: 42px;
  background: #3b82f6;
  color: #fff;
  border-radius: 21px;
  font-size: 15px;
}
</style>
