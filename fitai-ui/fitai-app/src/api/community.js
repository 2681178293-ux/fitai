import http from '@/utils/request'

/** 发布动态 */
export function createPost(data) {
  return http.post('/community/post', data)
}

/** 获取动态列表 */
export function getFeed(params) {
  return http.get('/community/feed', params)
}

/** 获取动态详情 */
export function getPostDetail(id) {
  return http.get(`/community/post/${id}`)
}

/** 点赞/取消点赞 */
export function toggleLike(postId) {
  return http.post(`/community/post/${postId}/like`)
}

/** 删除动态 */
export function deletePost(id) {
  return http.delete(`/community/post/${id}`)
}

/** 训练打卡 */
export function checkin(trainingPlanId, content) {
  return http.post('/community/checkin', {}, { params: { trainingPlanId, content } })
}

/** 获取评论列表 */
export function getComments(postId) {
  return http.get(`/community/post/${postId}/comments`)
}

/** 发表评论 */
export function createComment(postId, data) {
  return http.post(`/community/post/${postId}/comment`, data)
}

/** 删除评论 */
export function deleteComment(commentId) {
  return http.delete(`/community/comment/${commentId}`)
}

/** 评论点赞/取消点赞 */
export function toggleCommentLike(commentId) {
  return http.post(`/community/comment/${commentId}/like`)
}
