import request from '@/utils/request'

/**
 * 获取动作列表
 */
export function getExerciseList(params) {
  return request.get('/exercise/list', { params })
}

/**
 * 分页查询动作
 */
export function getExercisePage(params) {
  return request.get('/exercise/page', { params })
}

/**
 * 获取动作详情
 */
export function getExerciseById(id) {
  return request.get(`/exercise/${id}`)
}

/**
 * AI 生成训练计划
 */
export function generatePlan(data) {
  return request.post('/training/plan/generate', data)
}

/**
 * AI 修改训练计划中的单个动作
 */
export function modifyPlanExercise(data) {
  return request.post('/training/plan/exercise/modify', data)
}

/**
 * 获取当前计划
 */
export function getCurrentPlan() {
  return request.get('/training/plan/current')
}

/**
 * 获取计划列表
 */
export function getPlanList(params) {
  return request.get('/training/plan/list', { params })
}

/**
 * 获取计划详情
 */
export function getPlanById(id) {
  return request.get(`/training/plan/${id}`)
}

/**
 * 开始计划
 */
export function startPlan(id) {
  return request.post(`/training/plan/${id}/start`)
}

/**
 * 完成计划
 */
export function completePlan(id) {
  return request.post(`/training/plan/${id}/complete`)
}

/**
 * 放弃计划
 */
export function abandonPlan(id) {
  return request.post(`/training/plan/${id}/abandon`)
}

/**
 * 恢复计划（将已放弃的计划恢复为草稿）
 */
export function restorePlan(id) {
  return request.post(`/training/plan/${id}/restore`)
}

/**
 * 删除计划
 */
export function deletePlan(id) {
  return request.post(`/training/plan/${id}/delete`)
}

/**
 * 添加训练记录
 */
export function addTrainingLog(data) {
  return request.post('/training/log', data)
}

/**
 * 获取训练记录
 */
export function getTrainingLogs(params) {
  return request.get('/training/log/list', { params })
}

/**
 * 获取训练日历
 */
export function getTrainingCalendar(year, month) {
  return request.get('/training/calendar', { params: { year, month } })
}

/**
 * 获取训练统计
 */
export function getTrainingStats() {
  return request.get('/training/stats')
}

/**
 * 诊断计划状态（用于排查问题）
 */
export function diagnosePlans() {
  return request.get('/training/plan/diagnose')
}
