<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getProductById, toggleFavoriteProduct } from '@/api/product'
import { useCartStore } from '@/stores/cart'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const cartStore = useCartStore()
const userStore = useUserStore()

const product = ref(null)
const loading = ref(true)
const selectedSku = ref(null)
const quantity = ref(1)
const favoriteLoading = ref(false)

onMounted(async () => {
  const id = route.params.id
  try {
    product.value = await getProductById(id)
    if (product.value.skuList?.length) {
      selectedSku.value = product.value.skuList[0]
    }
  } catch (e) {
    router.push('/mall')
  } finally {
    loading.value = false
  }
})

async function handleFavorite() {
  if (!userStore.isLoggedIn) {
    router.push({ name: 'Login', query: { redirect: route.fullPath } })
    return
  }
  if (favoriteLoading.value) return
  favoriteLoading.value = true
  try {
    const favorited = await toggleFavoriteProduct(product.value.id)
    product.value.userFavorited = favorited
    ElMessage.success(favorited ? '收藏成功' : '已取消收藏')
  } catch (e) {
    ElMessage.error('操作失败')
  } finally {
    favoriteLoading.value = false
  }
}

const currentPrice = computed(() => selectedSku.value?.price || product.value?.minPrice)

function selectSku(sku) {
  selectedSku.value = sku
}

async function handleAddToCart() {
  if (!userStore.isLoggedIn) {
    router.push({ name: 'Login', query: { redirect: route.fullPath } })
    return
  }
  if (!selectedSku.value) {
    ElMessage.warning('请选择规格')
    return
  }
  if (selectedSku.value.stock === 0) {
    ElMessage.error('该规格已售罄')
    return
  }
  if (quantity.value > selectedSku.value.stock) {
    ElMessage.warning(`库存不足，当前库存 ${selectedSku.value.stock} 件`)
    return
  }
  await cartStore.addItem(selectedSku.value.id, quantity.value)
  ElMessage.success('已加入购物车')
}

function handleBuyNow() {
  if (!userStore.isLoggedIn) {
    router.push({ name: 'Login', query: { redirect: route.fullPath } })
    return
  }
  if (!selectedSku.value) {
    ElMessage.warning('请选择规格')
    return
  }
  if (selectedSku.value.stock === 0) {
    ElMessage.error('该规格已售罄')
    return
  }
  if (quantity.value > selectedSku.value.stock) {
    ElMessage.warning(`库存不足，当前库存 ${selectedSku.value.stock} 件`)
    return
  }
  // 构建商品信息
  const item = {
    skuId: selectedSku.value.id,
    skuName: selectedSku.value.skuName || product.value.name,
    skuImage: selectedSku.value.image || product.value.coverImage,
    specs: selectedSku.value.specs,
    price: selectedSku.value.price,
    quantity: quantity.value
  }
  // 跳转到确认订单页
  router.push({
    name: 'OrderConfirm',
    query: { 
      type: 'direct',
      item: encodeURIComponent(JSON.stringify(item))
    }
  })
}
</script>

<template>
  <div class="max-w-4xl mx-auto px-4 py-6 pb-24 md:pb-6">
    <div v-if="loading" class="text-center py-16 text-gray-500">
      加载中...
    </div>
    
    <div v-else-if="product">
      <!-- 商品图片 -->
      <div class="aspect-square bg-gray-200 rounded-xl overflow-hidden mb-6">
        <img
          :src="selectedSku?.image || product.coverImage"
          :alt="product.name"
          class="w-full h-full object-cover"
        />
      </div>
      
      <!-- 商品信息 -->
      <div class="bg-white rounded-xl p-6">
        <div class="flex items-start justify-between gap-4">
          <div>
            <div class="text-red-500 text-2xl font-bold">¥{{ currentPrice }}</div>
            <h1 class="text-xl font-medium mt-2">{{ product.name }}</h1>
            <div class="flex items-center gap-4 text-sm text-gray-500 mt-2">
              <span>已售 {{ product.salesCount }} 件</span>
              <span v-if="selectedSku" class="flex items-center gap-1">
            <template v-if="selectedSku.stock === 0">
              <span class="text-red-500 font-medium">已售罄</span>
            </template>
            <template v-else-if="selectedSku.stock <= 10">
              <span class="text-orange-500">库存紧张</span>
              <span class="text-orange-500 font-medium">仅剩 {{ selectedSku.stock }} 件</span>
            </template>
            <template v-else>
              <span>库存 {{ selectedSku.stock }} 件</span>
            </template>
              </span>
            </div>
          </div>
          <el-button
            circle
            :type="product.userFavorited ? 'warning' : 'default'"
            :loading="favoriteLoading"
            @click="handleFavorite"
            :title="product.userFavorited ? '已收藏' : '收藏'"
          >
            {{ product.userFavorited ? '⭐' : '☆' }}
          </el-button>
        </div>
        
        <!-- SKU 选择 -->
        <div v-if="product.skuList?.length" class="mt-6">
          <div class="text-sm text-gray-600 mb-2">规格</div>
          <div class="flex flex-wrap gap-2">
            <button
              v-for="sku in product.skuList"
              :key="sku.id"
              @click="selectSku(sku)"
              class="px-4 py-2 border rounded-lg text-sm relative"
              :class="[
                selectedSku?.id === sku.id 
                  ? 'border-emerald-500 text-emerald-500 bg-emerald-50' 
                  : 'border-gray-300 hover:border-emerald-300',
                sku.stock === 0 ? 'opacity-50 line-through' : ''
              ]"
            >
              {{ sku.skuName }}
              <span v-if="sku.stock === 0" class="absolute -top-1 -right-1 bg-red-500 text-white text-xs px-1 rounded">缺</span>
            </button>
          </div>
        </div>
        
        <!-- 数量 -->
        <div class="mt-6">
          <div class="text-sm text-gray-600 mb-2">数量</div>
          <el-input-number v-model="quantity" :min="1" :max="selectedSku?.stock || 99" />
        </div>
        
        <!-- 操作按钮 -->
        <div class="flex gap-4 mt-8">
          <el-button 
            size="large" 
            @click="handleAddToCart"
            :disabled="!selectedSku || selectedSku.stock === 0"
          >
            {{ selectedSku?.stock === 0 ? '已售罄' : '加入购物车' }}
          </el-button>
          <el-button 
            type="primary" 
            size="large" 
            @click="handleBuyNow"
            :disabled="!selectedSku || selectedSku.stock === 0"
          >
            {{ selectedSku?.stock === 0 ? '已售罄' : '立即购买' }}
          </el-button>
        </div>
      </div>
      
      <!-- 商品详情 -->
      <div class="bg-white rounded-xl p-6 mt-4">
        <h2 class="text-lg font-medium mb-4">商品详情</h2>
        <div v-html="product.detailHtml" class="prose max-w-none"></div>
      </div>
    </div>
  </div>
</template>
