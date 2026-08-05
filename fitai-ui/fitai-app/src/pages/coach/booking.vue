<template>
  <view class="page">
    <!-- 教练信息 -->
    <view class="coach-bar" v-if="coachName">
      <text class="label">预约教练</text>
      <text class="value">{{ coachName }}</text>
    </view>

    <!-- 日期选择 -->
    <view class="section">
      <text class="section-title">选择日期</text>
      <scroll-view scroll-x class="date-scroll">
        <view class="date-row">
          <view
            v-for="d in dateList"
            :key="d.value"
            class="date-item"
            :class="{ active: d.value === selectedDate }"
            @click="selectDate(d.value)"
          >
            <text class="date-week">{{ d.week }}</text>
            <text class="date-day">{{ d.day }}</text>
            <text class="date-month">{{ d.month }}月</text>
          </view>
        </view>
      </scroll-view>
    </view>

    <!-- 时段选择 -->
    <view class="section">
      <text class="section-title">选择时段</text>
      <view v-if="slotLoading" class="hint">加载中...</view>
      <view v-else-if="slots.length === 0" class="hint">该日期暂无可预约时段</view>
      <view v-else class="slot-grid">
        <view
          v-for="s in slots"
          :key="s.timeSlot"
          class="slot-item"
          :class="{ active: s.timeSlot === selectedSlot, disabled: !s.available }"
          @click="selectSlot(s)"
        >
          <text class="slot-time">{{ s.timeSlot }}</text>
          <text class="slot-status">{{ s.available ? '可预约' : '已约满' }}</text>
        </view>
      </view>
    </view>

    <!-- 备注 -->
    <view class="section">
      <text class="section-title">备注</text>
      <textarea
        v-model="remark"
        class="remark-input"
        placeholder="填写您的训练目标或特殊需求（选填）"
        maxlength="200"
      />
      <text class="count">{{ remark.length }}/200</text>
    </view>

    <view class="placeholder"></view>

    <!-- 提交 -->
    <view class="foot-bar">
      <view class="summary">
        <text class="summary-text">{{ selectedDate || '未选日期' }} {{ selectedSlot || '' }}</text>
      </view>
      <view class="submit-btn" :class="{ disabled: submitting }" @click="handleSubmit">
        {{ submitting ? '提交中...' : '提交预约' }}
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getCoachSlots, createBooking } from '@/api/coach'

const WEEK_NAMES = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']

const coachId = ref(null)
const coachName = ref('')
const dateList = ref([])
const selectedDate = ref('')
const slots = ref([])
const selectedSlot = ref('')
const remark = ref('')
const slotLoading = ref(false)
const submitting = ref(false)

onMounted(() => {
  const pages = getCurrentPages()
  const current = pages[pages.length - 1]
  const options = current.options || {}
  coachId.value = options.coachId || null
  coachName.value = options.coachName ? decodeURIComponent(options.coachName) : ''

  buildDateList()
  if (dateList.value.length) {
    selectedDate.value = dateList.value[0].value
    loadSlots()
  }
})

function formatDate(date) {
  const y = date.getFullYear()
  const m = String(date.getMonth() + 1).padStart(2, '0')
  const d = String(date.getDate()).padStart(2, '0')
  return `${y}-${m}-${d}`
}

function buildDateList() {
  const list = []
  const today = new Date()
  for (let i = 0; i < 14; i++) {
    const d = new Date(today.getFullYear(), today.getMonth(), today.getDate() + i)
    list.push({
      value: formatDate(d),
      week: i === 0 ? '今天' : WEEK_NAMES[d.getDay()],
      day: String(d.getDate()).padStart(2, '0'),
      month: d.getMonth() + 1
    })
  }
  dateList.value = list
}

async function loadSlots() {
  if (!coachId.value || !selectedDate.value) return
  slotLoading.value = true
  selectedSlot.value = ''
  try {
    const res = await getCoachSlots(coachId.value, selectedDate.value)
    slots.value = Array.isArray(res.data) ? res.data : []
  } catch (e) {
    slots.value = []
    uni.showToast({ title: '时段加载失败', icon: 'none' })
  }
  slotLoading.value = false
}

function selectDate(value) {
  if (selectedDate.value === value) return
  selectedDate.value = value
  loadSlots()
}

function selectSlot(s) {
  if (!s.available) {
    uni.showToast({ title: '该时段已约满', icon: 'none' })
    return
  }
  selectedSlot.value = s.timeSlot
}

async function handleSubmit() {
  if (submitting.value) return
  if (!coachId.value) {
    uni.showToast({ title: '教练信息缺失', icon: 'none' })
    return
  }
  if (!selectedDate.value) {
    uni.showToast({ title: '请选择日期', icon: 'none' })
    return
  }
  if (!selectedSlot.value) {
    uni.showToast({ title: '请选择时段', icon: 'none' })
    return
  }
  submitting.value = true
  try {
    await createBooking({
      coachId: Number(coachId.value),
      bookingDate: selectedDate.value,
      timeSlot: selectedSlot.value,
      remark: remark.value
    })
    uni.showToast({ title: '预约成功', icon: 'success' })
    setTimeout(() => {
      uni.redirectTo({ url: '/pages/coach/my-booking' })
    }, 1200)
  } catch (e) {
    // request 已统一提示错误信息
    loadSlots()
  }
  submitting.value = false
}
</script>

<style lang="scss" scoped>
.page {
  min-height: 100vh;
  background: #f5f5f5;
}

.coach-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 14px 16px;
  background: #fff;
}

.label {
  font-size: 14px;
  color: #6b7280;
}

.value {
  font-size: 15px;
  font-weight: 600;
  color: #111;
}

.section {
  margin-top: 10px;
  padding: 14px 16px;
  background: #fff;
}

.section-title {
  display: block;
  font-size: 15px;
  font-weight: 600;
  color: #111;
  margin-bottom: 12px;
}

.date-scroll {
  white-space: nowrap;
  width: 100%;
}

.date-row {
  display: flex;
  gap: 10px;
}

.date-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  min-width: 56px;
  padding: 10px 6px;
  background: #f3f4f6;
  border-radius: 10px;
}

.date-item.active {
  background: #3b82f6;
}

.date-week {
  font-size: 11px;
  color: #6b7280;
}

.date-day {
  font-size: 18px;
  font-weight: 600;
  color: #111;
  margin-top: 2px;
}

.date-month {
  font-size: 11px;
  color: #9ca3af;
  margin-top: 2px;
}

.date-item.active .date-week,
.date-item.active .date-day,
.date-item.active .date-month {
  color: #fff;
}

.hint {
  padding: 30px;
  text-align: center;
  color: #999;
  font-size: 14px;
}

.slot-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 10px;
}

.slot-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 10px 4px;
  background: #f3f4f6;
  border-radius: 10px;
  border: 1px solid transparent;
}

.slot-item.active {
  background: #dbeafe;
  border-color: #3b82f6;
}

.slot-item.disabled {
  background: #f9fafb;
}

.slot-time {
  font-size: 14px;
  color: #111;
}

.slot-status {
  font-size: 11px;
  color: #16a34a;
  margin-top: 4px;
}

.slot-item.disabled .slot-time {
  color: #d1d5db;
}

.slot-item.disabled .slot-status {
  color: #d1d5db;
}

.slot-item.active .slot-time {
  color: #2563eb;
  font-weight: 600;
}

.remark-input {
  width: 100%;
  height: 90px;
  padding: 10px;
  background: #f5f5f5;
  border-radius: 8px;
  font-size: 14px;
  box-sizing: border-box;
}

.count {
  display: block;
  text-align: right;
  margin-top: 6px;
  font-size: 11px;
  color: #9ca3af;
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

.summary-text {
  font-size: 14px;
  color: #374151;
}

.submit-btn {
  padding: 0 32px;
  height: 42px;
  line-height: 42px;
  background: #3b82f6;
  color: #fff;
  border-radius: 21px;
  font-size: 15px;
}

.submit-btn.disabled {
  background: #9ca3af;
}
</style>
