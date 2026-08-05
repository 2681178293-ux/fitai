import request from '@/utils/request'

/**
 * 获取进行中的挑战赛列表
 */
export function getActiveChallenges() {
  return request({
    url: '/challenge/list',
    method: 'get'
  })
}

/**
 * 获取挑战赛详情
 */
export function getChallengeDetail(id) {
  return request({
    url: `/challenge/${id}`,
    method: 'get'
  })
}

/**
 * 参加挑战赛
 */
export function joinChallenge(id) {
  return request({
    url: `/challenge/${id}/join`,
    method: 'post'
  })
}

/**
 * 挑战打卡
 */
export function checkinChallenge(id, value) {
  return request({
    url: `/challenge/${id}/checkin`,
    method: 'post',
    params: { value }
  })
}

/**
 * 获取我参与的挑战
 */
export function getMyChallenges() {
  return request({
    url: '/challenge/my',
    method: 'get'
  })
}

/**
 * 获取挑战赛排行榜
 */
export function getChallengeRanking(id, limit = 50) {
  return request({
    url: `/challenge/${id}/ranking`,
    method: 'get',
    params: { limit }
  })
}
