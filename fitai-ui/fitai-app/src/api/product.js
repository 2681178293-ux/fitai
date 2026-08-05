import http from '@/utils/request'

/** 获取商品列表 */
export function getProductPage(params) {
  return http.get('/product/page', params)
}

/** 获取商品详情 */
export function getProductById(id) {
  return http.get(`/product/${id}`)
}

/** 获取商品分类 */
export function getCategoryTree() {
  return http.get('/product/category/tree')
}

/** 搜索商品 */
export function searchProduct(keyword, params = {}) {
  return http.get('/product/search', { keyword, ...params })
}

/** 获取我的收藏（商品列表） */
export function getFavoriteProducts(params) {
  return http.get('/product/favorites', params)
}

/** 收藏/取消收藏商品 */
export function toggleFavoriteProduct(productId) {
  return http.post(`/product/${productId}/favorite`)
}
