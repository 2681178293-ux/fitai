import http from "@/api";

/**
 * @name 用户管理模块
 */

// 分页查询用户列表
export const getUserPageApi = params => {
  return http.get(`/admin/user/page`, params);
};

// 获取用户详情
export const getUserDetailApi = id => {
  return http.get(`/admin/user/${id}`);
};

// 更新用户状态
export const updateUserStatusApi = (id, status) => {
  return http.put(`/admin/user/${id}/status`, { status });
};

/**
 * @name 用户分群
 */

// 分页查询分群列表
export const getUserSegmentPageApi = params => {
  return http.get(`/admin/user/segment/page`, params);
};

// 分群详情
export const getUserSegmentDetailApi = id => {
  return http.get(`/admin/user/segment/${id}`);
};

// 新增分群
export const createUserSegmentApi = data => {
  return http.post(`/admin/user/segment`, data);
};

// 编辑分群
export const updateUserSegmentApi = (id, data) => {
  return http.put(`/admin/user/segment/${id}`, data);
};

// 批量删除分群（ids 逗号分隔）
export const deleteUserSegmentApi = ids => {
  return http.delete(`/admin/user/segment/${ids}`);
};

// 分群内用户分页
export const getUserSegmentUserPageApi = (id, params) => {
  return http.get(`/admin/user/segment/${id}/users`, params);
};

// 批量加入分群用户
export const addUserSegmentUserApi = (id, userIds) => {
  return http.post(`/admin/user/segment/${id}/users`, { userIds });
};

// 批量移出分群用户（userIds 逗号分隔）
export const removeUserSegmentUserApi = (id, userIds) => {
  return http.delete(`/admin/user/segment/${id}/users/${userIds}`);
};

/**
 * @name 身体数据
 */

// 分页查询身体数据
export const getBodyRecordPageApi = params => {
  return http.get(`/admin/user/body/page`, params);
};

// 身体数据详情
export const getBodyRecordDetailApi = id => {
  return http.get(`/admin/user/body/${id}`);
};

// 身体数据总览 KPI
export const getBodyRecordKpiApi = () => {
  return http.get(`/admin/user/body/kpi`, {}, { loading: false });
};

// 指定用户体重/体脂趋势
export const getBodyRecordTrendApi = (userId, limit = 30) => {
  return http.get(`/admin/user/body/trend/${userId}`, { limit });
};

// 新增身体记录
export const createBodyRecordApi = data => {
  return http.post(`/admin/user/body`, data);
};

// 编辑身体记录
export const updateBodyRecordApi = (id, data) => {
  return http.put(`/admin/user/body/${id}`, data);
};

// 批量删除身体记录（ids 逗号分隔）
export const deleteBodyRecordApi = ids => {
  return http.delete(`/admin/user/body/${ids}`);
};

/**
 * @name 用户设置
 */

// 分页查询用户设置
export const getUserSettingPageApi = params => {
  return http.get(`/admin/user/setting/page`, params);
};

// 查询指定用户设置
export const getUserSettingDetailApi = userId => {
  return http.get(`/admin/user/setting/${userId}`);
};

// 修改用户设置
export const updateUserSettingApi = (userId, data) => {
  return http.put(`/admin/user/setting/${userId}`, data);
};

/**
 * @name 收货地址
 */

// 分页查询收货地址
export const getUserAddressPageApi = params => {
  return http.get(`/admin/user/address/page`, params);
};

// 批量删除收货地址（ids 逗号分隔）
export const deleteUserAddressApi = ids => {
  return http.delete(`/admin/user/address/${ids}`);
};
