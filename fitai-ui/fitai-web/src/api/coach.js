import request from '@/utils/request'

/**
 * 获取教练列表（分页）
 * @param {Object} params { specialty, keyword, pageNum, pageSize }
 */
export function getCoachList(params) {
  return request.get('/coach/list', { params })
}

/**
 * 获取教练详情
 */
export function getCoachById(id) {
  return request.get(`/coach/${id}`)
}

/**
 * 获取教练某天的可预约时段
 * @param {Number|String} id 教练ID
 * @param {String} date yyyy-MM-dd
 */
export function getCoachSlots(id, date) {
  return request.get(`/coach/${id}/slots`, { params: { date } })
}

/**
 * 获取我绑定的教练列表
 */
export function getMyCoaches() {
  return request.get('/coach/my')
}

/**
 * 提交预约
 * @param {Object} data { coachId, bookingDate, timeSlot, remark }
 */
export function createBooking(data) {
  return request.post('/booking', data)
}

/**
 * 获取我的预约（分页）
 * @param {Object} params { status, pageNum, pageSize }
 */
export function getMyBookings(params) {
  return request.get('/booking/my', { params })
}

/**
 * 取消我的预约
 */
export function cancelBooking(id) {
  return request.post(`/booking/${id}/cancel`)
}
