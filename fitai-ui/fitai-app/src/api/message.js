import http from '@/utils/request'

/** 消息列表（分页） */
export function getMessageList(params) {
  return http.get('/message/list', params)
}

/** 未读数量 */
export function getMessageUnreadCount() {
  return http.get('/message/unread-count')
}

/** 标记已读 */
export function markMessageRead(id) {
  return http.post(`/message/read/${id}`, {})
}

/** 全部已读 */
export function markAllMessagesRead() {
  return http.post('/message/read-all', {})
}

/** 删除消息 */
export function deleteMessage(id) {
  return http.delete(`/message/${id}`)
}
