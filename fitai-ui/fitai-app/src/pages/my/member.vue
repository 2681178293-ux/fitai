<template>
  <view class="page">
    <view class="status-card" :class="{ vip: memberStatus.isVip }">
      <view class="status-header">
        <text class="status-title">{{ memberStatus.isVip ? '会员已开通' : '开通会员' }}</text>
        <text v-if="memberStatus.isVip" class="expire">到期：{{ formatDate(memberStatus.vipExpireTime) }}</text>
        <text v-else class="expire">多档套餐 · 价格与每月 AI 加赠不同 · 支付后自动续期权益</text>
      </view>
    </view>

    <view class="plans-section">
      <view class="section-head">
        <text class="section-title">选择套餐</text>
        <text v-if="plans.length" class="section-hint">点选套餐后去支付</text>
      </view>

      <view v-if="loading" class="loading-hint">加载中…</view>

      <view
        v-for="p in plans"
        v-else
        :key="p.id"
        class="plan-card"
        :class="{ hot: p.id === 2, active: selectedId === p.id }"
        @click="selectPlan(p)"
      >
        <view v-if="p.id === 2" class="rec-badge">推荐</view>
        <view class="plan-header">
          <view class="name-row">
            <text class="plan-name">{{ p.name }}</text>
            <text class="type-tag">{{ planTypeLabel(p.type) }}</text>
          </view>
          <view class="plan-price">
            <text class="price-symbol">¥</text>
            <text class="price-value">{{ formatMoney(p.price) }}</text>
          </view>
        </view>

        <view v-if="p.originalPrice && Number(p.originalPrice) > Number(p.price)" class="plan-origin">
          <text class="origin-label">原价 ¥{{ formatMoney(p.originalPrice) }}</text>
        </view>

        <view v-if="tokenLine(p)" class="token-row">
          <text class="token-icon">◇</text>
          <text class="token-text">每月加赠 AI {{ tokenLine(p) }} tokens</text>
        </view>

        <view v-if="p.durationDays" class="plan-meta">
          <text>有效期 {{ p.durationDays }} 天</text>
        </view>

        <view v-if="p.description" class="plan-desc">{{ p.description }}</view>

        <view v-if="normalizeBenefits(p).length" class="benefits">
          <text v-for="(b, i) in normalizeBenefits(p)" :key="i" class="benefit-tag">{{ b }}</text>
        </view>

        <view class="card-foot" @click.stop="handleSubscribe(p)">
          <text class="foot-btn">立即开通</text>
        </view>
      </view>
    </view>

    <view class="orders-entry" @click="goMemberOrders">
      <text>会员订单记录</text>
      <text class="arrow">›</text>
    </view>

    <view v-if="!loading && plans.length === 0" class="empty-wrap">
      <text class="empty-title">暂无套餐</text>
      <text class="empty-desc">多为数据库未写入会员套餐。请在服务器执行种子脚本后下拉刷新，或联系管理员上架。</text>
      <view class="retry-btn" @click="loadPlans">重新加载</view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { getMemberPlans, getMemberStatus, subscribeMember } from '@/api/member'
import { useUserStore } from '@/store/user'

const userStore = useUserStore()
const memberStatus = ref({ isVip: false, vipExpireTime: null })
const plans = ref([])
const loading = ref(true)
const selectedId = ref(null)

onMounted(() => {
  if (!userStore.isLoggedIn) {
    uni.navigateTo({ url: '/pages/login/index' })
    return
  }
  loadStatus()
  loadPlans()
})

onShow(() => {
  if (userStore.isLoggedIn) {
    loadStatus()
    loadPlans()
  }
})

async function loadStatus() {
  try {
    const res = await getMemberStatus()
    memberStatus.value = res.data || {}
  } catch (e) {
    uni.navigateTo({ url: '/pages/login/index' })
  }
}

async function loadPlans() {
  loading.value = true
  try {
    const res = await getMemberPlans()
    const raw = res.data
    const list = Array.isArray(raw) ? raw : raw?.records || []
    plans.value = list
    if (list.length && selectedId.value == null) {
      const rec = list.find(p => p.id === 2) || list[0]
      selectedId.value = rec?.id ?? null
    }
  } catch (e) {
    plans.value = []
    uni.showToast({ title: e.msg || '加载失败', icon: 'none' })
  }
  loading.value = false
}

function formatDate(str) {
  if (!str) return '-'
  return str.slice(0, 10)
}

function planTypeLabel(type) {
  const m = { 1: '月卡', 2: '季卡', 3: '年卡' }
  return m[type] || '会员'
}

function formatMoney(v) {
  if (v == null || v === '') return '-'
  const n = Number(v)
  return Number.isFinite(n) ? n.toFixed(2) : String(v)
}

function normalizeBenefits(plan) {
  const b = plan?.benefits
  if (b == null) return []
  if (Array.isArray(b)) return b
  if (typeof b === 'string') {
    try {
      const j = JSON.parse(b)
      return Array.isArray(j) ? j : []
    } catch {
      return []
    }
  }
  return []
}

function tokenLine(plan) {
  const n = plan?.aiTokenQuotaMonthly
  if (n == null || n === 0) return null
  if (n >= 10000) {
    const w = n / 10000
    const s = w % 1 === 0 ? String(w) : w.toFixed(1)
    return `约 ${s} 万`
  }
  return `约 ${n}`
}

function selectPlan(p) {
  selectedId.value = p.id
}

function goMemberOrders() {
  uni.navigateTo({ url: '/pages/my/member-orders' })
}

async function handleSubscribe(plan) {
  try {
    const res = await subscribeMember(plan.id)
    const orderNo = res.data?.orderNo
    const amount = res.data?.amount
    if (!orderNo || amount == null) {
      uni.showToast({ title: '创建订单失败', icon: 'none' })
      return
    }
    const name = plan.name || '会员'
    const subject = encodeURIComponent(`FitAI会员-${name}`)
    uni.navigateTo({
      url: `/pages/order/pay?orderNo=${encodeURIComponent(orderNo)}&amount=${amount}&orderType=member&subject=${subject}`
    })
  } catch (e) {
    uni.showToast({ title: e.msg || '操作失败', icon: 'none' })
  }
}
</script>

<style scoped>
.page {
  min-height: 100vh;
  background: linear-gradient(180deg, #f8fafc 0%, #f1f5f9 100%);
  padding: 16px;
  padding-bottom: calc(24px + env(safe-area-inset-bottom));
}

.status-card {
  background: linear-gradient(135deg, #64748b, #475569);
  border-radius: 16px;
  padding: 22px 20px;
  margin-bottom: 16px;
  box-shadow: 0 8px 24px rgba(15, 23, 42, 0.12);
}

.status-card.vip {
  background: linear-gradient(135deg, #f59e0b, #d97706);
}

.status-title {
  font-size: 20px;
  font-weight: bold;
  color: #fff;
}

.expire {
  display: block;
  font-size: 12px;
  color: rgba(255, 255, 255, 0.92);
  margin-top: 8px;
  line-height: 1.45;
}

.plans-section {
  background: #fff;
  border-radius: 16px;
  padding: 16px 14px 8px;
  box-shadow: 0 2px 16px rgba(15, 23, 42, 0.06);
}

.section-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.section-title {
  font-size: 17px;
  font-weight: 700;
  color: #0f172a;
}

.section-hint {
  font-size: 12px;
  color: #94a3b8;
}

.loading-hint {
  text-align: center;
  padding: 24px;
  color: #94a3b8;
  font-size: 14px;
}

.plan-card {
  position: relative;
  padding: 16px 14px 12px;
  border: 2px solid #e2e8f0;
  border-radius: 14px;
  margin-bottom: 14px;
  background: #fafbfc;
}

.plan-card.active {
  border-color: #3b82f6;
  background: #f8fafc;
  box-shadow: 0 0 0 1px rgba(59, 130, 246, 0.2);
}

.plan-card.hot {
  border-color: #c7d2fe;
}

.rec-badge {
  position: absolute;
  top: -1px;
  right: 12px;
  background: linear-gradient(135deg, #6366f1, #8b5cf6);
  color: #fff;
  font-size: 10px;
  padding: 3px 10px;
  border-radius: 0 0 8px 8px;
  font-weight: 600;
}

.plan-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 12px;
}

.name-row {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 8px;
}

.plan-name {
  font-size: 17px;
  font-weight: 700;
  color: #0f172a;
}

.type-tag {
  font-size: 11px;
  padding: 2px 8px;
  background: #e2e8f0;
  color: #475569;
  border-radius: 6px;
}

.plan-price {
  flex-shrink: 0;
  color: #ef4444;
}

.price-symbol {
  font-size: 12px;
}

.price-value {
  font-size: 22px;
  font-weight: 800;
}

.plan-origin {
  margin-top: 6px;
}

.origin-label {
  font-size: 12px;
  color: #9ca3af;
  text-decoration: line-through;
}

.token-row {
  margin-top: 12px;
  padding: 10px 12px;
  background: linear-gradient(90deg, #eff6ff 0%, #eef2ff 100%);
  border-radius: 10px;
  display: flex;
  align-items: center;
  gap: 8px;
  border: 1px solid #bfdbfe;
}

.token-icon {
  color: #2563eb;
  font-size: 12px;
}

.token-text {
  font-size: 14px;
  font-weight: 600;
  color: #1e40af;
}

.plan-meta {
  margin-top: 8px;
  font-size: 12px;
  color: #64748b;
}

.plan-desc {
  font-size: 13px;
  color: #64748b;
  margin-top: 8px;
  line-height: 1.45;
}

.benefits {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 10px;
}

.benefit-tag {
  font-size: 11px;
  padding: 4px 8px;
  background: #eff6ff;
  color: #1d4ed8;
  border-radius: 6px;
}

.card-foot {
  margin-top: 14px;
  padding-top: 12px;
  border-top: 1px solid #e2e8f0;
}

.foot-btn {
  display: block;
  text-align: center;
  height: 36px;
  line-height: 36px;
  background: linear-gradient(135deg, #2563eb, #4f46e5);
  color: #fff;
  border-radius: 10px;
  font-size: 14px;
  font-weight: 600;
}

.orders-entry {
  margin-top: 16px;
  padding: 14px 16px;
  background: #fff;
  border-radius: 12px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 15px;
  color: #334155;
  box-shadow: 0 2px 12px rgba(15, 23, 42, 0.06);
}

.arrow {
  font-size: 20px;
  color: #cbd5e1;
}

.empty-wrap {
  margin-top: 12px;
  padding: 28px 16px;
  text-align: center;
  background: #fff;
  border-radius: 16px;
  border: 1px dashed #e2e8f0;
}

.empty-title {
  display: block;
  font-size: 16px;
  font-weight: 600;
  color: #64748b;
  margin-bottom: 8px;
}

.empty-desc {
  display: block;
  font-size: 12px;
  color: #94a3b8;
  line-height: 1.55;
  margin-bottom: 16px;
}

.retry-btn {
  display: inline-block;
  padding: 10px 28px;
  background: #f1f5f9;
  color: #334155;
  border-radius: 999px;
  font-size: 14px;
  font-weight: 500;
}
</style>
