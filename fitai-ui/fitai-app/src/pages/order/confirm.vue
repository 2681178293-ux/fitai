<template>
  <view class="page">
    <view v-if="!orderItems.length && !loading" class="empty-wrap">
      <text>没有要结算的商品</text>
      <view class="back-btn" @click="goBack">返回</view>
    </view>

    <view v-else class="content">
      <!-- 收货地址 -->
      <view class="address-card" @click="goToAddress">
        <view class="address-info">
          <text class="addr-name">{{ address.name }}</text>
          <text class="addr-phone">{{ address.phone }}</text>
        </view>
        <text class="addr-detail">{{ address.province }}{{ address.city }}{{ address.district }}{{ address.detail }}</text>
        <IconifyIcon icon="mdi:chevron-right" :size="20" color="#999" class="addr-arrow" />
      </view>

      <!-- 商品列表 -->
      <view class="products-card">
        <view
          v-for="item in orderItems"
          :key="item.skuId || item.id"
          class="product-item"
        >
          <image :src="(item.skuImage || item.image) || '/static/banner-default.png'" class="prod-img" mode="aspectFill" />
          <view class="prod-info">
            <text class="prod-name">{{ item.skuName || item.productName }}</text>
            <view class="prod-row">
              <text class="prod-price">¥{{ item.price }}</text>
              <text class="prod-qty">x{{ item.quantity }}</text>
            </view>
          </view>
        </view>
      </view>

      <!-- 备注 -->
      <view class="remark-card">
        <text class="remark-label">订单备注</text>
        <textarea
          v-model="remark"
          class="remark-input"
          placeholder="选填，可告诉卖家特殊需求"
          maxlength="200"
        />
      </view>

      <!-- 金额 -->
      <view class="amount-card">
        <view class="amount-row">
          <text class="amount-label">商品金额</text>
          <text>¥{{ totalAmount }}</text>
        </view>
        <view class="amount-row">
          <text class="amount-label">运费</text>
          <text class="free">免运费</text>
        </view>
        <view class="amount-row total">
          <text class="amount-label">实付金额</text>
          <text class="amount-value">¥{{ totalAmount }}</text>
        </view>
      </view>

      <!-- 提交订单：紧跟实付金额下方，与 PC 订单详情操作区一致 -->
      <view v-if="orderItems.length" class="submit-row">
        <view class="bar-left">
          <text class="bar-total">¥{{ totalAmount }}</text>
          <text class="bar-desc">共 {{ totalQuantity }} 件</text>
        </view>
        <view
          class="bar-btn"
          :class="{ disabled: submitting }"
          @click="handleSubmit"
        >
          {{ submitting ? '提交中...' : '提交订单' }}
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { onLoad, onShow } from '@dcloudio/uni-app'
import { createOrder } from '@/api/order'
import { getToken } from '@/utils/authStorage'
import { getCart } from '@/api/cart'
import { listAddress } from '@/api/address'

const ADDRESS_PLACEHOLDER = '请选择收货地址'

function emptyAddress() {
  return {
    name: ADDRESS_PLACEHOLDER,
    phone: '',
    province: '',
    city: '',
    district: '',
    detail: ''
  }
}

const loading = ref(true)
const submitting = ref(false)
const orderItems = ref([])
const remark = ref('')
const orderType = ref('') // 'direct' | 'cart'
const cartItemIds = ref([])
const routeOptions = ref({})

onLoad((options) => {
  routeOptions.value = options || {}
})

const address = ref(emptyAddress())

const totalAmount = computed(() => {
  return orderItems.value.reduce((sum, item) => {
    return sum + (Number(item.price) || 0) * (item.quantity || 0)
  }, 0).toFixed(2)
})

const totalQuantity = computed(() => {
  return orderItems.value.reduce((sum, item) => sum + (item.quantity || 0), 0)
})

async function refreshAddressFromServer() {
  if (!getToken()) return
  try {
    const res = await listAddress()
    const addrList = Array.isArray(res.data) ? res.data : []
    const def = addrList.find((a) => a.isDefault) || addrList[0]
    if (def) {
      address.value = {
        name: def.name,
        phone: def.phone,
        province: def.province || '',
        city: def.city || '',
        district: def.district || '',
        detail: def.detail || ''
      }
    } else {
      address.value = emptyAddress()
    }
  } catch (e) {
    console.error(e)
  }
}

onMounted(() => {
  const token = getToken()
  if (!token) {
    uni.showToast({ title: '请先登录', icon: 'none' })
    uni.navigateTo({ url: '/pages/login/index' })
    loading.value = false
    return
  }
  loadOrderItems()
})

onShow(() => {
  if (getToken() && orderItems.value.length > 0) {
    refreshAddressFromServer()
  }
})

async function loadOrderItems() {
  const options = Object.keys(routeOptions.value).length > 0 ? routeOptions.value : (getCurrentPages().pop()?.options || {})
  const type = options.type
  const itemStr = options.item
  const idsStr = options.ids

  orderType.value = type || 'direct'

  if (type === 'direct' && itemStr) {
    try {
      const item = JSON.parse(decodeURIComponent(itemStr))
      orderItems.value = [item]
    } catch (e) {
      uni.showToast({ title: '商品信息无效', icon: 'none' })
    }
  } else if (type === 'cart' && idsStr) {
    try {
      const idSet = new Set(
        idsStr.split(',')
          .map(s => String(s.trim()))
          .filter(Boolean)
      )
      const res = await getCart()
      const cartData = res.data || {}
      const allItems = cartData.items || []
      orderItems.value = allItems.filter(i => idSet.has(String(i.id)))
      cartItemIds.value = orderItems.value.map(i => Number(i.id))
      if (orderItems.value.length === 0) {
        uni.showToast({ title: '所选商品已失效', icon: 'none' })
      }
    } catch (e) {
      uni.showToast({ title: e.msg || '加载失败', icon: 'none' })
    }
  }

  await refreshAddressFromServer()

  loading.value = false

  if (orderItems.value.length === 0) {
    setTimeout(() => goBack(), 1500)
  }
}

async function handleSubmit() {
  if (submitting.value || orderItems.value.length === 0) return
  if (address.value.name === ADDRESS_PLACEHOLDER) {
    uni.showToast({ title: '请选择收货地址', icon: 'none' })
    return
  }

  submitting.value = true
  try {
    const orderData = {
      addressSnapshot: {
        name: address.value.name,
        phone: address.value.phone,
        province: address.value.province,
        city: address.value.city,
        district: address.value.district,
        detail: address.value.detail
      },
      remark: remark.value || ''
    }
    // 根据实际数据选择 directBuy 或 cartItemIds
    if (cartItemIds.value.length > 0) {
      orderData.cartItemIds = cartItemIds.value
    } else {
      const item = orderItems.value[0]
      orderData.directBuy = {
        skuId: Number(item.skuId),
        quantity: Number(item.quantity) || 1
      }
    }
    const res = await createOrder(orderData)
    const order = res.data
    uni.showToast({ title: '订单创建成功' })
    // 跳转支付页
    uni.redirectTo({
      url: `/pages/order/pay?orderNo=${order.orderNo}&orderId=${order.id}&amount=${order.payAmount}&subject=${encodeURIComponent('FitAI订单-' + order.orderNo)}`
    })
  } catch (e) {
    uni.showToast({ title: e.msg || '创建失败', icon: 'none' })
  } finally {
    submitting.value = false
  }
}

function goToAddress() {
  uni.navigateTo({ url: '/pages/my/address' })
}

function goBack() {
  uni.navigateBack()
}
</script>

<style lang="scss" scoped>
.page {
  min-height: 100vh;
  background: #f5f5f5;
  padding-bottom: 24px;
}

.empty-wrap {
  padding: 60px;
  text-align: center;
  color: #999;
}

.back-btn {
  margin-top: 16px;
  color: #3b82f6;
  font-size: 14px;
}

.content {
  padding: 12px;
}

.address-card {
  padding: 16px;
  background: #fff;
  border-radius: 12px;
  margin-bottom: 12px;
  position: relative;
}

.address-info {
  display: flex;
  gap: 12px;
  margin-bottom: 8px;
}

.addr-name {
  font-size: 16px;
  font-weight: 500;
  color: #333;
}

.addr-phone {
  font-size: 14px;
  color: #666;
}

.addr-detail {
  display: block;
  font-size: 14px;
  color: #666;
  line-height: 1.5;
}

.addr-arrow {
  position: absolute;
  right: 16px;
  top: 50%;
  transform: translateY(-50%);
}

.products-card {
  padding: 16px;
  background: #fff;
  border-radius: 12px;
  margin-bottom: 12px;
}

.product-item {
  display: flex;
  gap: 12px;
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;
}
.product-item:last-child {
  border-bottom: none;
}

.prod-img {
  width: 80px;
  height: 80px;
  border-radius: 8px;
  background: #f5f5f5;
}

.prod-info {
  flex: 1;
}

.prod-name {
  display: block;
  font-size: 14px;
  color: #333;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.prod-row {
  margin-top: 8px;
  display: flex;
  justify-content: space-between;
}

.prod-price {
  font-size: 16px;
  font-weight: bold;
  color: #ef4444;
}

.prod-qty {
  font-size: 14px;
  color: #999;
}

.remark-card {
  padding: 16px;
  background: #fff;
  border-radius: 12px;
  margin-bottom: 12px;
}

.remark-label {
  font-size: 14px;
  color: #666;
}

.remark-input {
  width: 100%;
  margin-top: 8px;
  padding: 12px;
  min-height: 80px;
  font-size: 14px;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  box-sizing: border-box;
}

.amount-card {
  padding: 16px;
  background: #fff;
  border-radius: 12px;
}

.amount-row {
  display: flex;
  justify-content: space-between;
  padding: 8px 0;
  font-size: 14px;
  color: #666;
}
.amount-row.total {
  margin-top: 8px;
  padding-top: 12px;
  border-top: 1px solid #f0f0f0;
  font-size: 16px;
  font-weight: 500;
  color: #333;
}
.amount-value {
  font-size: 18px;
  font-weight: bold;
  color: #ef4444;
}
.free {
  color: #22c55e;
}

.submit-row {
  margin-top: 12px;
  padding: 16px;
  background: #fff;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.06);
}

.bar-left {
  display: flex;
  flex-direction: column;
}
.bar-total {
  font-size: 18px;
  font-weight: bold;
  color: #ef4444;
}
.bar-desc {
  font-size: 12px;
  color: #999;
}

.bar-btn {
  padding: 0 32px;
  height: 40px;
  line-height: 40px;
  background: linear-gradient(135deg, #ef4444, #f97316);
  color: #fff;
  border-radius: 20px;
  font-size: 16px;
  text-align: center;
}
.bar-btn.disabled {
  opacity: 0.7;
}
</style>
