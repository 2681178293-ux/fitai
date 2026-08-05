import http from '@/utils/request'

/**
 * 获取课程分类
 */
export function getCourseCategories() {
  return http.get('/course/category/tree')
}

/**
 * 获取课程列表
 */
export function getCourseList(params) {
  return http.get('/course/page', params)
}

/**
 * 获取课程详情
 */
export function getCourseDetail(id) {
  return http.get(`/course/${id}`)
}

/**
 * 更新学习进度（秒）
 */
export function updateProgress(courseId, seconds) {
  return http.post(`/course/${courseId}/progress`, {}, { params: { progressSeconds: seconds } })
}

/**
 * 记录开始播放（创建学习记录、首次计观看）
 */
export function recordCoursePlay(courseId) {
  return http.post(`/course/${courseId}/play`, {})
}

/**
 * 完成课程
 */
export function completeCourse(courseId) {
  return http.post(`/course/${courseId}/complete`)
}

/**
 * 获取学习历史
 */
export function getLearningHistory(params) {
  return http.get('/course/history', params)
}

/**
 * 获取我的收藏（已点赞课程）
 */
export function getFavoriteCourses(params) {
  return http.get('/course/favorites', params)
}

/**
 * 点赞/取消点赞课程
 */
export function toggleLike(courseId) {
  return http.post(`/course/${courseId}/like`)
}

/**
 * 收藏/取消收藏课程
 */
export function toggleFavorite(courseId) {
  return http.post(`/course/${courseId}/favorite`)
}
