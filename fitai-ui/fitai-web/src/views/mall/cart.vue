<script setup>
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useCartStore } from '@/stores/cart'

const router = useRouter()
const cartStore = useCartStore()

onMounted(() => {
  cartStore.fetchCart()
})

async function handleQuantityChange(item, quantity) {
  await cartStore.updateQuantity(item.id, quantity)
}

async function handleSelectedChange(item, selected) {
  await cartStore.updateSelected(item.id, selected)
}

async function handleSelectAll(selected) {
  await cartStore.selectAll(selected)
}

async function handleDelete(ids) {
  try {
    await ElMessageBox.confirm('确定要删除选中的商品吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await cartStore.deleteItems(ids)
    ElMessage.success('删除成功')
  } catch (e) {}
}

function handleCheckout() {
  const selectedItems = cartStore.items.filter(item => item.selected && item.valid)
  if (!selectedItems.length) {
    ElMessage.warning('请选择要结算的商品')
    return
  }
  router.push({ name: 'OrderConfirm', query: { type: 'cart' } })
}

function goToMall() {
  router.push('/mall')
}
</script>

<template>
  <div class="max-w-4xl mx-auto px-4 py-6 pb-24 md:pb-6">
    <h1 class="text-2xl font-bold mb-6">购物车</h1>
    
    <div v-if="cartStore.items.length === 0" class="bg-white rounded-xl p-16 text-center">
      <div class="text-4xl mb-4">🛒</div>
      <p class="text-gray-500 mb-4">购物车还是空的</p>
      <el-button type="primary" @click="goToMall">去逛逛</el-button>
    </div>
    
    <div v-else>
      <!-- 购物车列表 -->
      <div class="bg-white rounded-xl overflow-hidden">
        <!-- 全选 -->
        <div class="flex items-center gap-4 px-4 py-3 border-b">
          <el-checkbox
            :model-value="cartStore.allSelected"
            @change="handleSelectAll"
          >
            全选
          </el-checkbox>
        </div>
        
        <!-- 商品列表 -->
        <div
          v-for="item in cartStore.items"
          :key="item.id"
          class="flex items-center gap-4 p-4 border-b last:border-0"
          :class="{ 'opacity-50': !item.valid }"
        >
          <el-checkbox
            :model-value="item.selected"
            :disabled="!item.valid"
            @change="(val) => handleSelectedChange(item, val)"
          />
          <img
            :src="item.image"
            :alt="item.productName"
            class="w-20 h-20 object-cover rounded"
          />
          <div class="flex-1 min-w-0">
            <h3 class="font-medium line-clamp-1">{{ item.productName }}</h3>
            <p class="text-sm text-gray-500">{{ item.skuName }}</p>
            <div class="flex items-center justify-between mt-2">
              <span class="text-red-500 font-medium">¥{{ item.price }}</span>
              <el-input-number
                :model-value="item.quantity"
                :min="1"
                :max="item.stock"
                size="small"
                @change="(val) => handleQuantityChange(item, val)"
              />
            </div>
          </div>
          <el-button text type="danger" @click="handleDelete([item.id])">删除</el-button>
        </div>
      </div>
      
      <!-- 底部结算栏 -->
      <div class="mt-4 bg-white rounded-xl px-6 py-4 flex items-center justify-between">
        <div>
          <span class="text-gray-500">已选 {{ cartStore.selectedCount }} 件</span>
          <span class="ml-4">合计：</span>
          <span class="text-red-500 text-xl font-bold">¥{{ cartStore.selectedAmount.toFixed(2) }}</span>
        </div>
        <el-button type="primary" size="large" :disabled="!cartStore.selectedCount" @click="handleCheckout">
          结算
        </el-button>
      </div>
    </div>
  </div>
</template>
