<template>
  <view class="page">
    <view v-if="!token" class="empty-wrap">
      <text>请先登录</text>
      <view class="action-btn" @click="goLogin">去登录</view>
    </view>

    <view v-else-if="loading" class="loading-wrap">
      <text>加载中...</text>
    </view>

    <view v-else-if="items.length === 0" class="empty-wrap">
      <text>购物车是空的</text>
      <view class="action-btn" @click="goToShop">去逛逛</view>
    </view>

    <view v-else class="content">
      <view
        v-for="item in items"
        :key="item.id"
        class="cart-item"
        :class="{ invalid: !item.valid }"
      >
        <view class="item-check" @click="toggleSelect(item)">
          <IconifyIcon
            :icon="item.selected ? 'mdi:checkbox-marked' : 'mdi:checkbox-blank-outline'"
            :size="22"
            :color="item.selected ? '#ef4444' : '#ccc'"
          />
        </view>
        <view class="item-content" @click="goToDetail(item.spuId)">
          <image :src="item.image || '/static/banner-default.png'" class="item-img" mode="aspectFill" />
          <view class="item-info">
            <text class="item-name">{{ item.skuName || item.productName }}</text>
            <view v-if="!item.valid" class="invalid-tip">库存不足</view>
            <view class="item-row">
              <text class="item-price">¥{{ item.price }}</text>
              <view class="qty-control">
                <view
                  class="qty-btn"
                  :class="{ disabled: item.quantity <= 1 }"
                  @click.stop="item.quantity > 1 && changeQty(item, item.quantity - 1)"
                >
                  -
                </view>
                <text class="qty-num">{{ item.quantity }}</text>
                <view
                  class="qty-btn"
                  :class="{ disabled: item.quantity >= (item.stock || 0) }"
                  @click.stop="item.valid && item.quantity < item.stock && changeQty(item, item.quantity + 1)"
                >
                  +
                </view>
              </view>
            </view>
          </view>
        </view>
        <view class="item-del" @click.stop="handleDelete(item)">
          <IconifyIcon icon="mdi:delete-outline" :size="22" color="#999" />
        </view>
      </view>
    </view>

    <!-- 底部操作栏 -->
    <view v-if="token && items.length > 0" class="bottom-bar">
      <view class="bar-left" @click="toggleAll">
        <IconifyIcon
          :icon="allSelected ? 'mdi:checkbox-marked' : 'mdi:checkbox-blank-outline'"
          :size="22"
          :color="allSelected ? '#ef4444' : '#ccc'"
        />
        <text class="bar-all">全选</text>
      </view>
      <view class="bar-right">
        <view class="bar-total">
          <text class="total-label">合计：</text>
          <text class="total-value">¥{{ selectedAmount }}</text>
        </view>
        <view
          class="bar-btn"
          :class="{ disabled: selectedCount === 0 }"
          @click="handleCheckout"
        >
          结算({{ selectedCount }})
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { getCart, updateQuantity, updateSelected, selectAll, deleteItems } from '@/api/cart'
import { getToken } from '@/utils/authStorage'

const token = ref(getToken())
const loading = ref(true)
const items = ref([])

const allSelected = computed(() => {
  const validItems = items.value.filter(i => i.valid)
  if (validItems.length === 0) return false
  return validItems.every(i => i.selected)
})

const selectedCount = computed(() => {
  return items.value
    .filter(i => i.selected && i.valid)
    .reduce((sum, i) => sum + i.quantity, 0)
})

const selectedAmount = computed(() => {
  return items.value
    .filter(i => i.selected && i.valid)
    .reduce((sum, i) => sum + (Number(i.price) || 0) * (i.quantity || 0), 0)
    .toFixed(2)
})

onMounted(() => {
  token.value = getToken()
  if (token.value) {
    loadCart()
  } else {
    loading.value = false
  }
})

// 从其他页面返回时刷新（如登录后、添加购物车后）
onShow(() => {
  const t = getToken()
  if (t) {
    token.value = t
    if (!loading.value) loadCart()
  }
})

async function loadCart() {
  loading.value = true
  try {
    const res = await getCart()
    const data = res.data || {}
    items.value = data.items || []
  } catch (e) {
    if (e.code === 1101) {
      token.value = ''
    }
    uni.showToast({ title: '加载失败', icon: 'none' })
  }
  loading.value = false
}

function toggleSelect(item) {
  if (!item.valid) return
  updateSelected(item.id, !item.selected)
    .then(() => {
      item.selected = !item.selected
    })
    .catch(() => {})
}

function toggleAll() {
  const validItems = items.value.filter(i => i.valid)
  if (validItems.length === 0) return
  const newVal = !allSelected.value
  selectAll(newVal)
    .then(() => {
      items.value.forEach(i => { if (i.valid) i.selected = newVal })
    })
    .catch(() => {})
}

async function changeQty(item, qty) {
  if (qty < 1) return
  if (item.valid && qty > item.stock) qty = item.stock
  try {
    await updateQuantity(item.id, qty)
    item.quantity = qty
  } catch (e) {
    uni.showToast({ title: '操作失败', icon: 'none' })
  }
}

function handleDelete(item) {
  uni.showModal({
    title: '确认删除',
    content: '确定移除此商品？',
    success: async (modal) => {
      if (modal.confirm) {
        try {
          await deleteItems([item.id])
          items.value = items.value.filter(i => i.id !== item.id)
          uni.showToast({ title: '已移除' })
        } catch (e) {
          uni.showToast({ title: '删除失败', icon: 'none' })
        }
      }
    }
  })
}

function handleCheckout() {
  const selected = items.value.filter(i => i.selected && i.valid)
  if (selected.length === 0) {
    uni.showToast({ title: '请选择商品', icon: 'none' })
    return
  }
  const ids = selected.map(i => i.id).join(',')
  uni.navigateTo({ url: `/pages/order/confirm?type=cart&ids=${ids}` })
}

function goLogin() {
  uni.navigateTo({ url: '/pages/login/index' })
}

function goToShop() {
  uni.switchTab({ url: '/pages/shop/index' })
}

function goToDetail(spuId) {
  if (spuId) uni.navigateTo({ url: `/pages/shop/detail?id=${spuId}` })
}
</script>

<style lang="scss" scoped>
.page {
  min-height: 100vh;
  background: #f5f5f5;
  padding-bottom: 60px;
}

.loading-wrap,
.empty-wrap {
  padding: 60px;
  text-align: center;
  color: #999;
}

.action-btn {
  margin-top: 16px;
  display: inline-block;
  padding: 8px 24px;
  background: #3b82f6;
  color: #fff;
  border-radius: 20px;
  font-size: 14px;
}

.content {
  padding: 12px;
}

.cart-item {
  display: flex;
  align-items: center;
  padding: 12px;
  margin-bottom: 12px;
  background: #fff;
  border-radius: 12px;

  &.invalid {
    opacity: 0.6;
  }
}

.item-check {
  padding-right: 12px;
}

.item-content {
  flex: 1;
  display: flex;
  overflow: hidden;
}

.item-img {
  width: 90px;
  height: 90px;
  border-radius: 8px;
  background: #f5f5f5;
}

.item-info {
  flex: 1;
  margin-left: 12px;
  overflow: hidden;
}

.item-name {
  display: block;
  font-size: 14px;
  color: #333;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.invalid-tip {
  margin-top: 4px;
  font-size: 12px;
  color: #ef4444;
}

.item-row {
  margin-top: 8px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.item-price {
  font-size: 16px;
  font-weight: bold;
  color: #ef4444;
}

.qty-control {
  display: flex;
  align-items: center;
}

.qty-btn {
  width: 28px;
  height: 28px;
  line-height: 28px;
  text-align: center;
  border: 1px solid #e5e7eb;
  font-size: 16px;
  color: #333;
  &.disabled {
    color: #ccc;
    border-color: #f0f0f0;
  }
}

.qty-num {
  min-width: 36px;
  text-align: center;
  font-size: 14px;
}

.item-del {
  padding-left: 12px;
}

.bottom-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  height: 56px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 16px;
  background: #fff;
  box-shadow: 0 -2px 10px rgba(0,0,0,0.05);
}

.bar-left {
  display: flex;
  align-items: center;
  gap: 8px;
}

.bar-all {
  font-size: 14px;
  color: #333;
}

.bar-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.bar-total {
  text-align: right;
}

.total-label {
  font-size: 12px;
  color: #666;
}

.total-value {
  font-size: 18px;
  font-weight: bold;
  color: #ef4444;
}

.bar-btn {
  padding: 0 24px;
  height: 36px;
  line-height: 36px;
  background: linear-gradient(135deg, #ef4444, #f97316);
  color: #fff;
  border-radius: 18px;
  font-size: 14px;
}
.bar-btn.disabled {
  background: #e5e7eb !important;
  color: #9ca3af !important;
}
</style>
