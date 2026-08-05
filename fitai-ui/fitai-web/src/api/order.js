import request from '@/utils/request'

function newIdempotencyKey() {
  if (typeof crypto !== 'undefined' && typeof crypto.randomUUID === 'function') {
    return crypto.randomUUID()
  }
  return `${Date.now()}-${Math.random().toString(36).slice(2, 12)}`
}

/**
 * 创建订单
 */
export function createOrder(data) {
  return request.post('/order', data, {
    headers: { 'Idempotency-Key': newIdempotencyKey() }
  })
}

/**
 * 获取订单详情
 */
export function getOrderById(id) {
  return request.get(`/order/${id}`)
}

/**
 * 获取订单列表
 */
export function getOrderList(params) {
  return request.get('/order/list', { params })
}

/**
 * 支付订单（旧接口，仅更新状态）
 */
export function payOrder(id, payType) {
  return request.post(`/order/${id}/pay`, null, { params: { payType } })
}

/**
 * 创建支付订单（调用支付宝）
 * @param {Object} data - 支付参数
 * @param {string} data.orderNo - 订单编号
 * @param {string} data.orderType - 订单类型 product/member/seckill
 * @param {number} data.amount - 支付金额
 * @param {string} data.payType - 支付方式 alipay/wxpay
 * @param {string} data.subject - 订单标题
 */
export function createPayOrder(data) {
  return request.post('/pay/create', data)
}

/**
 * 合并支付多个订单
 * @param {Object} data - 支付参数
 * @param {string[]} data.orderNos - 订单编号列表
 * @param {string} data.orderType - 订单类型
 * @param {number} data.totalAmount - 总金额
 * @param {string} data.payType - 支付方式
 * @param {string} data.subject - 订单标题
 */
export function mergePayOrder(data) {
  return request.post('/pay/merge', data)
}

/**
 * 查询支付状态
 */
export function queryPayStatus(transactionNo) {
  return request.get(`/pay/status/${transactionNo}`)
}

/**
 * 取消订单
 */
export function cancelOrder(id) {
  return request.post(`/order/${id}/cancel`)
}

/**
 * 确认收货
 */
export function confirmReceive(id) {
  return request.post(`/order/${id}/confirm`)
}

/**
 * 查询订单物流轨迹
 */
export function getOrderLogistics(id) {
  return request.get(`/order/${id}/logistics`)
}
