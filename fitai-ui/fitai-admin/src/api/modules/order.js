import http from "@/api";

/**
 * @name 订单管理模块
 */

// 分页查询订单列表
export const getOrderPageApi = params => {
  return http.get(`/admin/order/page`, params);
};

// 获取订单详情
export const getOrderDetailApi = id => {
  return http.get(`/admin/order/${id}`);
};

// 发货
export const shipOrderApi = (id, data) => {
  return http.put(`/admin/order/${id}/ship`, data);
};

// 取消订单
export const cancelOrderApi = id => {
  return http.put(`/admin/order/${id}/cancel`);
};

// 查询订单物流轨迹
export const getOrderLogisticsApi = id => {
  return http.get(`/admin/order/${id}/logistics`);
};
