<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useCartStore } from '@/stores/cart'
import { createOrder } from '@/api/order'
import { Location, Phone, User } from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const cartStore = useCartStore()

const loading = ref(false)
const submitting = ref(false)
const orderItems = ref([])
const remark = ref('')

// 收货地址（简化处理，实际应从地址管理获取）
const address = ref({
  name: '张三',
  phone: '138****8888',
  province: '广东省',
  city: '深圳市',
  district: '南山区',
  detail: '科技园xxx大厦'
})

// 计算总金额
const totalAmount = computed(() => {
  return orderItems.value.reduce((sum, item) => {
    return sum + item.price * item.quantity
  }, 0).toFixed(2)
})

// 计算商品数量
const totalQuantity = computed(() => {
  return orderItems.value.reduce((sum, item) => sum + item.quantity, 0)
})

// 加载订单商品
const loadOrderItems = () => {
  // 从路由参数或购物车获取
  const type = route.query.type
  
  if (type === 'cart') {
    // 从购物车选中商品创建订单
    orderItems.value = cartStore.selectedItems.map(item => ({
      skuId: item.skuId,
      skuName: item.skuName,
      skuImage: item.skuImage,
      specs: item.specs,
      price: item.price,
      quantity: item.quantity
    }))
  } else if (type === 'direct') {
    // 直接购买（从商品详情页）
    const itemStr = route.query.item
    if (itemStr) {
      try {
        const item = JSON.parse(decodeURIComponent(itemStr))
        orderItems.value = [item]
      } catch (e) {
        console.error('解析商品信息失败:', e)
      }
    }
  }

  if (orderItems.value.length === 0) {
    ElMessage.warning('没有要结算的商品')
    router.back()
  }
}

// 提交订单
const handleSubmit = async () => {
  if (orderItems.value.length === 0) {
    ElMessage.warning('没有要结算的商品')
    return
  }

  submitting.value = true
  try {
    const orderData = {
      addressSnapshot: address.value,
      remark: remark.value
    }

    const type = route.query.type
    if (type === 'direct') {
      // 直接购买
      const item = orderItems.value[0]
      orderData.directBuy = {
        skuId: item.skuId,
        quantity: item.quantity
      }
    } else if (type === 'cart') {
      // 从购物车下单（需要购物车项ID）
      orderData.cartItemIds = cartStore.selectedItems.map(item => item.id)
    }

    const data = await createOrder(orderData)
    ElMessage.success('订单创建成功')

    // 如果是从购物车来的，清空已选商品
    if (type === 'cart') {
      cartStore.clearSelected()
    }

    // 跳转到订单详情页（data 是订单对象，取其 id）
    router.replace(`/order/${data.id}`)
  } catch (error) {
    ElMessage.error(error.message || '创建订单失败')
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  loadOrderItems()
})
</script>

<template>
  <div class="order-confirm-page">
    <h1 class="text-xl font-bold mb-4">确认订单</h1>

    <!-- 收货地址 -->
    <div class="address-card bg-white rounded-lg p-4 mb-4 shadow-sm">
      <div class="flex items-start gap-3">
        <el-icon class="text-xl text-blue-500 mt-1"><Location /></el-icon>
        <div class="flex-1">
          <div class="flex items-center gap-2">
            <span class="font-medium">{{ address.name }}</span>
            <span class="text-gray-500">{{ address.phone }}</span>
          </div>
          <p class="text-gray-600 mt-1">
            {{ address.province }}{{ address.city }}{{ address.district }}{{ address.detail }}
          </p>
        </div>
        <el-button text type="primary">修改</el-button>
      </div>
    </div>

    <!-- 商品列表 -->
    <div class="products-card bg-white rounded-lg p-4 mb-4 shadow-sm">
      <h2 class="font-medium mb-3">商品清单</h2>
      
      <div v-for="item in orderItems" :key="item.skuId" class="product-item flex gap-3 py-3 border-b last:border-b-0">
        <img :src="item.skuImage || '/placeholder.png'" :alt="item.skuName" class="w-20 h-20 object-cover rounded">
        <div class="flex-1">
          <h3 class="font-medium line-clamp-2">{{ item.skuName }}</h3>
          <p v-if="item.specs" class="text-sm text-gray-500 mt-1">
            {{ Object.values(item.specs).join(' / ') }}
          </p>
          <div class="flex justify-between items-center mt-2">
            <span class="text-red-500 font-medium">¥{{ item.price }}</span>
            <span class="text-gray-500">x{{ item.quantity }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 订单备注 -->
    <div class="remark-card bg-white rounded-lg p-4 mb-4 shadow-sm">
      <h2 class="font-medium mb-3">订单备注</h2>
      <el-input
        v-model="remark"
        type="textarea"
        :rows="2"
        placeholder="选填，可以告诉卖家您的特殊需求"
        maxlength="200"
        show-word-limit
      />
    </div>

    <!-- 金额明细 -->
    <div class="amount-card bg-white rounded-lg p-4 shadow-sm">
      <div class="flex justify-between py-2">
        <span class="text-gray-500">商品金额</span>
        <span>¥{{ totalAmount }}</span>
      </div>
      <div class="flex justify-between py-2">
        <span class="text-gray-500">运费</span>
        <span class="text-green-500">免运费</span>
      </div>
      <div class="flex justify-between py-2 border-t mt-2 pt-3">
        <span class="font-medium">实付金额</span>
        <span class="text-red-500 text-xl font-bold">¥{{ totalAmount }}</span>
      </div>
    </div>

    <!-- 提交订单：紧跟实付金额下方 -->
    <div class="action-card bg-white rounded-lg p-4 mt-4 shadow-sm flex justify-between items-center flex-wrap gap-3">
      <div>
        <p class="text-sm text-gray-500">共 {{ totalQuantity }} 件商品</p>
        <p class="text-red-500 text-xl font-bold">¥{{ totalAmount }}</p>
      </div>
      <el-button
        type="danger"
        size="large"
        :loading="submitting"
        @click="handleSubmit"
      >
        提交订单
      </el-button>
    </div>
  </div>
</template>

<style scoped>
.order-confirm-page {
  padding: 16px;
  padding-bottom: 24px;
  max-width: 600px;
  margin: 0 auto;
  background: #f5f5f5;
  min-height: 100vh;
}

.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>
