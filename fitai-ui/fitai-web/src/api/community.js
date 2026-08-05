import request from '@/utils/request'

/**
 * 发布动态
 */
export function createPost(data) {
  return request({
    url: '/community/post',
    method: 'post',
    data
  })
}

/**
 * 获取动态列表
 */
export function getFeed(params) {
  return request({
    url: '/community/feed',
    method: 'get',
    params
  })
}

/**
 * 获取动态详情
 */
export function getPostDetail(id) {
  return request({
    url: `/community/post/${id}`,
    method: 'get'
  })
}

/**
 * 点赞/取消点赞
 */
export function toggleLike(postId) {
  return request({
    url: `/community/post/${postId}/like`,
    method: 'post'
  })
}

/**
 * 删除动态
 */
export function deletePost(id) {
  return request({
    url: `/community/post/${id}`,
    method: 'delete'
  })
}

/**
 * 训练打卡
 */
export function checkin(trainingPlanId, content) {
  return request({
    url: '/community/checkin',
    method: 'post',
    params: { trainingPlanId, content }
  })
}

// ==================== 评论接口 ====================

/**
 * 获取评论列表
 */
export function getComments(postId) {
  return request({
    url: `/community/post/${postId}/comments`,
    method: 'get'
  })
}

/**
 * 发表评论
 */
export function createComment(postId, data) {
  return request({
    url: `/community/post/${postId}/comment`,
    method: 'post',
    data
  })
}

/**
 * 删除评论
 */
export function deleteComment(commentId) {
  return request({
    url: `/community/comment/${commentId}`,
    method: 'delete'
  })
}

/**
 * 评论点赞/取消点赞
 */
export function toggleCommentLike(commentId) {
  return request({
    url: `/community/comment/${commentId}/like`,
    method: 'post'
  })
}
