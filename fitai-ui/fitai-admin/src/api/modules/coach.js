import http from "@/api";

/**
 * @name 教练体系模块（管理端）
 */

/* ------------------------- A1 教练档案 /admin/coach ------------------------- */

/** 分页查询教练，params: { name, specialty, status, pageNum, pageSize } */
export const getCoachPageApi = params => {
  return http.get(`/admin/coach/page`, params);
};

/** 教练下拉选项（id + name） */
export const getCoachListApi = () => {
  return http.get(`/admin/coach/list`, {}, { loading: false });
};

/** 教练详情 */
export const getCoachDetailApi = id => {
  return http.get(`/admin/coach/${id}`);
};

/** 新增教练 */
export const createCoachApi = data => {
  return http.post(`/admin/coach`, data);
};

/** 编辑教练 */
export const updateCoachApi = (id, data) => {
  return http.put(`/admin/coach/${id}`, data);
};

/** 批量删除教练（ids 逗号分隔） */
export const deleteCoachApi = ids => {
  return http.delete(`/admin/coach/${ids}`);
};

/** 启用停用教练 */
export const updateCoachStatusApi = (id, status) => {
  return http.put(`/admin/coach/${id}/status`, { status });
};

/* ------------------------ A2 教练排期 /admin/coach/schedule ------------------------ */

/** 查询教练排期（不存在返回默认模板） */
export const getCoachScheduleApi = coachId => {
  return http.get(`/admin/coach/schedule/${coachId}`);
};

/** 保存教练排期，data: { workDays, workSlots, leaveDates } */
export const updateCoachScheduleApi = (coachId, data) => {
  return http.put(`/admin/coach/schedule/${coachId}`, data);
};

/* ------------------------ A3 教练预约 /admin/coach/booking ------------------------ */

/** 预约分页，params: { coachId, userId, status, startDate, endDate, pageNum, pageSize } */
export const getCoachBookingPageApi = params => {
  return http.get(`/admin/coach/booking/page`, params);
};

/** 预约详情 */
export const getCoachBookingDetailApi = id => {
  return http.get(`/admin/coach/booking/${id}`);
};

/** 确认预约（0 -> 1） */
export const confirmCoachBookingApi = id => {
  return http.put(`/admin/coach/booking/${id}/confirm`);
};

/** 完成预约（1 -> 2） */
export const completeCoachBookingApi = id => {
  return http.put(`/admin/coach/booking/${id}/complete`);
};

/** 取消预约，data: { cancelReason } */
export const cancelCoachBookingApi = (id, data) => {
  return http.put(`/admin/coach/booking/${id}/cancel`, data);
};

/* -------------------------- A4 学员绑定 /admin/coach/bind -------------------------- */

/** 绑定分页，params: { coachId, userId, bindType, status, pageNum, pageSize } */
export const getCoachBindPageApi = params => {
  return http.get(`/admin/coach/bind/page`, params);
};

/** 新增绑定，data: { userId, coachId, bindType } */
export const createCoachBindApi = data => {
  return http.post(`/admin/coach/bind`, data);
};

/** 解绑（status -> 0，写 endTime） */
export const unbindCoachBindApi = id => {
  return http.put(`/admin/coach/bind/${id}/unbind`);
};
