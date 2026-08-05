import http from "@/api";

/**
 * @name AI 观测 / 配额 模块（调用追踪 / 成本 / Token 用量 / 用户配额 / 方案审核 / 会话管理 / 统计看板增强）
 * 独立文件，避免与 ai.js 冲突。接口路径/方法与后端 admin 控制器严格一致。
 */

// ==================== 调用追踪 /admin/ai/trace ====================
export const getTracePageApi = params => http.get(`/admin/ai/trace/page`, params);
export const getTraceStatsApi = () => http.get(`/admin/ai/trace/stats/summary`);
export const getTraceByIdApi = traceId => http.get(`/admin/ai/trace/${traceId}`);

// ==================== 成本流水 /admin/ai/cost ====================
export const getCostPageApi = params => http.get(`/admin/ai/cost/page`, params);
export const getCostStatsApi = () => http.get(`/admin/ai/cost/stats/summary`);
export const getCostByIdApi = costId => http.get(`/admin/ai/cost/${costId}`);

// ==================== Token 用量 /admin/ai/token-usage ====================
export const getTokenUsagePageApi = params => http.get(`/admin/ai/token-usage/page`, params);
export const getTokenUsageStatsApi = () => http.get(`/admin/ai/token-usage/stats/summary`);
export const getTokenUsageTrendApi = days => http.get(`/admin/ai/token-usage/stats/trend`, { days });
export const getTokenUsageByIdApi = id => http.get(`/admin/ai/token-usage/${id}`);

// ==================== 用户配额 /admin/ai/quota ====================
export const getQuotaPageApi = params => http.get(`/admin/ai/quota/page`, params);
export const getUserQuotaApi = userId => http.get(`/admin/ai/quota/user/${userId}`);
export const grantQuotaGiftApi = data => http.post(`/admin/ai/quota/gift`, data);
export const getQuotaGiftPageApi = params => http.get(`/admin/ai/quota/gift/page`, params);
export const deleteQuotaGiftsApi = ids => http.delete(`/admin/ai/quota/gift/${ids}`);
export const getQuotaConfigApi = () => http.get(`/admin/ai/quota/config`);

// ==================== 方案审核 /admin/ai/plan-review ====================
export const getPlanReviewPageApi = params => http.get(`/admin/ai/plan-review/page`, params);
export const getPlanReviewByIdApi = reviewId => http.get(`/admin/ai/plan-review/${reviewId}`);
export const auditPlanReviewApi = data => http.put(`/admin/ai/plan-review/audit`, data);
export const getPlanReviewPendingCountApi = () => http.get(`/admin/ai/plan-review/pending-count`);
export const submitPlanReviewApi = data => http.post(`/admin/ai/plan-review/submit`, data);

// ==================== 会话管理 /admin/ai/session ====================
export const getSessionPageApi = params => http.get(`/admin/ai/session/page`, params);
export const getSessionByIdApi = id => http.get(`/admin/ai/session/${id}`);
export const getSessionMessagePageApi = params => http.get(`/admin/ai/session/message/page`, params);
export const archiveSessionApi = id => http.put(`/admin/ai/session/${id}/archive`);
export const activateSessionApi = id => http.put(`/admin/ai/session/${id}/activate`);
export const deleteSessionsApi = ids => http.delete(`/admin/ai/session/${ids}`);
/** 导出会话消息 CSV（返回 Blob） */
export const exportSessionApi = id => http.get(`/admin/ai/session/${id}/export`, {}, { responseType: "blob" });

// ==================== 统计看板增强 /admin/ai/stats-plus ====================
export const getStatsOverviewApi = () => http.get(`/admin/ai/stats-plus/overview`);
export const getStatsTokenTrendApi = days => http.get(`/admin/ai/stats-plus/token-trend`, { days });
export const getStatsCallTrendApi = days => http.get(`/admin/ai/stats-plus/call-trend`, { days });
export const getStatsModelDistApi = () => http.get(`/admin/ai/stats-plus/model-dist`);
export const getStatsSceneDistApi = () => http.get(`/admin/ai/stats-plus/scene-dist`);
