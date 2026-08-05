import http from '@/utils/request'

/**
 * 获取用户信息
 */
export function getUserInfo() {
  return http.get('/user/info')
}

/**
 * 更新用户信息
 */
export function updateUserInfo(data) {
  return http.put('/user', data)
}

/**
 * 获取身体数据
 */
export function getBodyRecords(params) {
  return http.get('/user/body-records', params)
}

/**
 * 添加身体数据
 */
export function addBodyRecord(data) {
  return http.post('/user/body-records', data)
}
