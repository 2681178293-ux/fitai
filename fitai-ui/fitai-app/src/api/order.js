import http from '@/utils/request'

function newIdempotencyKey() {
  const c = typeof crypto !== 'undefined' ? crypto : null
  if (c && typeof c.randomUUID === 'function') {
    return c.randomUUID()
  }
  return `${Date.now()}-${Math.random().toString(36).slice(2, 12)}`
}

/** 创建订单 */
export function createOrder(data) {
  return http.post('/order', data, {
    header: { 'Idempotency-Key': newIdempotencyKey() }
  })
}

/** 获取订单详情 */
export function getOrderById(id) {
  return http.get(`/order/${id}`)
}

/** 获取订单列表 */
export function getOrderList(params) {
  return http.get('/order/list', params)
}

/** 支付订单 */
export function payOrder(id, payType) {
  return http.post(`/order/${id}/pay`, {}, { params: { payType } })
}

/** 创建支付订单 */
export function createPayOrder(data) {
  return http.post('/pay/create', data)
}

/** 合并支付 */
export function mergePayOrder(data) {
  return http.post('/pay/merge', data)
}

/** 查询支付状态 */
export function queryPayStatus(transactionNo) {
  return http.get(`/pay/status/${transactionNo}`)
}

/** 取消订单 */
export function cancelOrder(id) {
  return http.post(`/order/${id}/cancel`)
}

/** 确认收货 */
export function confirmReceive(id) {
  return http.post(`/order/${id}/confirm`)
}

/** 订单物流 */
export function getOrderLogistics(id) {
  return http.get(`/order/${id}/logistics`)
}
