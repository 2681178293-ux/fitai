<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { CircleCheck, CircleClose, Timer } from '@element-plus/icons-vue'
import request from '@/utils/request'

const route = useRoute()
const router = useRouter()

const loading = ref(true)
const payResult = ref(null)
const error = ref(null)

// 支付结果状态配置
const statusConfig = {
  success: {
    icon: CircleCheck,
    title: '支付成功',
    color: 'text-green-500',
    bgColor: 'bg-green-50'
  },
  pending: {
    icon: Timer,
    title: '支付处理中',
    color: 'text-yellow-500',
    bgColor: 'bg-yellow-50'
  },
  failed: {
    icon: CircleClose,
    title: '支付失败',
    color: 'text-red-500',
    bgColor: 'bg-red-50'
  }
}

// 获取当前状态配置
const currentStatus = ref(statusConfig.pending)

// 查询支付结果
const queryPayResult = async () => {
  const orderNo = route.query.orderNo
  
  if (!orderNo) {
    error.value = '订单号不存在'
    loading.value = false
    return
  }
  
  try {
    // 调用后端同步跳转验签接口
    const data = await request.get('/pay/return/alipay', {
      params: route.query
    })
    
    payResult.value = data
    
    // 根据支付状态设置显示
    if (data.status === 1) {
      currentStatus.value = statusConfig.success
    } else if (data.status === 0) {
      currentStatus.value = statusConfig.pending
    } else {
      currentStatus.value = statusConfig.failed
    }
  } catch (e) {
    console.error('查询支付结果失败', e)
    error.value = e.message || '查询支付结果失败'
    currentStatus.value = statusConfig.failed
  } finally {
    loading.value = false
  }
}

// 查看订单
const goToOrder = () => {
  router.push('/order')
}

// 继续购物
const goToMall = () => {
  router.push('/mall')
}

onMounted(() => {
  queryPayResult()
})
</script>

<template>
  <div class="order-result-page" v-loading="loading">
    <div v-if="!loading" class="result-card bg-white rounded-lg shadow-sm p-8 text-center">
      <!-- 状态图标 -->
      <div :class="['icon-wrapper mx-auto mb-6 w-20 h-20 rounded-full flex items-center justify-center', currentStatus.bgColor]">
        <el-icon :class="['text-5xl', currentStatus.color]">
          <component :is="currentStatus.icon" />
        </el-icon>
      </div>
      
      <!-- 状态标题 -->
      <h1 :class="['text-2xl font-bold mb-2', currentStatus.color]">
        {{ currentStatus.title }}
      </h1>
      
      <!-- 错误信息 -->
      <p v-if="error" class="text-gray-500 mb-6">{{ error }}</p>
      
      <!-- 支付信息 -->
      <template v-if="payResult">
        <div class="pay-info text-gray-600 mb-6">
          <p v-if="payResult.transactionNo" class="mb-2">
            交易流水号：<span class="font-mono">{{ payResult.transactionNo }}</span>
          </p>
          <p v-if="payResult.tradeNo" class="mb-2">
            支付宝交易号：<span class="font-mono">{{ payResult.tradeNo }}</span>
          </p>
        </div>
      </template>
      
      <!-- 操作按钮 -->
      <div class="actions flex justify-center gap-4 mt-8">
        <el-button size="large" @click="goToMall">继续购物</el-button>
        <el-button type="primary" size="large" @click="goToOrder">查看订单</el-button>
      </div>
      
      <!-- 提示信息 -->
      <p v-if="currentStatus === statusConfig.pending" class="text-gray-400 text-sm mt-6">
        支付结果可能存在延迟，请稍后刷新或查看订单状态
      </p>
    </div>
  </div>
</template>

<style scoped>
.order-result-page {
  min-height: 60vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px 16px;
  background: #f5f5f5;
}

.result-card {
  max-width: 500px;
  width: 100%;
}
</style>
