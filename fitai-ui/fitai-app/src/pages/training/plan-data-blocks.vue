<template>
  <view v-if="data && data._fallbackText" class="fallback">
    <text selectable class="fallback-text">{{ data._fallbackText }}</text>
  </view>
  <view v-else-if="data && !hasAny" class="empty-struct">
    <text class="empty-t">暂无结构化计划内容</text>
  </view>
  <view v-else-if="data" class="blocks">
    <view v-if="warnings.length" class="block warn">
      <view class="block-head warn-head">
        <text class="head-icon">⚠</text>
        <text class="head-title">注意事项</text>
      </view>
      <view v-for="(w, i) in warnings" :key="'w' + i" class="warn-line">
        <text class="bullet">•</text>
        <text class="line-text">{{ w }}</text>
      </view>
    </view>

    <view v-if="tips.length" class="block tips">
      <view class="block-head tips-head">
        <text class="head-icon">✓</text>
        <text class="head-title">训练建议</text>
      </view>
      <view v-for="(t, i) in tips" :key="'t' + i" class="tip-line">
        <text class="bullet">•</text>
        <text class="line-text">{{ t }}</text>
      </view>
    </view>

    <view v-if="nutritionText" class="block nutrition">
      <view class="block-head nut-head">
        <text class="head-icon">🥗</text>
        <text class="head-title">饮食与营养</text>
      </view>
      <text class="nut-body">{{ nutritionText }}</text>
    </view>

    <view v-if="weeklyDays.length" class="weekly">
      <view class="weekly-title-row">
        <text class="weekly-icon">📅</text>
        <text class="weekly-title">周训练安排</text>
        <text class="weekly-count">{{ weeklyDays.length }} 天</text>
      </view>
      <view v-for="(day, di) in weeklyDays" :key="di" class="day-card">
        <view class="day-top">
          <view class="day-badge">
            <text class="day-name">{{ dayLabel(day) }}</text>
          </view>
          <text v-if="day.focus" class="day-focus">{{ day.focus }}</text>
        </view>
        <text v-if="day.warmup" class="mini">热身：{{ day.warmup }}</text>
        <text v-if="day.duration" class="mini dur">时长约 {{ day.duration }} 分钟</text>

        <view v-for="(ex, ei) in dayExercises(day)" :key="ei" class="ex-card">
          <view class="ex-top">
            <text class="ex-name">{{ ex.name || '动作' }}</text>
            <text class="ex-meta">{{ exLine(ex) }}</text>
          </view>
          <text v-if="ex.tips" class="ex-tip">{{ ex.tips }}</text>
        </view>

        <text v-if="day.cooldown" class="mini">放松：{{ day.cooldown }}</text>
        <text v-if="day.notes" class="day-notes">{{ day.notes }}</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { computed } from 'vue'
import { asArrayField } from '@/utils/trainingPlanData'

const props = defineProps({
  data: { type: Object, default: null }
})

const warnings = computed(() => asArrayField(props.data?.warnings))
const tips = computed(() => asArrayField(props.data?.tips))

const nutritionText = computed(() => {
  const n = props.data?.nutrition
  if (n == null) return ''
  if (typeof n === 'string') return n
  try {
    return JSON.stringify(n)
  } catch {
    return String(n)
  }
})

const weeklyDays = computed(() => {
  const d = props.data
  if (!d || d._fallbackText) return []
  if (Array.isArray(d.weeklyPlan) && d.weeklyPlan.length) return d.weeklyPlan
  if (Array.isArray(d.days) && d.days.length) return d.days
  return []
})

const hasAny = computed(() => {
  return (
    warnings.value.length > 0 ||
    tips.value.length > 0 ||
    !!nutritionText.value.trim() ||
    weeklyDays.value.length > 0
  )
})

function dayLabel(day) {
  if (!day) return ''
  if (day.dayName) return day.dayName
  if (day.day != null) return `第${day.day}天`
  return '训练日'
}

function dayExercises(day) {
  if (!day) return []
  if (Array.isArray(day.exercises)) return day.exercises
  if (Array.isArray(day.workouts)) return day.workouts
  return []
}

function exLine(ex) {
  if (!ex) return ''
  const parts = []
  if (ex.sets != null) parts.push(`${ex.sets}组`)
  if (ex.reps != null) parts.push(`× ${ex.reps}`)
  if (ex.rest) parts.push(`休息 ${ex.rest}`)
  return parts.join(' · ')
}
</script>

<style scoped>
.blocks {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.fallback {
  padding: 8px 0;
}
.fallback-text {
  font-size: 12px;
  color: #475569;
  line-height: 1.55;
  white-space: pre-wrap;
  word-break: break-word;
}

.empty-struct {
  padding: 20px;
  text-align: center;
}
.empty-t {
  font-size: 13px;
  color: #94a3b8;
}

.block {
  border-radius: 14px;
  padding: 12px 14px;
}

.warn {
  background: linear-gradient(135deg, #fff7ed 0%, #ffedd5 100%);
  border: 1px solid #fed7aa;
}

.tips {
  background: linear-gradient(135deg, #eff6ff 0%, #dbeafe 100%);
  border: 1px solid #bfdbfe;
}

.nutrition {
  background: linear-gradient(135deg, #f0fdf4 0%, #dcfce7 100%);
  border: 1px solid #bbf7d0;
}

.block-head {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 10px;
}

.head-icon {
  font-size: 16px;
}

.head-title {
  font-size: 14px;
  font-weight: 700;
  color: #0f172a;
}

.warn-line,
.tip-line {
  display: flex;
  gap: 8px;
  margin-bottom: 6px;
  align-items: flex-start;
}

.warn-line:last-child,
.tip-line:last-child {
  margin-bottom: 0;
}

.bullet {
  color: #64748b;
  font-size: 14px;
  line-height: 1.45;
}

.line-text {
  flex: 1;
  font-size: 13px;
  color: #334155;
  line-height: 1.5;
}

.nut-body {
  font-size: 13px;
  color: #166534;
  line-height: 1.55;
}

.weekly {
  margin-top: 4px;
}

.weekly-title-row {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
}

.weekly-icon {
  font-size: 18px;
}

.weekly-title {
  flex: 1;
  font-size: 15px;
  font-weight: 700;
  color: #0f172a;
}

.weekly-count {
  font-size: 12px;
  color: #64748b;
  background: #f1f5f9;
  padding: 2px 10px;
  border-radius: 999px;
}

.day-card {
  background: #fff;
  border-radius: 14px;
  padding: 14px;
  margin-bottom: 12px;
  border: 1px solid #e2e8f0;
  box-shadow: 0 2px 8px rgba(15, 23, 42, 0.04);
}

.day-top {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 8px;
  margin-bottom: 10px;
}

.day-badge {
  background: linear-gradient(135deg, #4f46e5 0%, #6366f1 100%);
  padding: 4px 12px;
  border-radius: 8px;
}

.day-name {
  font-size: 13px;
  font-weight: 700;
  color: #fff;
}

.day-focus {
  font-size: 13px;
  font-weight: 600;
  color: #4338ca;
}

.mini {
  display: block;
  font-size: 12px;
  color: #64748b;
  margin-bottom: 8px;
}

.mini.dur {
  color: #0ea5e9;
  font-weight: 500;
}

.ex-card {
  background: #f8fafc;
  border-radius: 10px;
  padding: 10px 12px;
  margin-bottom: 8px;
  border-left: 3px solid #3b82f6;
}

.ex-top {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.ex-name {
  font-size: 14px;
  font-weight: 600;
  color: #1e293b;
}

.ex-meta {
  font-size: 12px;
  color: #64748b;
}

.ex-tip {
  display: block;
  margin-top: 6px;
  font-size: 12px;
  color: #94a3b8;
  font-style: italic;
}

.day-notes {
  display: block;
  margin-top: 8px;
  font-size: 12px;
  color: #64748b;
  padding: 8px;
  background: #f8fafc;
  border-radius: 8px;
}
</style>
