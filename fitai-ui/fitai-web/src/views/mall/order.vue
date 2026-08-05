<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getOrderList, createPayOrder, mergePayOrder, getOrderLogistics, confirmReceive } from '@/api/order'
import { getMySeckillOrders, paySeckillOrder } from '@/api/seckill'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'
import axios from 'axios'
import { useUserStore } from '@/stores/user'
import { getPcPayReturnBaseUrl } from '@/utils/payReturnUrl'

const PC_API_BASE = import.meta.env.VITE_API_BASE || '/api/pc'
const payReturnBase = getPcPayReturnBaseUrl()

// 静默请求（不弹出错误提示，用于轮询）
const silentRequest = axios.create({
  baseURL: PC_API_BASE,
  timeout: 10000
})
silentRequest.interceptors.request.use((config) => {
  const userStore = useUserStore()
  if (userStore.token) {
    config.headers.Authorization = userStore.token
  }
  return config
})
silentRequest.interceptors.response.use(
  (response) => response.data?.data,
  () => null // 静默处理错误
)

const route = useRoute()
const router = useRouter()
const payingOrderId = ref(null) // 正在支付的订单ID
const mergePaying = ref(false) // 合并支付中
const selectedOrderIds = ref([]) // 已选择的订单ID

const orders = ref([])
const loading = ref(false)
const total = ref(0)
const activeTab = ref(route.query.status || '')
const logisticsVisible = ref(false)
const logisticsLoading = ref(false)
const logisticsData = ref(null)
const confirmingId = ref(null)

// 待付款订单列表
const unpaidOrders = computed(() => orders.value.filter(o => o.status === 'unpaid'))

// 已选择的待付款订单
const selectedUnpaidOrders = computed(() => 
  unpaidOrders.value.filter(o => selectedOrderIds.value.includes(o.id))
)

// 已选订单总金额
const selectedTotalAmount = computed(() => 
  selectedUnpaidOrders.value.reduce((sum, o) => sum + Number(o.payAmount), 0).toFixed(2)
)

// 是否全选
const isAllSelected = computed(() => 
  unpaidOrders.value.length > 0 && 
  unpaidOrders.value.every(o => selectedOrderIds.value.includes(o.id))
)

const tabs = [
  { value: '', label: '全部' },
  { value: 'unpaid', label: '待付款' },
  { value: 'paid', label: '待发货' },
  { value: 'shipped', label: '待收货' },
  { value: 'completed', label: '已完成' }
]

const query = ref({
  pageNum: 1,
  pageSize: 10,
  status: route.query.status || ''
})

onMounted(() => {
  loadOrders()
})

async function loadOrders() {
  loading.value = true
  try {
    const [res, seckillList] = await Promise.all([
      getOrderList(query.value),
      getMySeckillOrders().catch(() => [])
    ])
    const productOrders = (res.records || []).map(o => ({ ...o, orderType: 'product' }))
    const seckillOrders = (Array.isArray(seckillList) ? seckillList : []).map(o => ({
      orderType: 'seckill',
      id: 'seckill_' + o.orderNo,
      orderNo: o.orderNo,
      payAmount: o.seckillPrice ?? o.totalAmount,
      status: o.status === 0 ? 'unpaid' : o.status === 1 ? 'completed' : 'cancelled',
      statusName: o.statusText || (o.status === 0 ? '待付款' : o.status === 1 ? '已完成' : '已取消'),
      items: [{ skuName: o.skuName, skuImage: o.skuImage, price: o.seckillPrice, quantity: o.quantity || 1 }],
      createTime: o.createTime
    }))
    const merged = [...productOrders, ...seckillOrders].sort(
      (a, b) => new Date(b.createTime) - new Date(a.createTime)
    )
    orders.value = merged
    total.value = (Number(res.total) || 0) + seckillOrders.length
  } catch (e) {}
  loading.value = false
}

function handleTabChange(status) {
  activeTab.value = status
  query.value.status = status
  query.value.pageNum = 1
  selectedOrderIds.value = [] // 切换 tab 时清空选择
  loadOrders()
}

// 切换订单选择
function toggleOrderSelect(order) {
  const idx = selectedOrderIds.value.indexOf(order.id)
  if (idx === -1) {
    selectedOrderIds.value.push(order.id)
  } else {
    selectedOrderIds.value.splice(idx, 1)
  }
}

// 全选/取消全选
function toggleSelectAll() {
  if (isAllSelected.value) {
    selectedOrderIds.value = []
  } else {
    selectedOrderIds.value = unpaidOrders.value.map(o => o.id)
  }
}

function handlePageChange(page) {
  query.value.pageNum = page
  loadOrders()
}

function goToDetail(order) {
  if (order.orderType === 'seckill') {
    router.push(`/order/seckill/${order.orderNo}`)
  } else {
    router.push(`/order/${order.id}`)
  }
}

// 支付订单
async function handlePay(order, e) {
  e.stopPropagation() // 阻止冒泡，避免跳转到详情页
  
  payingOrderId.value = order.id
  try {
    if (order.orderType === 'seckill') {
      // 秒杀订单支付
      const data = await paySeckillOrder(order.orderNo, payReturnBase)
      if (data && data.payForm) {
        const payWindow = window.open('', '_blank')
        if (payWindow) {
          payWindow.document.write(data.payForm)
          payWindow.document.close()
          ElMessage.info('请在新窗口完成支付')
          const checkInterval = setInterval(async () => {
            try {
              const res = await request.get(`/seckill/order/${order.orderNo}`)
              if (res && res.status !== 0) {
                clearInterval(checkInterval)
                ElMessage.success('支付成功')
                loadOrders()
              }
            } catch (_) {}
          }, 3000)
          setTimeout(() => clearInterval(checkInterval), 60000)
        } else {
          ElMessage.error('无法打开支付窗口，请检查浏览器是否拦截了弹窗')
        }
      }
    } else {
      // 商城订单支付
      const data = await createPayOrder({
        orderNo: order.orderNo,
        orderType: 'product',
        amount: order.payAmount,
        payType: 'alipay',
        subject: `订单支付-${order.orderNo}`,
        returnUrl: payReturnBase
      })
      if (data.payForm) {
        const payWindow = window.open('', '_blank')
        if (payWindow) {
          payWindow.document.write(data.payForm)
          payWindow.document.close()
          ElMessage.info('请在新窗口完成支付')
          const checkInterval = setInterval(async () => {
            const res = await silentRequest.get(`/order/${order.id}`)
            if (res && res.status !== 'unpaid') {
              clearInterval(checkInterval)
              ElMessage.success('支付成功')
              loadOrders()
            }
          }, 3000)
          setTimeout(() => clearInterval(checkInterval), 60000)
        } else {
          ElMessage.error('无法打开支付窗口，请检查浏览器是否拦截了弹窗')
        }
      }
    }
  } catch (error) {
    ElMessage.error(error.message || '创建支付订单失败')
  } finally {
    payingOrderId.value = null
  }
}

// 合并支付多个订单
async function handleMergePay() {
  if (selectedUnpaidOrders.value.length < 2) {
    ElMessage.warning('请至少选择2个订单进行合并付款')
    return
  }

  mergePaying.value = true
  try {
    const orderNos = selectedUnpaidOrders.value.map(o => o.orderNo)
    const data = await mergePayOrder({
      orderNos,
      orderType: 'product',
      totalAmount: Number(selectedTotalAmount.value),
      payType: 'alipay',
      subject: `合并支付${orderNos.length}笔订单`,
      returnUrl: payReturnBase
    })

    if (data.payForm) {
      const payWindow = window.open('', '_blank')
      if (payWindow) {
        payWindow.document.write(data.payForm)
        payWindow.document.close()

        ElMessage.info('请在新窗口完成支付')

        // 轮询支付状态（使用静默请求，不弹出错误）
        const orderIdToCheck = selectedUnpaidOrders.value[0].id
        const checkInterval = setInterval(async () => {
          const res = await silentRequest.get(`/order/${orderIdToCheck}`)
          if (res && res.status !== 'unpaid') {
            clearInterval(checkInterval)
            ElMessage.success('支付成功')
            selectedOrderIds.value = []
            loadOrders()
          }
        }, 3000)

        setTimeout(() => clearInterval(checkInterval), 60000)
      } else {
        ElMessage.error('无法打开支付窗口，请检查浏览器是否拦截了弹窗')
      }
    }
  } catch (error) {
    ElMessage.error(error.message || '合并支付失败')
  } finally {
    mergePaying.value = false
  }
}

// 查看物流（仅普通订单且已发货）
async function handleLogistics(order, e) {
  e.stopPropagation()
  if (order.orderType !== 'product' || order.status !== 'shipped') return
  logisticsVisible.value = true
  logisticsData.value = null
  logisticsLoading.value = true
  try {
    const res = await getOrderLogistics(order.id)
    logisticsData.value = res.data ?? res
  } catch (err) {
    ElMessage.error(err?.response?.data?.msg || '查询物流失败')
  } finally {
    logisticsLoading.value = false
  }
}

// 确认收货
async function handleConfirmReceive(order, e) {
  e.stopPropagation()
  if (order.orderType !== 'product' || order.status !== 'shipped') return
  confirmingId.value = order.id
  try {
    await confirmReceive(order.id)
    ElMessage.success('已确认收货')
    loadOrders()
  } catch (err) {
    ElMessage.error(err?.response?.data?.msg || '操作失败')
  } finally {
    confirmingId.value = null
  }
}
</script>

<template>
  <div class="max-w-4xl mx-auto px-4 py-6 pb-24 md:pb-6">
    <h1 class="text-2xl font-bold mb-6">我的订单</h1>
    
    <!-- 订单状态 Tab -->
    <div class="flex gap-2 mb-6 overflow-x-auto">
      <button
        v-for="tab in tabs"
        :key="tab.value"
        @click="handleTabChange(tab.value)"
        class="px-4 py-2 rounded-lg whitespace-nowrap"
        :class="activeTab === tab.value 
          ? 'bg-emerald-500 text-white' 
          : 'bg-white text-gray-600 hover:bg-gray-100'"
      >
        {{ tab.label }}
      </button>
    </div>
    
    <!-- 合并付款操作栏（只在有待付款订单时显示） -->
    <div 
      v-if="unpaidOrders.length > 1" 
      class="bg-white rounded-xl p-4 mb-4 flex items-center justify-between"
    >
      <div class="flex items-center gap-4">
        <el-checkbox 
          :model-value="isAllSelected" 
          :indeterminate="selectedOrderIds.length > 0 && !isAllSelected"
          @change="toggleSelectAll"
        >
          全选待付款
        </el-checkbox>
        <span class="text-gray-500 text-sm">
          已选 {{ selectedUnpaidOrders.length }} 笔订单
        </span>
      </div>
      <div class="flex items-center gap-4">
        <span v-if="selectedUnpaidOrders.length > 0">
          合计：<span class="text-red-500 font-bold text-lg">¥{{ selectedTotalAmount }}</span>
        </span>
        <el-button 
          type="danger" 
          :disabled="selectedUnpaidOrders.length < 2"
          :loading="mergePaying"
          @click="handleMergePay"
        >
          合并付款 ({{ selectedUnpaidOrders.length }})
        </el-button>
      </div>
    </div>

    <!-- 订单列表 -->
    <div v-loading="loading">
      <div v-if="orders.length === 0" class="bg-white rounded-xl p-16 text-center">
        <p class="text-gray-500">暂无订单</p>
      </div>
      
      <div v-else class="space-y-4">
        <div
          v-for="order in orders"
          :key="order.id"
          class="bg-white rounded-xl p-4 cursor-pointer hover:shadow-md transition-shadow"
          :class="{ 'ring-2 ring-emerald-500': selectedOrderIds.includes(order.id) }"
          @click="goToDetail(order)"
        >
          <div class="flex items-center justify-between mb-4">
            <div class="flex items-center gap-3">
              <!-- 待付款订单显示多选框 -->
              <el-checkbox 
                v-if="order.status === 'unpaid' && unpaidOrders.length > 1"
                :model-value="selectedOrderIds.includes(order.id)"
                @click.stop="toggleOrderSelect(order)"
              />
              <span class="text-sm text-gray-500">订单号：{{ order.orderNo }}</span>
            </div>
            <el-tag :type="order.status === 'unpaid' ? 'warning' : 'success'" size="small">
              {{ order.statusName }}
            </el-tag>
          </div>
          
          <!-- 订单商品 -->
          <div
            v-for="item in order.items"
            :key="item.id"
            class="flex gap-4 py-2"
          >
            <img :src="item.skuImage" class="w-16 h-16 object-cover rounded" />
            <div class="flex-1">
              <p class="text-sm line-clamp-1">{{ item.skuName }}</p>
              <p class="text-sm text-gray-500">x{{ item.quantity }}</p>
            </div>
            <div class="text-right">
              <p class="text-red-500">¥{{ item.price }}</p>
            </div>
          </div>
          
          <div class="flex justify-between items-center mt-4 pt-4 border-t">
            <span class="text-sm text-gray-500">共 {{ order.items.reduce((s, i) => s + i.quantity, 0) }} 件商品</span>
            <div class="flex items-center gap-4">
              <span>
                实付：<span class="text-red-500 font-bold">¥{{ order.payAmount }}</span>
              </span>
              <el-button
                v-if="order.status === 'unpaid'"
                type="danger"
                size="small"
                :loading="payingOrderId === order.id"
                @click="handlePay(order, $event)"
              >
                付款
              </el-button>
              <template v-if="order.orderType === 'product' && order.status === 'shipped'">
                <el-button size="small" @click="handleLogistics(order, $event)">查看物流</el-button>
                <el-button type="primary" size="small" :loading="confirmingId === order.id" @click="handleConfirmReceive(order, $event)">确认收货</el-button>
              </template>
            </div>
          </div>
        </div>
      </div>

      <!-- 物流轨迹弹窗 -->
      <el-dialog v-model="logisticsVisible" title="物流轨迹" width="560px">
        <div v-loading="logisticsLoading" class="min-h-[200px]">
          <template v-if="logisticsData">
            <div class="mb-4 flex gap-4 text-sm">
              <span>{{ logisticsData.expressCompanyName }} {{ logisticsData.expressNo }}</span>
              <el-tag size="small">{{ logisticsData.status }}</el-tag>
            </div>
            <el-timeline v-if="logisticsData.traces?.length">
              <el-timeline-item
                v-for="(item, idx) in logisticsData.traces"
                :key="idx"
                :timestamp="item.time"
                placement="top"
              >
                <div>{{ item.context }}</div>
                <div v-if="item.location" class="text-gray-500 text-xs mt-1">{{ item.location }}</div>
              </el-timeline-item>
            </el-timeline>
            <div v-else class="text-gray-500">暂无轨迹信息</div>
          </template>
        </div>
      </el-dialog>
      
      <!-- 分页 -->
      <div v-if="total > query.pageSize" class="flex justify-center mt-8">
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
