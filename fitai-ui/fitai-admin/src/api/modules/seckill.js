import http from "@/api";

/**
 * @name 秒杀管理模块
 */

// 分页查询秒杀活动列表
export const getSeckillActivityPageApi = params => {
  return http.get(`/admin/seckill/activity/page`, params);
};

// 获取秒杀活动详情
export const getSeckillActivityDetailApi = id => {
  return http.get(`/admin/seckill/activity/${id}`);
};

// 创建秒杀活动
export const addSeckillActivityApi = data => {
  return http.post(`/admin/seckill/activity`, data);
};

// 编辑秒杀活动
export const updateSeckillActivityApi = (id, data) => {
  return http.put(`/admin/seckill/activity/${id}`, data);
};

// 删除秒杀活动
export const deleteSeckillActivityApi = id => {
  return http.delete(`/admin/seckill/activity/${id}`);
};

// 获取秒杀商品列表
export const getSeckillSkuListApi = activityId => {
  return http.get(`/admin/seckill/activity/${activityId}/sku`);
};

// 添加秒杀商品
export const addSeckillSkuApi = (activityId, data) => {
  return http.post(`/admin/seckill/activity/${activityId}/sku`, data);
};

// 删除秒杀商品
export const deleteSeckillSkuApi = id => {
  return http.delete(`/admin/seckill/sku/${id}`);
};

// 切换秒杀商品状态
export const toggleSeckillSkuStatusApi = id => {
  return http.put(`/admin/seckill/sku/${id}/status`);
};
