import request from '@/utils/request'

/**
 * 获取购物车
 */
export function getCart() {
  return request.get('/cart')
}

/**
 * 添加商品到购物车
 */
export function addItem(skuId, quantity) {
  return request.post('/cart/add', null, { params: { skuId, quantity } })
}

/**
 * 修改商品数量
 */
export function updateQuantity(id, quantity) {
  return request.put(`/cart/${id}/quantity`, null, { params: { quantity } })
}

/**
 * 删除商品
 */
export function deleteItems(ids) {
  return request.delete('/cart', { params: { ids: ids.join(',') } })
}

/**
 * 选中/取消选中
 */
export function updateSelected(id, selected) {
  return request.put(`/cart/${id}/selected`, null, { params: { selected } })
}

/**
 * 全选/取消全选
 */
export function selectAll(selected) {
  return request.put('/cart/selectAll', null, { params: { selected } })
}

/**
 * 清空购物车
 */
export function clear() {
  return request.delete('/cart/clear')
}
