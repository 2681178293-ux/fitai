import request from '@/utils/request'

/**
 * 获取进行中的秒杀活动列表
 */
export function getActiveActivities() {
  return request({
    url: '/seckill/activities',
    method: 'get'
  })
}

/**
 * 获取秒杀活动详情
 */
export function getActivityDetail(id) {
  return request({
    url: `/seckill/activity/${id}`,
    method: 'get'
  })
}

/**
 * 执行秒杀
 */
export function doSeckill(data) {
  return request({
    url: '/seckill/rush',
    method: 'post',
    data
  })
}

/**
 * 获取秒杀订单详情
 */
export function getSeckillOrderDetail(orderNo) {
  return request({
    url: `/seckill/order/${orderNo}`,
    method: 'get'
  })
}

/**
 * 获取我的秒杀订单列表
 */
export function getMySeckillOrders() {
  return request({
    url: '/seckill/orders',
    method: 'get'
  })
}

/**
 * 支付秒杀订单
 * @param {string} orderNo
 * @param {string} [returnUrl] 支付宝同步跳转落地页基址，与商城订单一致传 PC 端地址
 */
export function paySeckillOrder(orderNo, returnUrl) {
  return request({
    url: `/seckill/order/${orderNo}/pay`,
    method: 'post',
    params: returnUrl ? { returnUrl } : undefined
  })
}

/**
 * 取消秒杀订单
 */
export function cancelSeckillOrder(orderNo) {
  return request({
    url: `/seckill/order/${orderNo}/cancel`,
    method: 'post'
  })
}
