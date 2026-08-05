import request from '@/utils/request'

/**
 * 获取课程列表
 */
export function getCourseList(params) {
  return request.get('/course/list', { params })
}

/**
 * 分页查询课程
 */
export function getCoursePage(params) {
  return request.get('/course/page', { params })
}

/**
 * 获取课程详情
 */
export function getCourseById(id) {
  return request.get(`/course/${id}`)
}

/**
 * 获取课程分类
 */
export function getCategoryTree() {
  return request.get('/course/category/tree')
}

/**
 * 更新学习进度
 */
export function updateProgress(courseId, progressSeconds) {
  return request.post(`/course/${courseId}/progress`, null, { params: { progressSeconds } })
}

/**
 * 完成课程
 */
export function completeCourse(courseId) {
  return request.post(`/course/${courseId}/complete`)
}

/**
 * 获取学习历史
 */
export function getUserHistory(params) {
  return request.get('/course/history', { params })
}

/**
 * 获取我的收藏（课程列表）
 */
export function getFavoriteCourses(params) {
  return request.get('/course/favorites', { params })
}

/**
 * 点赞/取消点赞课程
 * @returns {Promise<boolean>} true-已点赞, false-已取消
 */
export function toggleLikeCourse(courseId) {
  return request.post(`/course/${courseId}/like`)
}

/**
 * 收藏/取消收藏课程
 * @returns {Promise<boolean>} true-已收藏, false-已取消
 */
export function toggleFavoriteCourse(courseId) {
  return request.post(`/course/${courseId}/favorite`)
}

/**
 * 获取点赞状态
 */
export function getLikeStatus(courseId) {
  return request.get(`/course/${courseId}/like/status`)
}

/**
 * 获取收藏状态
 */
export function getFavoriteStatus(courseId) {
  return request.get(`/course/${courseId}/favorite/status`)
}

/**
 * 记录播放（登录用户）
 */
export function recordPlay(courseId) {
  return request.post(`/course/${courseId}/play`)
}

/**
 * 记录观看（非登录用户）
 */
export function recordView(courseId) {
  return request.post(`/course/${courseId}/view`)
}
