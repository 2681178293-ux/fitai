import http from '@/utils/request'

/** 收货地址列表 */
export function listAddress() {
  return http.get('/user/address/list')
}

/** 新增地址 */
export function addAddress(data) {
  return http.post('/user/address', data)
}

/** 修改地址 */
export function updateAddress(id, data) {
  return http.put(`/user/address/${id}`, data)
}

/** 删除地址 */
export function deleteAddress(id) {
  return http.delete(`/user/address/${id}`)
}

/** 设为默认 */
export function setAddressDefault(id) {
  return http.put(`/user/address/${id}/default`)
}
