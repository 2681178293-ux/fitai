<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { getTrainingStats } from '@/api/training'
import { getOrderList } from '@/api/order'

const router = useRouter()
const userStore = useUserStore()

// 训练统计
const stats = ref({
  totalCount: 0,
  monthCount: 0,
  streakDays: 0
})

// 订单数量
const orderCounts = ref({
  unpaid: 0,
  paid: 0,
  shipped: 0
})

onMounted(async () => {
  // 加载训练统计
  try {
    stats.value = await getTrainingStats()
  } catch (e) {}
  
  // 加载订单数量（简化处理）
  try {
    const unpaidRes = await getOrderList({ status: 'unpaid', pageNum: 1, pageSize: 1 })
    orderCounts.value.unpaid = unpaidRes.total || 0
    const paidRes = await getOrderList({ status: 'paid', pageNum: 1, pageSize: 1 })
    orderCounts.value.paid = paidRes.total || 0
    const shippedRes = await getOrderList({ status: 'shipped', pageNum: 1, pageSize: 1 })
    orderCounts.value.shipped = shippedRes.total || 0
  } catch (e) {}
})

// 菜单列表
const menuItems = [
  { icon: '📋', title: '我的订单', path: '/order' },
  { icon: '🗓️', title: '我的预约', path: '/coach/my-booking' },
  { icon: '📚', title: '学习记录', path: '/course/history' },
  { icon: '💪', title: '训练记录', path: '/training/history' },
  { icon: '🥗', title: '饮食记录', path: '/diet' },
  { icon: '⭐', title: '我的收藏', path: '/profile/favorites' },
  { icon: '📍', title: '收货地址', path: '/profile/address' },
  { icon: '⚙️', title: '账号设置', path: '/profile/settings' }
]

function goToMenu(path) {
  router.push(path)
}

function goToOrders(status) {
  router.push({ name: 'Order', query: { status } })
}

async function handleLogout() {
  try {
    await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await userStore.doLogout()
    router.push('/')
  } catch (e) {
    // 取消
  }
}
</script>

<template>
  <div class="max-w-2xl mx-auto px-4 py-6 pb-24 md:pb-6">
    <!-- 用户信息卡片 -->
    <div class="bg-gradient-to-r from-emerald-500 to-cyan-500 rounded-2xl p-6 text-white">
      <div class="flex items-center gap-4">
        <el-avatar :size="64" :src="userStore.avatar" />
        <div>
          <h2 class="text-xl font-bold">{{ userStore.nickname }}</h2>
          <div class="flex items-center gap-2 mt-1 text-sm opacity-90">
            <span v-if="userStore.isVip" class="px-2 py-0.5 bg-white/20 rounded">VIP会员</span>
            <span v-else>普通用户</span>
          </div>
        </div>
      </div>
      
      <!-- 训练统计 -->
      <div class="grid grid-cols-3 gap-4 mt-6 text-center">
        <div>
          <div class="text-2xl font-bold">{{ stats.totalCount }}</div>
          <div class="text-sm opacity-80">累计训练</div>
        </div>
        <div>
          <div class="text-2xl font-bold">{{ stats.monthCount }}</div>
          <div class="text-sm opacity-80">本月训练</div>
        </div>
        <div>
          <div class="text-2xl font-bold">{{ stats.streakDays }}</div>
          <div class="text-sm opacity-80">连续打卡</div>
        </div>
      </div>
    </div>
    
    <!-- 订单入口 -->
    <div class="bg-white rounded-xl p-4 mt-4">
      <div class="flex items-center justify-between mb-4">
        <span class="font-medium">我的订单</span>
        <span class="text-sm text-gray-500 cursor-pointer" @click="goToOrders()">全部订单 &gt;</span>
      </div>
      <div class="grid grid-cols-4 gap-4 text-center">
        <div class="cursor-pointer" @click="goToOrders('unpaid')">
          <el-badge :value="orderCounts.unpaid" :hidden="!orderCounts.unpaid">
            <div class="text-2xl mb-1">💳</div>
          </el-badge>
          <div class="text-xs text-gray-500">待付款</div>
        </div>
        <div class="cursor-pointer" @click="goToOrders('paid')">
          <el-badge :value="orderCounts.paid" :hidden="!orderCounts.paid">
            <div class="text-2xl mb-1">📦</div>
          </el-badge>
          <div class="text-xs text-gray-500">待发货</div>
        </div>
        <div class="cursor-pointer" @click="goToOrders('shipped')">
          <el-badge :value="orderCounts.shipped" :hidden="!orderCounts.shipped">
            <div class="text-2xl mb-1">🚚</div>
          </el-badge>
          <div class="text-xs text-gray-500">待收货</div>
        </div>
        <div class="cursor-pointer" @click="goToOrders('completed')">
          <div class="text-2xl mb-1">✅</div>
          <div class="text-xs text-gray-500">已完成</div>
        </div>
      </div>
    </div>
    
    <!-- 功能菜单 -->
    <div class="bg-white rounded-xl mt-4 overflow-hidden">
      <div
        v-for="item in menuItems"
        :key="item.path"
        class="flex items-center justify-between px-4 py-3 border-b border-gray-100 last:border-0 cursor-pointer hover:bg-gray-50"
        @click="goToMenu(item.path)"
      >
        <div class="flex items-center gap-3">
          <span class="text-xl">{{ item.icon }}</span>
          <span>{{ item.title }}</span>
        </div>
        <span class="text-gray-400">&gt;</span>
      </div>
    </div>
    
    <!-- 退出登录 -->
    <el-button
      type="danger"
      plain
      class="w-full mt-6"
      @click="handleLogout"
    >
      退出登录
    </el-button>
  </div>
</template>
