import http from "@/api";

/**
 * @name 用户管理模块
 */

// 分页查询 C 端用户列表
export const getUserPageApi = params => {
  return http.get(`/admin/user/customer/list`, params);
};

// 获取 C 端用户详情
export const getUserDetailApi = id => {
  return http.get(`/admin/user/customer/${id}`);
};

// 更新 C 端用户状态（0正常 1冻结）
export const updateUserStatusApi = (id, status) => {
  return http.put(`/admin/user/customer`, { customerId: id, status });
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

// 编辑分群：body 含 segmentId
export const updateUserSegmentApi = data => {
  return http.put(`/admin/user/segment`, data);
};

// 批量删除分群（ids 逗号分隔）
export const deleteUserSegmentApi = ids => {
  return http.delete(`/admin/user/segment/${ids}`);
};

// 分群内用户分页（后端 segment-rel/list，无按分群过滤参数）
export const getUserSegmentUserPageApi = (segmentId, params) => {
  return http.get(`/admin/user/segment-rel/list`, { pageNum: 1, pageSize: 500, ...params }).then(res => {
    const records = (res.data?.records || []).filter(r => Number(r.segmentId) === Number(segmentId));
    return {
      ...res,
      data: {
        ...res.data,
        records,
        total: records.length
      }
    };
  });
};

// 批量加入分群用户
export const addUserSegmentUserApi = (segmentId, userIds) => {
  return Promise.all(
    userIds.map(customerId => http.post(`/admin/user/segment-rel`, { segmentId, customerId }))
  );
};

// 批量移出分群用户（传关联主键 segmentRelId，逗号分隔）
export const removeUserSegmentUserApi = (_segmentId, segmentRelIds) => {
  return http.delete(`/admin/user/segment-rel/${segmentRelIds}`);
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
export const getBodyRecordTrendApi = (customerId, limit = 30) => {
  return http.get(`/admin/user/body/trend/${customerId}`, { limit });
};

// 新增身体记录
export const createBodyRecordApi = data => {
  return http.post(`/admin/user/body`, data);
};

// 编辑身体记录：body 含 metricId
export const updateBodyRecordApi = data => {
  return http.put(`/admin/user/body`, data);
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
