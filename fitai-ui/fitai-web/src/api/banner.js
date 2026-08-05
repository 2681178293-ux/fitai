import request from '@/utils/request'

/**
 * 获取轮播图列表
 * @param {Object} params - { position: 'home' | 'course' | 'product' }
 */
export function getBannerList(params = {}) {
  return request.get('/banner/list', { params })
}
