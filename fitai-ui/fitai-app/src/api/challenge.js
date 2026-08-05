import http from '@/utils/request'

/** 获取进行中的挑战赛列表 */
export function getActiveChallenges() {
  return http.get('/challenge/list')
}

/** 获取挑战赛详情 */
export function getChallengeDetail(id) {
  return http.get(`/challenge/${id}`)
}

/** 参加挑战赛 */
export function joinChallenge(id) {
  return http.post(`/challenge/${id}/join`)
}

/** 挑战打卡 */
export function checkinChallenge(id, value) {
  return http.post(`/challenge/${id}/checkin`, {}, { params: { value } })
}

/** 获取我参与的挑战 */
export function getMyChallenges() {
  return http.get('/challenge/my')
}

/** 获取挑战赛排行榜 */
export function getChallengeRanking(id, limit = 50) {
  return http.get(`/challenge/${id}/ranking`, { limit })
}
