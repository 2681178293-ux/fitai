import http from '@/utils/request'

/** 我的徽章（含全部定义与解锁状态） */
export function getMyBadges() {
  return http.get('/badge/my')
}

/** 设置展示 */
export function setBadgeDisplay(badgeId, display) {
  return http.post(`/badge/${badgeId}/display`, {}, { params: { display } })
}
