<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getCoachById, getCoachSlots, createBooking } from '@/api/coach'
import { useUserStore } from '@/stores/user'

const WEEK_NAMES = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const coach = ref(null)
const loading = ref(true)

const dateList = ref([])
const selectedDate = ref('')
const slots = ref([])
const selectedSlot = ref('')
const slotLoading = ref(false)
const remark = ref('')
const submitting = ref(false)

const tags = computed(() => {
  const s = coach.value?.specialty
  if (!s) return []
  return String(s)
    .split(/[,，]/)
    .map(t => t.trim())
    .filter(t => t)
})

const certificates = computed(() => {
  const c = coach.value?.certificates
  if (!c) return []
  if (Array.isArray(c)) return c.filter(x => x)
  return String(c)
    .split(/[,，\n]/)
    .map(t => t.trim())
    .filter(t => t)
})

onMounted(async () => {
  buildDateList()
  const id = route.params.id
  try {
    coach.value = await getCoachById(id)
  } catch (e) {
    router.push('/coach')
    return
  } finally {
    loading.value = false
  }
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
      label: `${d.getMonth() + 1}/${d.getDate()}`
    })
  }
  dateList.value = list
}

async function loadSlots() {
  if (!coach.value || !selectedDate.value) return
  slotLoading.value = true
  selectedSlot.value = ''
  try {
    const res = await getCoachSlots(coach.value.id, selectedDate.value)
    slots.value = Array.isArray(res) ? res : []
  } catch (e) {
    slots.value = []
  }
  slotLoading.value = false
}

function selectDate(value) {
  if (selectedDate.value === value) return
  selectedDate.value = value
  loadSlots()
}

function selectSlot(slot) {
  if (!slot.available) return
  selectedSlot.value = slot.timeSlot
}

function formatRating(rating) {
  const n = Number(rating)
  return Number.isFinite(n) && n > 0 ? n.toFixed(1) : '暂无'
}

function formatPrice(price) {
  const n = Number(price)
  return Number.isFinite(n) ? n.toFixed(2) : '0.00'
}

async function handleSubmit() {
  if (!userStore.isLoggedIn) {
    router.push({ name: 'Login', query: { redirect: route.fullPath } })
    return
  }
  if (!selectedDate.value) {
    ElMessage.warning('请选择预约日期')
    return
  }
  if (!selectedSlot.value) {
    ElMessage.warning('请选择预约时段')
    return
  }
  submitting.value = true
  try {
    await createBooking({
      coachId: coach.value.id,
      bookingDate: selectedDate.value,
      timeSlot: selectedSlot.value,
      remark: remark.value
    })
    ElMessage.success('预约成功')
    router.push('/coach/my-booking')
  } catch (e) {
    loadSlots()
  } finally {
    submitting.value = false
  }
}
</script>

<template>
  <div class="max-w-6xl mx-auto px-4 py-6 pb-24 md:pb-6">
    <div v-if="loading" class="text-center py-16 text-gray-500">
      加载中...
    </div>

    <div v-else-if="coach" class="grid grid-cols-1 md:grid-cols-3 gap-6">
      <!-- 左侧：教练信息 -->
      <div class="md:col-span-2 space-y-4">
        <div class="bg-white rounded-xl p-6">
          <div class="flex gap-5">
            <el-avatar :size="88" :src="coach.avatar" />
            <div class="flex-1 min-w-0">
              <div class="flex items-center gap-3">
                <h1 class="text-2xl font-bold">{{ coach.name }}</h1>
                <span class="text-sm text-gray-400">{{ coach.title || '健身教练' }}</span>
              </div>
              <div class="flex flex-wrap gap-1 mt-3">
                <el-tag v-for="tag in tags" :key="tag" size="small" type="success">{{ tag }}</el-tag>
              </div>
              <div class="flex flex-wrap gap-6 mt-4 text-sm">
                <div>
                  <div class="text-lg font-bold text-amber-500">{{ formatRating(coach.rating) }}</div>
                  <div class="text-xs text-gray-400">评分</div>
                </div>
                <div>
                  <div class="text-lg font-bold">{{ coach.studentCount || 0 }}</div>
                  <div class="text-xs text-gray-400">学员数</div>
                </div>
                <div>
                  <div class="text-lg font-bold text-red-500">¥{{ formatPrice(coach.price) }}</div>
                  <div class="text-xs text-gray-400">每课时</div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div class="bg-white rounded-xl p-6">
          <h2 class="text-lg font-medium mb-3">教练简介</h2>
          <p class="text-gray-600 leading-relaxed whitespace-pre-line">
            {{ coach.intro || '这位教练还没有填写简介' }}
          </p>
        </div>

        <div class="bg-white rounded-xl p-6">
          <h2 class="text-lg font-medium mb-3">资质证书</h2>
          <ul v-if="certificates.length" class="space-y-2">
            <li v-for="(c, i) in certificates" :key="i" class="text-gray-600 flex gap-2">
              <span class="text-emerald-500">•</span>
              <span>{{ c }}</span>
            </li>
          </ul>
          <p v-else class="text-gray-400">暂无证书信息</p>
        </div>
      </div>

      <!-- 右侧：预约面板 -->
      <div class="md:col-span-1">
        <div class="bg-white rounded-xl p-5 md:sticky md:top-20">
          <h2 class="text-lg font-medium mb-4">预约课程</h2>

          <div class="text-sm text-gray-500 mb-2">选择日期</div>
          <div class="flex gap-2 overflow-x-auto pb-2">
            <button
              v-for="d in dateList"
              :key="d.value"
              class="shrink-0 px-3 py-2 rounded-lg text-xs border transition-colors"
              :class="selectedDate === d.value
                ? 'bg-emerald-500 text-white border-emerald-500'
                : 'bg-gray-50 text-gray-600 border-transparent hover:bg-gray-100'"
              @click="selectDate(d.value)"
            >
              <div>{{ d.week }}</div>
              <div class="font-medium">{{ d.label }}</div>
            </button>
          </div>

          <div class="text-sm text-gray-500 mt-4 mb-2">选择时段</div>
          <div v-loading="slotLoading">
            <div v-if="!slotLoading && slots.length === 0" class="text-center text-gray-400 text-sm py-6">
              该日期暂无可预约时段
            </div>
            <div v-else class="grid grid-cols-2 gap-2">
              <button
                v-for="s in slots"
                :key="s.timeSlot"
                class="px-2 py-2 rounded-lg text-xs border transition-colors"
                :class="!s.available
                  ? 'bg-gray-50 text-gray-300 border-transparent cursor-not-allowed'
                  : selectedSlot === s.timeSlot
                    ? 'bg-emerald-50 text-emerald-600 border-emerald-500'
                    : 'bg-gray-50 text-gray-600 border-transparent hover:bg-gray-100'"
                :disabled="!s.available"
                @click="selectSlot(s)"
              >
                <div class="font-medium">{{ s.timeSlot }}</div>
                <div>{{ s.available ? '可预约' : '已约满' }}</div>
              </button>
            </div>
          </div>

          <div class="text-sm text-gray-500 mt-4 mb-2">备注</div>
          <el-input
            v-model="remark"
            type="textarea"
            :rows="3"
            maxlength="200"
            show-word-limit
            placeholder="填写您的训练目标或特殊需求（选填）"
          />

          <el-button
            type="primary"
            size="large"
            class="w-full mt-4"
            :loading="submitting"
            @click="handleSubmit"
          >
            提交预约
          </el-button>
        </div>
      </div>
    </div>

    <el-empty v-else description="教练不存在" />
  </div>
</template>
