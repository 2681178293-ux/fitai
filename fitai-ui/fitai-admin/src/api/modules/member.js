import http from "@/api";

/** 会员套餐列表（管理端含下架） */
export const getMemberPlansApi = () => {
  return http.get(`/admin/member/plans`, {}, { loading: false });
};

export const createMemberPlanApi = data => {
  return http.post(`/admin/member/plans`, data);
};

export const updateMemberPlanApi = (id, data) => {
  return http.put(`/admin/member/plans/${id}`, data);
};

export const deleteMemberPlanApi = id => {
  return http.delete(`/admin/member/plans/${id}`);
};

/** 会员订单分页 */
export const getMemberOrderPageApi = params => {
  return http.get(`/admin/member/orders/page`, params);
};

export const getMemberOrderDetailApi = id => {
  return http.get(`/admin/member/orders/${id}`);
};

/* ==================== 徽章管理 /admin/member/badge ==================== */

/** 徽章分页 BadgeQueryDTO(name, code, category, status) */
export const getBadgePageApi = params => {
  return http.get(`/admin/member/badge/page`, params);
};

export const getBadgeDetailApi = id => {
  return http.get(`/admin/member/badge/${id}`);
};

export const createBadgeApi = data => {
  return http.post(`/admin/member/badge`, data);
};

export const updateBadgeApi = (id, data) => {
  return http.put(`/admin/member/badge/${id}`, data);
};

/** 批量删除，ids 支持逗号分隔 */
export const deleteBadgeApi = ids => {
  return http.delete(`/admin/member/badge/${ids}`);
};

/** 用户获章记录分页 UserBadgeQueryDTO(userId, badgeId) */
export const getBadgeGrantPageApi = params => {
  return http.get(`/admin/member/badge/grant/page`, params);
};

/** 手动发放徽章 {badgeId, userIds:[]} */
export const grantBadgeApi = data => {
  return http.post(`/admin/member/badge/grant`, data);
};

/* ==================== 会员等级 /admin/member/level ==================== */

export const getLevelPageApi = params => {
  return http.get(`/admin/member/level/page`, params);
};

/** 全部等级（升序），供下拉使用 */
export const getLevelListApi = () => {
  return http.get(`/admin/member/level/list`, {}, { loading: false });
};

export const getLevelDetailApi = id => {
  return http.get(`/admin/member/level/${id}`);
};

export const createLevelApi = data => {
  return http.post(`/admin/member/level`, data);
};

export const updateLevelApi = (id, data) => {
  return http.put(`/admin/member/level/${id}`, data);
};

export const deleteLevelApi = ids => {
  return http.delete(`/admin/member/level/${ids}`);
};

/* ==================== 会员权益 /admin/member/benefit ==================== */

/** 权益分页 BenefitQueryDTO(name, code, status) */
export const getBenefitPageApi = params => {
  return http.get(`/admin/member/benefit/page`, params);
};

/** 全部启用权益（供等级页多选） */
export const getBenefitListApi = () => {
  return http.get(`/admin/member/benefit/list`, {}, { loading: false });
};

export const getBenefitDetailApi = id => {
  return http.get(`/admin/member/benefit/${id}`);
};

export const createBenefitApi = data => {
  return http.post(`/admin/member/benefit`, data);
};

export const updateBenefitApi = (id, data) => {
  return http.put(`/admin/member/benefit/${id}`, data);
};

export const deleteBenefitApi = ids => {
  return http.delete(`/admin/member/benefit/${ids}`);
};

/* ==================== 签到记录 /admin/member/checkin ==================== */

/** 签到分页 CheckinQueryDTO(userId, startDate, endDate) */
export const getCheckinPageApi = params => {
  return http.get(`/admin/member/checkin/page`, params);
};

/** 签到 KPI：今日签到数、本月签到数、最长连续天数 */
export const getCheckinKpiApi = () => {
  return http.get(`/admin/member/checkin/kpi`, {}, { loading: false });
};

/** 成长值流水分页 GrowthLogQueryDTO(userId, bizType) */
export const getGrowthLogPageApi = params => {
  return http.get(`/admin/member/checkin/growth/page`, params);
};

export const deleteCheckinApi = ids => {
  return http.delete(`/admin/member/checkin/${ids}`);
};

/* ==================== 积分规则 /admin/member/points-rule ==================== */

/** 积分规则分页 PointsRuleQueryDTO(name, eventType, status) */
export const getPointsRulePageApi = params => {
  return http.get(`/admin/member/points-rule/page`, params);
};

/** 事件类型枚举 [{value,label}] */
export const getPointsRuleEventsApi = () => {
  return http.get(`/admin/member/points-rule/events`, {}, { loading: false });
};

export const getPointsRuleDetailApi = id => {
  return http.get(`/admin/member/points-rule/${id}`);
};

export const createPointsRuleApi = data => {
  return http.post(`/admin/member/points-rule`, data);
};

export const updatePointsRuleApi = (id, data) => {
  return http.put(`/admin/member/points-rule/${id}`, data);
};

export const deletePointsRuleApi = ids => {
  return http.delete(`/admin/member/points-rule/${ids}`);
};

/* ==================== 积分流水 /admin/member/points-log ==================== */

/** 积分流水分页 PointsLogQueryDTO(userId, type, source, startTime, endTime) */
export const getPointsLogPageApi = params => {
  return http.get(`/admin/member/points-log/page`, params);
};

/** 积分 KPI：今日发放、今日消耗、累计发放 */
export const getPointsLogKpiApi = () => {
  return http.get(`/admin/member/points-log/kpi`, {}, { loading: false });
};

/** 手动调整积分 PointsAdjustDTO(userId, points, remark) */
export const adjustPointsApi = data => {
  return http.post(`/admin/member/points-log/adjust`, data);
};

/** 导出积分流水 CSV（返回文件流） */
export const exportPointsLogApi = params => {
  return http.get(`/admin/member/points-log/export`, params, { responseType: "blob" });
};

/* ==================== 会员订阅 /admin/member/subscription ==================== */

/** 订阅分页 SubscriptionQueryDTO(userId, planId, status) */
export const getSubscriptionPageApi = params => {
  return http.get(`/admin/member/subscription/page`, params);
};

/** 订阅 KPI：生效中会员数、本月新增、7日内到期数 */
export const getSubscriptionKpiApi = () => {
  return http.get(`/admin/member/subscription/kpi`, {}, { loading: false });
};

/** 取消订阅 */
export const cancelSubscriptionApi = id => {
  return http.put(`/admin/member/subscription/${id}/cancel`);
};
