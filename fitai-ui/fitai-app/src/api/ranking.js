import http from '@/utils/request'

/** 训练排行 period: week|month */
export function getTrainingRanking(params) {
  return http.get('/ranking/training', params)
}

/** 签到排行 period: week|month */
export function getCheckinRanking(params) {
  return http.get('/ranking/checkin', params)
}
