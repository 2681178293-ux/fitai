<template>
  <view class="page">
    <!-- 签到卡片 -->
    <view class="checkin-card">
      <view class="card-header">
        <text class="card-title">每日签到</text>
        <text class="continuous">连续签到 {{ status.continuousDays || 0 }} 天</text>
      </view>
      <view class="card-body">
        <view
          class="checkin-btn"
          :class="{ checked: status.isCheckedIn }"
          @click="handleCheckin"
        >
          <text v-if="status.isCheckedIn" class="btn-text">已签到</text>
          <text v-else class="btn-text">立即签到</text>
          <text v-if="!status.isCheckedIn" class="btn-points">+{{ status.todayPoints || 10 }} 积分</text>
        </view>
      </view>
    </view>

    <!-- 签到规则 -->
    <view class="rule-card">
      <text class="rule-title">签到规则</text>
      <text class="rule-desc">第1天+10积分，第2天+20积分，第3天+30积分...</text>
      <text class="rule-desc">连续签到7天可得100积分！</text>
    </view>

    <!-- 本月签到日历 -->
    <view class="calendar-card">
      <text class="calendar-title">{{ currentYear }}年{{ currentMonth }}月</text>
      <view class="calendar-grid">
        <view
          v-for="d in calendarDays"
          :key="d.date"
          class="day-cell"
          :class="{ checked: d.checked, today: d.isToday }"
        >
          <text class="day-num">{{ d.day }}</text>
          <text v-if="d.checked" class="day-check">✓</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getCheckinStatus, doCheckin, getCheckinCalendar } from '@/api/checkin'
import { useUserStore } from '@/store/user'

const userStore = useUserStore()
const status = ref({ isCheckedIn: false, continuousDays: 0, todayPoints: 10 })
const calendarData = ref([])
const currentYear = ref(new Date().getFullYear())
const currentMonth = ref(new Date().getMonth() + 1)

const calendarDays = ref([])

onMounted(() => {
  if (!userStore.isLoggedIn) {
    uni.navigateTo({ url: '/pages/login/index' })
    return
  }
  loadStatus()
  loadCalendar()
})

async function loadStatus() {
  try {
    const res = await getCheckinStatus()
    status.value = res.data || status.value
  } catch (e) {
    uni.navigateTo({ url: '/pages/login/index' })
  }
}

async function loadCalendar() {
  try {
    const res = await getCheckinCalendar({ year: currentYear.value, month: currentMonth.value })
    const list = res.data || []
    const checkedDates = new Set(list.map(c => c.checkinDate))
    const daysInMonth = new Date(currentYear.value, currentMonth.value, 0).getDate()
    const today = new Date().toISOString().slice(0, 10)
    const arr = []
    for (let d = 1; d <= daysInMonth; d++) {
      const dateStr = `${currentYear.value}-${String(currentMonth.value).padStart(2, '0')}-${String(d).padStart(2, '0')}`
      arr.push({
        date: dateStr,
        day: d,
        checked: checkedDates.has(dateStr),
        isToday: dateStr === today
      })
    }
    calendarDays.value = arr
  } catch (e) {}
}

async function handleCheckin() {
  if (status.value.isCheckedIn) return
  try {
    const res = await doCheckin()
    uni.showToast({ title: `签到成功 +${res.data?.points || 0}积分` })
    status.value.isCheckedIn = true
    status.value.continuousDays = res.data?.continuousDays || 1
    loadCalendar()
  } catch (e) {
    uni.showToast({ title: e.msg || '签到失败', icon: 'none' })
  }
}
</script>

<style scoped>
.page {
  min-height: 100vh;
  background: #f5f5f5;
  padding: 16px;
}

.checkin-card {
  background: linear-gradient(135deg, #3b82f6, #60a5fa);
  border-radius: 16px;
  padding: 24px;
  margin-bottom: 16px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.card-title {
  font-size: 20px;
  font-weight: bold;
  color: #fff;
}

.continuous {
  font-size: 14px;
  color: rgba(255,255,255,0.9);
}

.card-body {
  display: flex;
  justify-content: center;
}

.checkin-btn {
  width: 140px;
  height: 140px;
  border-radius: 50%;
  background: rgba(255,255,255,0.3);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  border: 4px solid rgba(255,255,255,0.6);
}

.checkin-btn.checked {
  background: rgba(255,255,255,0.5);
}

.btn-text {
  font-size: 18px;
  font-weight: bold;
  color: #fff;
}

.btn-points {
  font-size: 12px;
  color: rgba(255,255,255,0.9);
  margin-top: 4px;
}

.rule-card {
  background: #fff;
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 16px;
}

.rule-title {
  font-size: 16px;
  font-weight: bold;
  color: #333;
}

.rule-desc {
  display: block;
  font-size: 13px;
  color: #666;
  margin-top: 8px;
}

.calendar-card {
  background: #fff;
  border-radius: 12px;
  padding: 16px;
}

.calendar-title {
  font-size: 16px;
  font-weight: bold;
  color: #333;
  margin-bottom: 12px;
}

.calendar-grid {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 8px;
}

.day-cell {
  aspect-ratio: 1;
  background: #f5f5f5;
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  position: relative;
}

.day-cell.checked {
  background: #dbeafe;
  color: #3b82f6;
}

.day-cell.today {
  border: 2px solid #3b82f6;
}

.day-num {
  font-size: 14px;
}

.day-check {
  font-size: 10px;
  color: #3b82f6;
}
</style>
