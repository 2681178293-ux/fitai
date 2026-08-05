import http from '@/utils/request'

/** 获取进行中的秒杀活动列表 */
export function getActiveActivities() {
  return http.get('/seckill/activities')
}

/** 获取秒杀活动详情 */
export function getActivityDetail(id) {
  return http.get(`/seckill/activity/${id}`)
}

/** 执行秒杀 */
export function doSeckill(data) {
  return http.post('/seckill/rush', data)
}

/** 获取秒杀订单详情 */
export function getSeckillOrderDetail(orderNo) {
  return http.get(`/seckill/order/${orderNo}`)
}

/** 获取我的秒杀订单列表 */
export function getMySeckillOrders() {
  return http.get('/seckill/orders')
}

/** 支付秒杀订单 */
export function paySeckillOrder(orderNo, returnUrl) {
  return http.post(`/seckill/order/${orderNo}/pay`, {}, returnUrl ? { params: { returnUrl } } : {})
}

/** 取消秒杀订单 */
export function cancelSeckillOrder(orderNo) {
  return http.post(`/seckill/order/${orderNo}/cancel`)
}
