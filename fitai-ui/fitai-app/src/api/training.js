import http from '@/utils/request'

/** 获取动作列表 */
export function getExerciseList(params) {
  return http.get('/exercise/list', params)
}

/** 分页查询动作 */
export function getExercisePage(params) {
  return http.get('/exercise/page', params)
}

/** 获取动作详情 */
export function getExerciseById(id) {
  return http.get(`/exercise/${id}`)
}

/** AI 生成训练计划 */
export function generatePlan(data) {
  return http.post('/training/plan/generate', data)
}

/** 获取当前计划 */
export function getCurrentPlan() {
  return http.get('/training/plan/current')
}

/** 获取计划列表 */
export function getPlanList(params) {
  return http.get('/training/plan/list', params)
}

/** 获取计划详情 */
export function getPlanById(id) {
  return http.get(`/training/plan/${id}`)
}

/** 开始计划 */
export function startPlan(id) {
  return http.post(`/training/plan/${id}/start`)
}

/** 完成计划 */
export function completePlan(id) {
  return http.post(`/training/plan/${id}/complete`)
}

/** 放弃计划 */
export function abandonPlan(id) {
  return http.post(`/training/plan/${id}/abandon`)
}

/** 恢复计划 */
export function restorePlan(id) {
  return http.post(`/training/plan/${id}/restore`)
}

/** 删除计划 */
export function deletePlan(id) {
  return http.post(`/training/plan/${id}/delete`)
}

/** 添加训练记录 */
export function addTrainingLog(data) {
  return http.post('/training/log', data)
}

/** 获取训练记录 */
export function getTrainingLogs(params) {
  return http.get('/training/log/list', params)
}

/** 获取训练日历 */
export function getTrainingCalendar(year, month) {
  return http.get('/training/calendar', { year, month })
}

/** 获取训练统计 */
export function getTrainingStats() {
  return http.get('/training/stats')
}
