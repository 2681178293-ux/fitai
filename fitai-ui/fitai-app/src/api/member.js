import http from '@/utils/request'

/** 获取会员套餐列表 */
export function getMemberPlans() {
  return http.get('/member/plans')
}

/** 获取会员状态 */
export function getMemberStatus() {
  return http.get('/member/status')
}

/** 订阅会员 */
export function subscribeMember(planId) {
  return http.post(`/member/subscribe/${planId}`)
}

/** 获取会员订单列表 */
export function getMemberOrders() {
  return http.get('/member/orders')
}
