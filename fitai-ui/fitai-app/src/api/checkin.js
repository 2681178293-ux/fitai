import http from '@/utils/request'

/** 获取今日签到状态 */
export function getCheckinStatus() {
  return http.get('/checkin/status')
}

/** 签到 */
export function doCheckin() {
  return http.post('/checkin/do')
}

/** 获取签到日历 */
export function getCheckinCalendar(params) {
  return http.get('/checkin/calendar', params)
}
