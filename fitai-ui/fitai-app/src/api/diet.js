import http from '@/utils/request'

/** AI 生成饮食计划（一步保存） */
export function generateMealPlan(data) {
  return http.post('/diet/plan/generate', data)
}

/** AI 仅预览 */
export function previewMealPlan(data) {
  return http.post('/diet/plan/preview', data)
}

/** 保存计划（采纳/编辑） */
export function saveMealPlan(data) {
  return http.put('/diet/plan', data)
}

/** 获取指定日期的饮食计划 */
export function getMealPlan(date) {
  return http.get('/diet/plan', { date })
}

/** 添加饮食记录 */
export function addDietRecord(data) {
  return http.post('/diet/record', data)
}

/** 更新饮食记录 */
export function updateDietRecord(id, data) {
  return http.put(`/diet/record/${id}`, data)
}

/** 获取指定日期的饮食记录 */
export function getDietRecords(date) {
  return http.get('/diet/records', { date })
}

/** 删除饮食记录 */
export function deleteDietRecord(id) {
  return http.delete(`/diet/record/${id}`)
}

/** 获取每日营养统计 */
export function getDailyNutritionStats(date) {
  return http.get('/diet/stats', { date })
}

/** 按主键查询食物（与数据库一致） */
export function getFoodById(id) {
  return http.get(`/diet/food/detail/${id}`)
}

/** 搜索食物库（keyword 为空时不传参，后端返回前 30 条） */
export function searchFood(keyword) {
  const params = {}
  if (keyword != null && String(keyword).trim() !== '') {
    params.keyword = String(keyword).trim()
  }
  return http.get('/diet/food/search', params)
}

/** 计算用户 TDEE */
export function calculateTDEE() {
  return http.get('/diet/tdee')
}
