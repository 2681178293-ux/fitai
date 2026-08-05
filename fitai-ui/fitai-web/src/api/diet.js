import request from '@/utils/request'

/**
 * AI 生成饮食计划
 */
export function generateMealPlan(data) {
  return request({
    url: '/diet/plan/generate',
    method: 'post',
    data
  })
}

/**
 * AI 仅预览（不落库）
 */
export function previewMealPlan(data) {
  return request({
    url: '/diet/plan/preview',
    method: 'post',
    data
  })
}

/**
 * 保存饮食计划（采纳预览 / 编辑）
 */
export function saveMealPlan(data) {
  return request({
    url: '/diet/plan',
    method: 'put',
    data
  })
}

/**
 * 获取指定日期的饮食计划
 */
export function getMealPlan(date) {
  return request({
    url: '/diet/plan',
    method: 'get',
    params: { date }
  })
}

/**
 * 添加饮食记录
 */
export function addDietRecord(data) {
  return request({
    url: '/diet/record',
    method: 'post',
    data
  })
}

/**
 * 更新饮食记录
 */
export function updateDietRecord(id, data) {
  return request({
    url: `/diet/record/${id}`,
    method: 'put',
    data
  })
}

/**
 * 获取指定日期的饮食记录
 */
export function getDietRecords(date) {
  return request({
    url: '/diet/records',
    method: 'get',
    params: { date }
  })
}

/**
 * 删除饮食记录
 */
export function deleteDietRecord(id) {
  return request({
    url: `/diet/record/${id}`,
    method: 'delete'
  })
}

/**
 * 获取每日营养统计
 */
export function getDailyNutritionStats(date) {
  return request({
    url: '/diet/stats',
    method: 'get',
    params: { date }
  })
}

/**
 * 按主键查询食物（与数据库一致）
 */
export function getFoodById(id) {
  return request({
    url: `/diet/food/detail/${id}`,
    method: 'get'
  })
}

/**
 * 搜索食物库（keyword 为空时不传参，后端返回前 30 条）
 */
export function searchFood(keyword) {
  const params = {}
  if (keyword != null && String(keyword).trim() !== '') {
    params.keyword = String(keyword).trim()
  }
  return request({
    url: '/diet/food/search',
    method: 'get',
    params
  })
}

/**
 * 计算用户 TDEE
 */
export function calculateTDEE() {
  return request({
    url: '/diet/tdee',
    method: 'get'
  })
}
