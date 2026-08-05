import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import * as cartApi from '@/api/cart'

export const useCartStore = defineStore('cart', () => {
  // 状态
  const items = ref([])
  const loading = ref(false)

  // 计算属性
  const totalCount = computed(() => items.value.reduce((sum, item) => sum + item.quantity, 0))
  const selectedCount = computed(() => 
    items.value.filter(item => item.selected).reduce((sum, item) => sum + item.quantity, 0)
  )
  const selectedAmount = computed(() => 
    items.value.filter(item => item.selected && item.valid)
      .reduce((sum, item) => sum + item.price * item.quantity, 0)
  )
  const allSelected = computed(() => items.value.length > 0 && items.value.every(item => item.selected))
  const selectedItems = computed(() => items.value.filter(item => item.selected && item.valid !== false))

  // 获取购物车
  async function fetchCart() {
    loading.value = true
    try {
      const res = await cartApi.getCart()
      items.value = res.items || []
    } finally {
      loading.value = false
    }
  }

  // 添加商品
  async function addItem(skuId, quantity = 1) {
    await cartApi.addItem(skuId, quantity)
    await fetchCart()
  }

  // 修改数量
  async function updateQuantity(itemId, quantity) {
    await cartApi.updateQuantity(itemId, quantity)
    const item = items.value.find(i => i.id === itemId)
    if (item) {
      item.quantity = quantity
      item.subtotal = item.price * quantity
    }
  }

  // 删除商品
  async function deleteItems(itemIds) {
    await cartApi.deleteItems(itemIds)
    items.value = items.value.filter(item => !itemIds.includes(item.id))
  }

  // 选中/取消选中
  async function updateSelected(itemId, selected) {
    await cartApi.updateSelected(itemId, selected)
    const item = items.value.find(i => i.id === itemId)
    if (item) {
      item.selected = selected
    }
  }

  // 全选/取消全选
  async function selectAll(selected) {
    await cartApi.selectAll(selected)
    items.value.forEach(item => {
      item.selected = selected
    })
  }

  // 清空购物车
  async function clear() {
    await cartApi.clear()
    items.value = []
  }

  // 清空已选商品（下单后调用）
  async function clearSelected() {
    const selectedIds = selectedItems.value.map(item => item.id)
    if (selectedIds.length > 0) {
      await deleteItems(selectedIds)
    }
  }

  return {
    items,
    loading,
    totalCount,
    selectedCount,
    selectedAmount,
    allSelected,
    selectedItems,
    fetchCart,
    addItem,
    updateQuantity,
    deleteItems,
    updateSelected,
    selectAll,
    clear,
    clearSelected
  }
})
