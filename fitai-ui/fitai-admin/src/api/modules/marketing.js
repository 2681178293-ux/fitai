import http from "@/api";

/**
 * @name 营销管理模块 - 轮播图、优惠券
 */

// ==================== 优惠券 ====================

// 分页查询优惠券
export const getCouponPageApi = params => {
  return http.get(`/admin/marketing/coupons/page`, params);
};

// 新增优惠券
export const addCouponApi = data => {
  return http.post(`/admin/marketing/coupons`, data);
};

// 更新优惠券
export const updateCouponApi = (id, data) => {
  return http.put(`/admin/marketing/coupons/${id}`, data);
};

// 删除优惠券
export const deleteCouponApi = id => {
  return http.delete(`/admin/marketing/coupons/${id}`);
};

// 优惠券领取记录
export const getCouponUsersApi = (id, params) => {
  return http.get(`/admin/marketing/coupons/${id}/users`, params);
};

// ==================== 轮播图 ====================

// 获取轮播图列表
export const getBannerListApi = params => {
  return http.get(`/admin/marketing/banners`, params);
};

// 新增轮播图
export const addBannerApi = data => {
  return http.post(`/admin/marketing/banners`, data);
};

// 编辑轮播图
export const updateBannerApi = (id, data) => {
  return http.put(`/admin/marketing/banners/${id}`, data);
};

// 删除轮播图
export const deleteBannerApi = id => {
  return http.delete(`/admin/marketing/banners/${id}`);
};
