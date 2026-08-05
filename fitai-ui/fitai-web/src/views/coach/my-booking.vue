<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getMyBookings, cancelBooking } from '@/api/coach'

const router = useRouter()

const bookings = ref([])
const loading = ref(false)
const total = ref(0)
const activeTab = ref('')
const cancellingId = ref(null)

const tabs = [
  { value: '', label: '全部' },
  { value: 0, label: '待确认' },
  { value: 1, label: '已确认' },
  { value: 2, label: '已完成' },
  { value: 3, label: '已取消' }
]

const query = ref({
  pageNum: 1,
  pageSize: 10,
  status: ''
})

onMounted(() => {
  loadBookings()
})

async function loadBookings() {
  loading.value = true
  try {
    const params = { ...query.value }
    if (params.status === '') delete params.status
    const res = await getMyBookings(params)
    bookings.value = res.records || []
    total.value = Number(res.total) || 0
  } catch (e) {}
  loading.value = false
}

function handleTabChange(status) {
  if (activeTab.value === status) return
  activeTab.value = status
  query.value.status = status
  query.value.pageNum = 1
  loadBookings()
}

function handlePageChange(page) {
  query.value.pageNum = page
  loadBookings()
}

function statusText(status) {
  const map = { 0: '待确认', 1: '已确认', 2: '已完成', 3: '已取消' }
  return map[status] || '-'
}

function statusType(status) {
  if (status === 0) return 'warning'
  if (status === 1) return 'primary'
  if (status === 2) return 'success'
  return 'info'
}

function canCancel(booking) {
  return booking.status === 0 || booking.status === 1
}

async function handleCancel(booking) {
  try {
    await ElMessageBox.confirm('确定要取消这次预约吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '再想想',
      type: 'warning'
    })
  } catch (e) {
    return
  }
  cancellingId.value = booking.id
  try {
    await cancelBooking(booking.id)
    ElMessage.success('已取消预约')
    loadBookings()
  } catch (e) {
  } finally {
    cancellingId.value = null
  }
}

function goToCoachList() {
  router.push('/coach')
}
</script>

<template>
  <div class="max-w-4xl mx-auto px-4 py-6 pb-24 md:pb-6">
    <div class="flex items-center justify-between mb-6">
      <h1 class="text-2xl font-bold">我的预约</h1>
      <el-button text type="primary" @click="goToCoachList">找教练 &gt;</el-button>
    </div>

    <!-- 状态筛选 -->
    <div class="bg-white rounded-xl p-2 mb-4 flex flex-wrap gap-2">
      <button
        v-for="tab in tabs"
        :key="tab.value === '' ? 'all' : tab.value"
        class="px-4 py-2 rounded-lg text-sm transition-colors"
        :class="activeTab === tab.value
          ? 'bg-emerald-50 text-emerald-600 font-medium'
          : 'text-gray-600 hover:bg-gray-100'"
        @click="handleTabChange(tab.value)"
      >
        {{ tab.label }}
      </button>
    </div>

    <!-- 预约列表 -->
    <div v-loading="loading">
      <el-empty v-if="!loading && bookings.length === 0" description="暂无预约记录" />

      <div v-else class="space-y-3">
        <div
          v-for="booking in bookings"
          :key="booking.id"
          class="bg-white rounded-xl p-4"
        >
          <div class="flex items-center justify-between pb-3 border-b border-gray-100">
            <span class="text-xs text-gray-400">{{ booking.bookingNo }}</span>
            <el-tag size="small" :type="statusType(booking.status)">
              {{ statusText(booking.status) }}
            </el-tag>
          </div>

          <div class="py-3 space-y-2 text-sm">
            <div class="flex gap-3">
              <span class="w-16 text-gray-400 shrink-0">教练</span>
              <span>{{ booking.coachName || '-' }}</span>
            </div>
            <div class="flex gap-3">
              <span class="w-16 text-gray-400 shrink-0">时间</span>
              <span>{{ booking.bookingDate }} {{ booking.timeSlot }}</span>
            </div>
            <div v-if="booking.remark" class="flex gap-3">
              <span class="w-16 text-gray-400 shrink-0">备注</span>
              <span class="text-gray-600">{{ booking.remark }}</span>
            </div>
            <div v-if="booking.status === 3 && booking.cancelReason" class="flex gap-3">
              <span class="w-16 text-gray-400 shrink-0">取消原因</span>
              <span class="text-gray-600">{{ booking.cancelReason }}</span>
            </div>
          </div>

          <div class="flex items-center justify-between pt-3 border-t border-gray-100">
            <span class="text-xs text-gray-400">{{ booking.createTime }}</span>
            <el-button
              v-if="canCancel(booking)"
              type="danger"
              plain
              size="small"
              :loading="cancellingId === booking.id"
              @click="handleCancel(booking)"
            >
              取消预约
            </el-button>
          </div>
        </div>
      </div>

      <!-- 分页 -->
      <div v-if="total > 0" class="flex justify-center mt-8">
        <el-pagination
          v-model:current-page="query.pageNum"
          :page-size="query.pageSize"
          :total="total"
          layout="prev, pager, next"
          @current-change="handlePageChange"
        />
      </div>
    </div>
  </div>
</template>
