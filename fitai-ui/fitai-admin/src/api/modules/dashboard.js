import http from "@/api";

/**
 * @name 数据统计模块
 */

// 获取核心统计数据
export const getDashboardStatsApi = () => {
  return http.get(`/admin/dashboard/stats`);
};

// 获取趋势数据
export const getDashboardTrendApi = params => {
  return http.get(`/admin/dashboard/trend`, params);
};

// 获取最近动态
export const getDashboardActivitiesApi = () => {
  return http.get(`/admin/dashboard/activities`);
};

// 获取订单状态分布
export const getDashboardDistributionApi = () => {
  return http.get(`/admin/dashboard/distribution`);
};
