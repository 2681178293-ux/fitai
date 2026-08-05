<template>
  <view class="page">
    <view v-if="loading" class="loading-wrap">
      <text>加载中...</text>
    </view>
    <view v-else-if="product" class="content">
      <!-- 商品图片 -->
      <swiper class="banner-swiper" indicator-dots circular>
        <swiper-item v-for="(img, i) in imageList" :key="i">
          <image :src="img" class="banner-img" mode="aspectFill" />
        </swiper-item>
      </swiper>

      <!-- 商品信息 -->
      <view class="info-card">
        <view class="price-row">
          <view class="price-left">
            <text class="price-symbol">¥</text>
            <text class="price-value">{{ currentPrice }}</text>
            <text v-if="selectedSku?.originalPrice" class="original-price">¥{{ selectedSku.originalPrice }}</text>
          </view>
          <view class="fav-btn" @click="handleFavorite">
            <IconifyIcon
              :icon="product.userFavorited ? 'mdi:star' : 'mdi:star-outline'"
              :size="24"
              :color="product.userFavorited ? '#f59e0b' : '#999'"
            />
            <text class="fav-text">{{ product.userFavorited ? '已收藏' : '收藏' }}</text>
          </view>
        </view>
        <text class="product-name">{{ product.name }}</text>
        <view class="meta-row">
          <text>已售 {{ product.salesCount || 0 }} 件</text>
          <text v-if="selectedSku">
            <text v-if="selectedSku.stock === 0" class="stock-sold">已售罄</text>
            <text v-else-if="selectedSku.stock <= 10" class="stock-low">仅剩 {{ selectedSku.stock }} 件</text>
            <text v-else>库存 {{ selectedSku.stock }} 件</text>
          </text>
        </view>
      </view>

      <!-- 规格选择 -->
      <view v-if="product.skuList?.length" class="section-card" @click="showSkuPopup = true">
        <view class="section-row">
          <text class="section-label">规格</text>
          <text class="section-value">{{ selectedSku?.skuName || '请选择规格' }}</text>
          <IconifyIcon icon="mdi:chevron-right" :size="20" color="#999" />
        </view>
      </view>

      <!-- 数量 -->
      <view class="section-card">
        <view class="section-row quantity-row">
          <text class="section-label">数量</text>
          <view class="quantity-control">
            <view
              class="qty-btn"
              :class="{ disabled: quantity <= 1 }"
              @click="quantity > 1 && quantity--"
            >
              -
            </view>
            <input
              type="number"
              v-model.number="quantity"
              class="qty-input"
              :min="1"
              :max="selectedSku?.stock || 99"
              @blur="onQuantityBlur"
            />
            <view
              class="qty-btn"
              :class="{ disabled: quantity >= (selectedSku?.stock || 99) }"
              @click="quantity < (selectedSku?.stock || 99) && quantity++"
            >
              +
            </view>
          </view>
        </view>
      </view>

      <!-- 商品详情 -->
      <view v-if="product.detailHtml || product.description" class="detail-card">
        <text class="detail-title">商品详情</text>
        <rich-text
          v-if="product.detailHtml"
          :nodes="product.detailHtml"
          class="detail-content"
        />
        <text v-else class="detail-content">{{ product.description }}</text>
      </view>
    </view>

    <view v-else class="empty-wrap">
      <text>商品不存在</text>
    </view>

    <!-- 底部操作栏 -->
    <view v-if="product" class="bottom-bar">
      <view class="bar-left">
        <view class="bar-item" @click="goTo('/pages/shop/cart')">
          <IconifyIcon icon="mdi:cart-outline" :size="24" />
          <text class="bar-text">购物车</text>
        </view>
      </view>
      <view class="bar-right">
        <view
          class="bar-btn cart-btn"
          :class="{ disabled: !selectedSku || selectedSku.stock === 0 }"
          @click="handleAddToCart"
        >
          {{ selectedSku?.stock === 0 ? '已售罄' : '加入购物车' }}
        </view>
        <view
          class="bar-btn buy-btn"
          :class="{ disabled: !selectedSku || selectedSku.stock === 0 }"
          @click="handleBuyNow"
        >
          {{ selectedSku?.stock === 0 ? '已售罄' : '立即购买' }}
        </view>
      </view>
    </view>

    <!-- 规格弹窗 -->
    <view v-if="showSkuPopup" class="sku-mask" @click="showSkuPopup = false">
      <view class="sku-popup" @click.stop>
        <view class="popup-header">
          <image
            :src="selectedSku?.image || product?.coverImage"
            class="popup-img"
            mode="aspectFill"
          />
          <view class="popup-info">
            <text class="popup-price">¥{{ currentPrice }}</text>
            <text class="popup-stock">库存 {{ selectedSku?.stock ?? 0 }} 件</text>
          </view>
        </view>
        <view class="popup-sku">
          <text class="popup-label">规格</text>
          <view class="sku-list">
            <view
              v-for="sku in product?.skuList"
              :key="sku.id"
              class="sku-item"
              :class="{
                active: selectedSku?.id === sku.id,
                sold: sku.stock === 0
              }"
              @click="sku.stock > 0 && selectSku(sku)"
            >
              {{ sku.skuName }}
              <text v-if="sku.stock === 0" class="sold-tag">缺</text>
            </view>
          </view>
        </view>
        <view class="popup-footer">
          <view class="bar-btn buy-btn full" @click="confirmSku">确定</view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getProductById, toggleFavoriteProduct } from '@/api/product'
import { addItem } from '@/api/cart'
import { getToken } from '@/utils/authStorage'

const product = ref(null)
const loading = ref(true)
const selectedSku = ref(null)
const quantity = ref(1)
const showSkuPopup = ref(false)
const favoriteLoading = ref(false)

const imageList = computed(() => {
  const p = product.value
  if (!p) return []
  const list = p.images && p.images.length ? p.images : []
  if (p.coverImage && !list.includes(p.coverImage)) {
    return [p.coverImage, ...list]
  }
  return list.length ? list : [p.coverImage || '/static/default-product.png']
})

const currentPrice = computed(() => {
  return selectedSku.value?.price ?? product.value?.minPrice ?? '0'
})

onMounted(async () => {
  const pages = getCurrentPages()
  const current = pages[pages.length - 1]
  const id = current.options?.id
  if (!id) {
    loading.value = false
    return
  }
  try {
    const res = await getProductById(id)
    product.value = res.data
    if (product.value?.skuList?.length) {
      const first = product.value.skuList.find(s => s.stock > 0) || product.value.skuList[0]
      selectedSku.value = first
    }
  } catch (e) {
    uni.showToast({ title: '加载失败', icon: 'none' })
  }
  loading.value = false
})

function selectSku(sku) {
  selectedSku.value = sku
}

function confirmSku() {
  showSkuPopup.value = false
}

function onQuantityBlur() {
  const max = selectedSku.value?.stock ?? 99
  if (quantity.value < 1) quantity.value = 1
  if (quantity.value > max) quantity.value = max
}

function checkLogin() {
  const token = getToken()
  if (!token) {
    uni.showToast({ title: '请先登录', icon: 'none' })
    uni.navigateTo({ url: '/pages/login/index' })
    return false
  }
  return true
}

async function handleFavorite() {
  if (!checkLogin() || favoriteLoading.value) return
  favoriteLoading.value = true
  try {
    const res = await toggleFavoriteProduct(product.value.id)
    product.value.userFavorited = res.data
    uni.showToast({ title: res.data ? '收藏成功' : '已取消收藏', icon: 'none' })
  } catch (e) {
    uni.showToast({ title: '操作失败', icon: 'none' })
  }
  favoriteLoading.value = false
}

async function handleAddToCart() {
  if (!checkLogin()) return
  if (!selectedSku.value) {
    uni.showToast({ title: '请选择规格', icon: 'none' })
    showSkuPopup.value = true
    return
  }
  if (selectedSku.value.stock === 0) {
    uni.showToast({ title: '该规格已售罄', icon: 'none' })
    return
  }
  if (quantity.value > selectedSku.value.stock) {
    uni.showToast({ title: `库存不足，仅剩 ${selectedSku.value.stock} 件`, icon: 'none' })
    return
  }
  try {
    await addItem(selectedSku.value.id, quantity.value)
    uni.showToast({ title: '已加入购物车' })
  } catch (e) {}
}

function handleBuyNow() {
  if (!checkLogin()) return
  if (!selectedSku.value) {
    uni.showToast({ title: '请选择规格', icon: 'none' })
    showSkuPopup.value = true
    return
  }
  if (selectedSku.value.stock === 0) {
    uni.showToast({ title: '该规格已售罄', icon: 'none' })
    return
  }
  if (quantity.value > selectedSku.value.stock) {
    uni.showToast({ title: `库存不足，仅剩 ${selectedSku.value.stock} 件`, icon: 'none' })
    return
  }
  const item = {
    skuId: selectedSku.value.id,
    skuName: selectedSku.value.skuName || product.value.name,
    skuImage: selectedSku.value.image || product.value.coverImage,
    specs: selectedSku.value.specs,
    price: selectedSku.value.price,
    quantity: quantity.value
  }
  uni.navigateTo({
    url: `/pages/order/confirm?type=direct&item=${encodeURIComponent(JSON.stringify(item))}`
  })
}

function goTo(url) {
  uni.navigateTo({ url })
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

.banner-swiper {
  width: 100%;
  height: 375px;
  background: #fff;
}

.banner-img {
  width: 100%;
  height: 100%;
}

.info-card {
  margin: 12px;
  padding: 16px;
  background: #fff;
  border-radius: 12px;
}

.price-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.price-left {
  display: flex;
  align-items: baseline;
}

.fav-btn {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 2px;
  padding: 4px 8px;
}

.fav-text {
  font-size: 10px;
  color: #999;
}

.price-symbol {
  font-size: 14px;
  color: #ef4444;
  margin-right: 2px;
}

.price-value {
  font-size: 24px;
  font-weight: bold;
  color: #ef4444;
}

.original-price {
  margin-left: 8px;
  font-size: 12px;
  color: #999;
  text-decoration: line-through;
}

.product-name {
  display: block;
  margin-top: 8px;
  font-size: 16px;
  font-weight: 500;
  color: #333;
}

.meta-row {
  margin-top: 8px;
  font-size: 12px;
  color: #999;
}

.stock-sold {
  color: #ef4444;
}

.stock-low {
  color: #f97316;
}

.section-card {
  margin: 0 12px 12px;
  padding: 16px;
  background: #fff;
  border-radius: 12px;
}

.section-row {
  display: flex;
  align-items: center;
}

.section-label {
  width: 60px;
  font-size: 14px;
  color: #666;
}

.section-value {
  flex: 1;
  font-size: 14px;
  color: #333;
}

.quantity-row {
  align-items: center;
}

.quantity-control {
  display: flex;
  align-items: center;
}

.qty-btn {
  width: 32px;
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

.qty-input {
  width: 48px;
  height: 28px;
  margin: 0 4px;
  text-align: center;
  border: 1px solid #e5e7eb;
  border-radius: 4px;
  font-size: 14px;
}

.detail-card {
  margin: 0 12px 12px;
  padding: 16px;
  background: #fff;
  border-radius: 12px;
}

.detail-title {
  font-size: 16px;
  font-weight: 500;
  color: #333;
}

.detail-content {
  display: block;
  margin-top: 12px;
  font-size: 14px;
  color: #666;
  line-height: 1.6;
}

.bottom-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  height: 56px;
  display: flex;
  align-items: center;
  padding: 0 12px;
  background: #fff;
  box-shadow: 0 -2px 10px rgba(0,0,0,0.05);
}

.bar-left {
  display: flex;
  gap: 16px;
}

.bar-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  font-size: 10px;
  color: #666;
}

.bar-item :deep(.iconify-icon) {
  margin-bottom: 2px;
}

.bar-right {
  flex: 1;
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.bar-btn {
  padding: 0 24px;
  height: 36px;
  line-height: 36px;
  border-radius: 18px;
  font-size: 14px;
  text-align: center;

  &.cart-btn {
    background: #fef3c7;
    color: #f59e0b;
  }

  &.buy-btn {
    background: linear-gradient(135deg, #ef4444, #f97316);
    color: #fff;
  }

  &.disabled {
    background: #e5e7eb !important;
    color: #9ca3af !important;
  }

  &.full {
    flex: 1;
  }
}

.sku-mask {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0,0,0,0.5);
  z-index: 999;
  display: flex;
  align-items: flex-end;
}

.sku-popup {
  width: 100%;
  max-height: 70vh;
  padding: 16px;
  padding-bottom: calc(16px + env(safe-area-inset-bottom));
  background: #fff;
  border-radius: 16px 16px 0 0;
}

.popup-header {
  display: flex;
  gap: 12px;
  padding-bottom: 16px;
  border-bottom: 1px solid #f0f0f0;
}

.popup-img {
  width: 80px;
  height: 80px;
  border-radius: 8px;
  background: #f5f5f5;
}

.popup-info {
  flex: 1;
}

.popup-price {
  font-size: 20px;
  font-weight: bold;
  color: #ef4444;
}

.popup-stock {
  display: block;
  margin-top: 4px;
  font-size: 12px;
  color: #999;
}

.popup-sku {
  margin-top: 16px;
}

.popup-label {
  font-size: 14px;
  color: #666;
}

.sku-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 8px;
}

.sku-item {
  padding: 8px 16px;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  font-size: 14px;
  color: #333;

  &.active {
    border-color: #ef4444;
    color: #ef4444;
  }

  &.sold {
    opacity: 0.5;
    text-decoration: line-through;
  }
}

.sold-tag {
  margin-left: 4px;
  padding: 1px 4px;
  background: #ef4444;
  color: #fff;
  font-size: 10px;
  border-radius: 2px;
}

.popup-footer {
  margin-top: 20px;
}
</style>
