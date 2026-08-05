<template>
  <view class="page">
    <!-- 当前计划 -->
    <view v-if="currentPlan" class="current-wrap">
      <view class="hero-strip" />
      <view class="current-card">
        <view class="card-header">
          <view class="title-row">
            <text class="plan-title">{{ displayPlanName(currentPlan) }}</text>
            <view class="status-pill" :class="'st-' + (currentPlan.status ?? 0)">
              {{ planStatusText(currentPlan.status) }}
            </view>
          </view>
          <text v-if="currentPlan.createTime" class="sub-meta">{{ formatShortDate(currentPlan.createTime) }}</text>
        </view>

        <view v-if="parsedCurrent" class="plan-body">
          <PlanDataBlocks :data="parsedCurrent" />
        </view>
        <view v-else class="plan-body empty-inner">
          <text class="empty-hint">暂无计划详情</text>
        </view>

        <view class="card-actions">
          <view v-if="currentPlan.status === 0" class="action-btn primary" @click="startPlan(currentPlan.id)">
            <text class="btn-icon">▶</text>
            <text>开始训练</text>
          </view>
          <view v-if="currentPlan.status === 1" class="action-btn ghost" @click="abandonPlan(currentPlan.id)">
            放弃计划
          </view>
        </view>
      </view>
    </view>

    <!-- 无计划时显示生成表单 -->
    <view v-else class="generate-wrap">
      <view class="generate-card">
        <text class="generate-title">AI 生成训练计划</text>
        <text class="generate-desc">根据目标与经验生成可执行的周计划</text>
        <view class="form-item">
          <text class="label">训练目标</text>
          <picker :range="goalOptions" range-key="label" @change="e => form.goal = goalOptions[e.detail.value].value">
            <view class="picker">{{ goalLabel }}</view>
          </picker>
        </view>
        <view class="form-item">
          <text class="label">训练经验</text>
          <picker :range="expOptions" range-key="label" @change="e => form.experience = expOptions[e.detail.value].value">
            <view class="picker">{{ expLabel }}</view>
          </picker>
        </view>
        <view class="form-item">
          <text class="label">每周训练天数</text>
          <picker :range="[1,2,3,4,5,6,7]" @change="e => form.weeklyDays = [1,2,3,4,5,6,7][e.detail.value]">
            <view class="picker">{{ form.weeklyDays }} 天</view>
          </picker>
        </view>
        <view class="form-item">
          <text class="label">单次时长（分钟）</text>
          <picker :range="[15,30,45,60,90,120]" @change="e => form.sessionDuration = [15,30,45,60,90,120][e.detail.value]">
            <view class="picker">{{ form.sessionDuration }} 分钟</view>
          </picker>
        </view>
        <view class="generate-btn" :class="{ disabled: generating }" @click="generatePlan">
          {{ generating ? '生成中…' : '生成计划' }}
        </view>
      </view>
    </view>

    <!-- 历史计划 -->
    <view class="history-section">
      <view class="section-head">
        <text class="section-title">历史计划</text>
      </view>
      <view v-if="planList.length === 0" class="empty">暂无历史计划</view>
      <view v-else class="history-list">
        <view
          v-for="p in planList"
          :key="p.id"
          class="history-item"
          @click="viewPlan(p)"
        >
          <view class="hi-left">
            <view class="hi-icon">
              <text class="hi-ico">📋</text>
            </view>
            <view class="hi-text">
              <text class="item-title">{{ displayPlanName(p) }}</text>
              <text class="item-date">{{ p.createTime ? formatShortDate(p.createTime) : '' }}</text>
            </view>
          </view>
          <view class="hi-right">
            <text class="item-status" :class="'st-' + (p.status ?? 0)">{{ planStatusText(p.status) }}</text>
            <text class="hi-arrow">›</text>
          </view>
        </view>
      </view>
    </view>

    <!-- 计划详情 -->
    <view v-if="planDetailShow" class="mask" @click="planDetailShow = false">
      <view class="detail-sheet" @click.stop>
        <view class="sheet-grab" />
        <text class="detail-title">{{ displayPlanName(planDetail) }}</text>
        <text class="detail-sub">{{ planDetail ? planStatusText(planDetail.status) : '' }}</text>
        <scroll-view scroll-y class="detail-scroll">
          <PlanDataBlocks v-if="parsedDetail" :data="parsedDetail" />
          <view v-else class="detail-empty">
            <text>暂无详情</text>
          </view>
        </scroll-view>
        <view class="detail-close" @click="planDetailShow = false">关闭</view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import PlanDataBlocks from './plan-data-blocks.vue'
import {
  generatePlan as apiGenerate,
  getCurrentPlan,
  getPlanList,
  getPlanById,
  startPlan as apiStart,
  abandonPlan as apiAbandon
} from '@/api/training'
import { useUserStore } from '@/store/user'
import { normalizePlanData } from '@/utils/trainingPlanData'

const userStore = useUserStore()
const currentPlan = ref(null)
const planList = ref([])
const generating = ref(false)
const planDetailShow = ref(false)
const planDetail = ref(null)
const pendingPlanId = ref(null)

const form = ref({
  goal: 1,
  experience: 1,
  weeklyDays: 3,
  sessionDuration: 45
})

const goalOptions = [
  { value: 1, label: '减脂' },
  { value: 2, label: '增肌' },
  { value: 3, label: '塑形' },
  { value: 4, label: '保持健康' }
]

const expOptions = [
  { value: 1, label: '新手' },
  { value: 2, label: '有一定基础' },
  { value: 3, label: '经验丰富' }
]

const goalLabel = computed(() => goalOptions.find(o => o.value === form.value.goal)?.label || '减脂')
const expLabel = computed(() => expOptions.find(o => o.value === form.value.experience)?.label || '新手')

const parsedCurrent = computed(() => normalizePlanData(currentPlan.value?.planData))
const parsedDetail = computed(() => normalizePlanData(planDetail.value?.planData))

function displayPlanName(p) {
  if (!p) return '训练计划'
  return p.name || p.title || '训练计划'
}

function formatShortDate(t) {
  if (!t) return ''
  const s = String(t)
  return s.length >= 10 ? s.slice(0, 10) : s
}

onLoad((options) => {
  if (options.planId) {
    pendingPlanId.value = options.planId
  }
})

onMounted(() => {
  if (!userStore.isLoggedIn) {
    uni.navigateTo({ url: '/pages/login/index' })
    return
  }
  loadPlan()
  loadList().then(() => {
    if (pendingPlanId.value) {
      const id = pendingPlanId.value
      pendingPlanId.value = null
      viewPlan({ id })
    }
  })
})

async function loadPlan() {
  try {
    const res = await getCurrentPlan()
    currentPlan.value = res.data
  } catch (e) {
    currentPlan.value = null
  }
}

async function loadList() {
  try {
    const res = await getPlanList({ pageNum: 1, pageSize: 20 })
    const list = res.data?.records || []
    planList.value = list.filter(p => p.id !== currentPlan.value?.id)
  } catch (e) {
    planList.value = []
  }
}

function planStatusText(s) {
  const map = { 0: '草稿', 1: '进行中', 2: '已完成', 3: '已放弃' }
  return map[s] || '-'
}

async function generatePlan() {
  if (generating.value) return
  generating.value = true
  try {
    await apiGenerate({
      goal: form.value.goal,
      experience: form.value.experience,
      weeklyDays: form.value.weeklyDays,
      sessionDuration: form.value.sessionDuration
    })
    uni.showToast({ title: '生成成功' })
    loadPlan()
    loadList()
  } catch (e) {
    uni.showToast({ title: e.msg || '生成失败', icon: 'none' })
  }
  generating.value = false
}

async function startPlan(id) {
  try {
    await apiStart(id)
    uni.showToast({ title: '已开始' })
    loadPlan()
  } catch (e) {
    uni.showToast({ title: e.msg || '操作失败', icon: 'none' })
  }
}

async function abandonPlan(id) {
  uni.showModal({
    title: '确认放弃',
    content: '放弃后计划将变为已放弃状态',
    success: async (modal) => {
      if (modal.confirm) {
        try {
          await apiAbandon(id)
          uni.showToast({ title: '已放弃' })
          loadPlan()
          loadList()
        } catch (e) {
          uni.showToast({ title: e.msg || '操作失败', icon: 'none' })
        }
      }
    }
  })
}

async function viewPlan(p) {
  if (!p?.id) return
  try {
    uni.showLoading({ title: '加载中' })
    const res = await getPlanById(p.id)
    planDetail.value = res.data
    planDetailShow.value = true
  } catch (e) {
    uni.showToast({ title: e.msg || '加载失败', icon: 'none' })
  } finally {
    uni.hideLoading()
  }
}
</script>

<style scoped>
.page {
  min-height: 100vh;
  background: linear-gradient(180deg, #e8f0fe 0%, #f0f4f8 120px, #f5f6f8 100%);
  padding: 12px 14px 28px;
  padding-bottom: calc(28px + env(safe-area-inset-bottom));
}

.current-wrap {
  position: relative;
  margin-bottom: 18px;
}

.hero-strip {
  height: 6px;
  border-radius: 3px;
  background: linear-gradient(90deg, #3b82f6, #8b5cf6, #06b6d4);
  margin: 0 4px 10px;
  opacity: 0.9;
}

.current-card {
  background: #fff;
  border-radius: 16px;
  padding: 18px 16px 16px;
  box-shadow: 0 4px 24px rgba(15, 23, 42, 0.06);
}

.card-header {
  margin-bottom: 14px;
}

.title-row {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 12px;
}

.plan-title {
  flex: 1;
  font-size: 1.05rem;
  font-weight: 700;
  color: #0f172a;
  line-height: 1.35;
}

.status-pill {
  flex-shrink: 0;
  padding: 4px 10px;
  border-radius: 999px;
  font-size: 11px;
  font-weight: 600;
}
.status-pill.st-0 {
  background: #f1f5f9;
  color: #64748b;
}
.status-pill.st-1 {
  background: #dbeafe;
  color: #1d4ed8;
}
.status-pill.st-2 {
  background: #dcfce7;
  color: #15803d;
}
.status-pill.st-3 {
  background: #fee2e2;
  color: #b91c1c;
}

.sub-meta {
  display: block;
  margin-top: 6px;
  font-size: 12px;
  color: #94a3b8;
}

.plan-body {
  margin-bottom: 0;
}

.empty-inner {
  padding: 12px 0;
  text-align: center;
}
.empty-hint {
  font-size: 13px;
  color: #94a3b8;
}

.card-actions {
  display: flex;
  gap: 10px;
  margin-top: 18px;
  padding-top: 4px;
}

.action-btn {
  flex: 1;
  height: 46px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  border-radius: 12px;
  font-size: 15px;
  font-weight: 600;
}

.action-btn.primary {
  background: linear-gradient(135deg, #2563eb 0%, #4f46e5 100%);
  color: #fff;
  box-shadow: 0 4px 14px rgba(37, 99, 235, 0.35);
}

.action-btn.ghost {
  background: #f8fafc;
  color: #64748b;
  border: 1px solid #e2e8f0;
}

.btn-icon {
  font-size: 12px;
  opacity: 0.95;
}

.generate-wrap {
  margin-bottom: 18px;
}

.generate-card {
  background: #fff;
  border-radius: 16px;
  padding: 20px 16px;
  box-shadow: 0 4px 24px rgba(15, 23, 42, 0.06);
}

.generate-title {
  font-size: 1.05rem;
  font-weight: 700;
  color: #0f172a;
  margin-bottom: 6px;
  display: block;
}

.generate-desc {
  font-size: 13px;
  color: #64748b;
  margin-bottom: 18px;
  display: block;
}

.form-item {
  margin-bottom: 14px;
}

.label {
  font-size: 13px;
  color: #64748b;
  display: block;
  margin-bottom: 6px;
}

.picker {
  height: 44px;
  line-height: 44px;
  padding: 0 14px;
  background: #f8fafc;
  border-radius: 10px;
  font-size: 14px;
  color: #334155;
  border: 1px solid #e2e8f0;
}

.generate-btn {
  margin-top: 24px;
  height: 48px;
  line-height: 48px;
  text-align: center;
  background: linear-gradient(135deg, #2563eb 0%, #4f46e5 100%);
  color: #fff;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  box-shadow: 0 4px 14px rgba(37, 99, 235, 0.3);
}

.generate-btn.disabled {
  opacity: 0.65;
  pointer-events: none;
}

.history-section {
  background: #fff;
  border-radius: 16px;
  padding: 16px 14px;
  box-shadow: 0 2px 16px rgba(15, 23, 42, 0.04);
}

.section-head {
  margin-bottom: 12px;
}

.section-title {
  font-size: 16px;
  font-weight: 700;
  color: #0f172a;
}

.empty {
  padding: 28px;
  text-align: center;
  color: #94a3b8;
  font-size: 14px;
}

.history-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.history-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 10px;
  background: #f8fafc;
  border-radius: 12px;
  border: 1px solid #f1f5f9;
}

.hi-left {
  display: flex;
  align-items: center;
  gap: 12px;
  flex: 1;
  min-width: 0;
}

.hi-icon {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  background: linear-gradient(135deg, #e0e7ff 0%, #dbeafe 100%);
  display: flex;
  align-items: center;
  justify-content: center;
}

.hi-ico {
  font-size: 18px;
}

.hi-text {
  display: flex;
  flex-direction: column;
  gap: 4px;
  min-width: 0;
}

.item-title {
  font-size: 14px;
  font-weight: 600;
  color: #1e293b;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.item-date {
  font-size: 12px;
  color: #94a3b8;
}

.hi-right {
  display: flex;
  align-items: center;
  gap: 4px;
  flex-shrink: 0;
}

.item-status {
  font-size: 12px;
  font-weight: 600;
  color: #64748b;
}
.item-status.st-1 {
  color: #2563eb;
}
.item-status.st-2 {
  color: #16a34a;
}
.item-status.st-3 {
  color: #dc2626;
}

.hi-arrow {
  font-size: 20px;
  color: #cbd5e1;
  font-weight: 300;
}

.mask {
  position: fixed;
  inset: 0;
  background: rgba(15, 23, 42, 0.45);
  z-index: 999;
  display: flex;
  align-items: flex-end;
}

.detail-sheet {
  width: 100%;
  max-height: 82vh;
  background: #fff;
  border-radius: 20px 20px 0 0;
  padding-bottom: env(safe-area-inset-bottom);
  box-shadow: 0 -8px 40px rgba(0, 0, 0, 0.12);
}

.sheet-grab {
  width: 36px;
  height: 4px;
  border-radius: 2px;
  background: #e2e8f0;
  margin: 10px auto 8px;
}

.detail-title {
  display: block;
  padding: 4px 8px 4px 18px;
  font-size: 17px;
  font-weight: 700;
  color: #0f172a;
}

.detail-sub {
  display: block;
  padding: 0 18px 12px;
  font-size: 13px;
  color: #3b82f6;
  font-weight: 600;
}

.detail-scroll {
  max-height: 52vh;
  padding: 0 16px 12px;
}

.detail-empty {
  padding: 40px;
  text-align: center;
  color: #94a3b8;
  font-size: 14px;
}

.detail-close {
  padding: 14px;
  text-align: center;
  color: #2563eb;
  font-weight: 600;
  font-size: 15px;
  border-top: 1px solid #f1f5f9;
}
</style>
