import request from '@/utils/request'

/**
 * 获取商品列表
 */
export function getProductPage(params) {
  return request.get('/product/page', { params })
}

/**
 * 获取商品详情
 */
export function getProductById(id) {
  return request.get(`/product/${id}`)
}

/**
 * 获取商品分类
 */
export function getCategoryTree() {
  return request.get('/product/category/tree')
}

/**
 * 搜索商品
 */
export function searchProduct(keyword, params) {
  return request.get('/product/search', { params: { keyword, ...params } })
}

/**
 * 获取我的收藏（商品列表）
 */
export function getFavoriteProducts(params) {
  return request.get('/product/favorites', { params })
}

/**
 * 收藏/取消收藏商品
 * @returns {Promise<boolean>} true-已收藏, false-已取消
 */
export function toggleFavoriteProduct(productId) {
  return request.post(`/product/${productId}/favorite`)
}
