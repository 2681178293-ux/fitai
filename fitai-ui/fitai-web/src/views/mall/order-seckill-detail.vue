<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getSeckillOrderDetail, paySeckillOrder } from '@/api/seckill'
import { getPcPayReturnBaseUrl } from '@/utils/payReturnUrl'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const order = ref(null)

const loadDetail = async () => {
  const orderNo = route.params.orderNo
  loading.value = true
  try {
    const data = await getSeckillOrderDetail(orderNo)
    order.value = data
  } catch (e) {
    ElMessage.error('加载失败')
  } finally {
    loading.value = false
  }
}

const goBack = () => router.push('/order')

const handlePay = async () => {
  if (!order.value || order.value.status !== 0) return
  try {
    const data = await paySeckillOrder(order.value.orderNo, getPcPayReturnBaseUrl())
    if (data && data.payForm) {
      const w = window.open('', '_blank')
      if (w) {
        w.document.write(data.payForm)
        w.document.close()
        ElMessage.info('请在新窗口完成支付')
        loadDetail()
      }
    }
  } catch (e) {
    ElMessage.error(e.message || '支付失败')
  }
}

onMounted(loadDetail)
</script>

<template>
  <div class="max-w-2xl mx-auto px-4 py-6">
    <button class="text-gray-600 mb-4" @click="goBack">← 返回订单列表</button>
    <div v-loading="loading" class="bg-white rounded-xl p-6 shadow">
      <template v-if="order">
        <div class="flex justify-between items-center mb-4 pb-4 border-b">
          <span class="text-gray-500">订单号：{{ order.orderNo }}</span>
          <el-tag :type="order.status === 0 ? 'warning' : order.status === 1 ? 'success' : 'info'">
            {{ order.statusText }}
          </el-tag>
        </div>
        <div class="flex gap-4 py-4">
          <img :src="order.skuImage" class="w-24 h-24 object-cover rounded" />
          <div class="flex-1">
            <p class="font-medium">{{ order.skuName }}</p>
            <p class="text-gray-500 text-sm">x{{ order.quantity }}</p>
          </div>
          <div class="text-right">
            <p class="text-red-500 font-bold">¥{{ order.seckillPrice }}</p>
          </div>
        </div>
        <div class="pt-4 border-t flex justify-between items-center">
          <span class="text-gray-500">实付：</span>
          <span class="text-red-500 font-bold text-lg">¥{{ order.seckillPrice }}</span>
        </div>
        <div v-if="order.status === 0" class="mt-4">
          <el-button type="danger" @click="handlePay">去支付</el-button>
        </div>
      </template>
    </div>
  </div>
</template>
