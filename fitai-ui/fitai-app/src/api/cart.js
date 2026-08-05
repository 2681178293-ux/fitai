import http from '@/utils/request'

/** 获取购物车 */
export function getCart() {
  return http.get('/cart')
}

/** 添加商品到购物车 */
export function addItem(skuId, quantity) {
  return http.post('/cart/add', {}, { params: { skuId, quantity } })
}

/** 修改商品数量 */
export function updateQuantity(id, quantity) {
  return http.put(`/cart/${id}/quantity`, {}, { params: { quantity } })
}

/** 删除购物车商品 */
export function deleteItems(ids) {
  return http.delete('/cart', { params: { ids: Array.isArray(ids) ? ids.join(',') : ids } })
}

/** 选中/取消选中 */
export function updateSelected(id, selected) {
  return http.put(`/cart/${id}/selected`, {}, { params: { selected } })
}

/** 全选/取消全选 */
export function selectAll(selected) {
  return http.put('/cart/selectAll', {}, { params: { selected } })
}

/** 清空购物车 */
export function clear() {
  return http.delete('/cart/clear')
}
