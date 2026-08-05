import http from '@/utils/request'

/** 积分余额 */
export function getPointsBalance() {
  return http.get('/points/balance')
}

/** 积分记录分页 */
export function getPointsLogs(params) {
  return http.get('/points/logs', params)
}

/** 我的优惠券 */
export function getMyCoupons(params) {
  return http.get('/coupon/my', params)
}

/** 可领取的优惠券 */
export function getAvailableCoupons() {
  return http.get('/coupon/available')
}

/** 领取优惠券 */
export function receiveCoupon(couponId) {
  return http.post(`/coupon/receive/${couponId}`, {})
}
