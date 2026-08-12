import http from "@/api";

// =====================================================================
// 业务/系统扩展接口（学习进度 / 训练设备 / 训练计划 / 训练记录 /
// 评论管理 / 内容审核 / 通知公告）
// 与后端控制器路由、方法、字段严格一致，独立文件避免与其它模块冲突。
// =====================================================================

// ========== 课程 - 学习进度 /admin/course/progress ==========

/** 分页查询学习进度 */
export const getLearnProgressPageApi = params => http.get("/admin/course/progress/page", params);

/** 完成率统计（courseId 可空=全部） */
export const getLearnProgressStatsApi = courseId =>
  http.get("/admin/course/progress/stats", { courseId });

/** 某课程学员列表 */
export const getCourseStudentsApi = courseId =>
  http.get(`/admin/course/progress/course/${courseId}/students`);

/** 重置学习进度 */
export const resetLearnProgressApi = id => http.put(`/admin/course/progress/${id}/reset`);

// ========== 训练设备 /admin/training/device ==========

/** 分页查询设备 */
export const getTrainDevicePageApi = params => http.get("/admin/training/device/page", params);

/** 设备详情 */
export const getTrainDeviceApi = id => http.get(`/admin/training/device/${id}`);

/** 新增设备 */
export const addTrainDeviceApi = data => http.post("/admin/training/device", data);

/** 编辑设备 */
export const updateTrainDeviceApi = (id, data) => http.put(`/admin/training/device/${id}`, data);

/** 删除设备 */
export const deleteTrainDeviceApi = id => http.delete(`/admin/training/device/${id}`);

/** 分页查询绑定记录 */
export const getTrainDeviceBindPageApi = params =>
  http.get("/admin/training/device/bind/page", params);

/** 解绑设备 */
export const unbindTrainDeviceApi = bindId =>
  http.put(`/admin/training/device/bind/${bindId}/unbind`);

// ========== 训练计划 /admin/training/plan ==========

/** 分页查询训练计划 */
export const getTrainPlanPageApi = params => http.get("/admin/training/plan/page", params);

/** 训练计划详情（含 planData 明细） */
export const getTrainPlanApi = id => http.get(`/admin/training/plan/${id}`);

/** 调整训练计划状态 */
export const updateTrainPlanStatusApi = (id, status) =>
  http.put(`/admin/training/plan/${id}/status`, null, { params: { status } });

/** 删除训练计划 */
export const deleteTrainPlanApi = id => http.delete(`/admin/training/plan/${id}`);

// ========== 训练记录 /admin/training/record ==========

/** 分页查询训练记录 */
export const getTrainRecordPageApi = params => http.get("/admin/training/record/page", params);

/** 训练记录统计 KPI */
export const getTrainRecordKpiApi = () => http.get("/admin/training/record/kpi");

/** 按用户统计训练记录 */
export const getTrainRecordUserStatsApi = () => http.get("/admin/training/record/stats/user");

/** 训练记录详情 */
export const getTrainRecordApi = id => http.get(`/admin/training/record/${id}`);

// ========== 评论管理 /admin/community/comment ==========

/** 分页查询评论 */
export const getCommentPageApi = params => http.get("/admin/community/comment/page", params);

/** 删除评论 */
export const deleteCommentApi = id => http.delete(`/admin/community/comment/${id}`);

/** 批量删除评论 */
export const batchDeleteCommentApi = ids =>
  http.delete("/admin/community/comment/batch", { ids: Array.isArray(ids) ? ids.join(",") : ids });

/** 置顶/取消置顶评论（top：1-置顶 0-取消） */
export const setCommentTopApi = (id, top) =>
  http.put(`/admin/community/comment/${id}/top`, null, { params: { top } });

// ========== 内容审核 /admin/community/moderation ==========

/** 待审列表分页（传 auditStatus=0） */
export const getModerationPageApi = params =>
  http.get("/admin/community/moderation/page", params);

/** 审核历史（已审核） */
export const getModerationHistoryApi = params =>
  http.get("/admin/community/moderation/history", params);

/** 审核记录详情 */
export const getModerationApi = id => http.get(`/admin/community/moderation/${id}`);

/** 提交内容审核 */
export const submitModerationApi = data => http.post("/admin/community/moderation", data);

/** 审核通过 */
export const passModerationApi = id => http.put(`/admin/community/moderation/${id}/pass`);

/** 审核驳回（body：moderationId + reason） */
export const rejectModerationApi = data => http.put("/admin/community/moderation/reject", data);

// ========== 通知公告 /admin/system/notice ==========

/** 公告分页列表 */
export const getNoticePageApi = params => http.get("/admin/system/notice/list", params);

/** 已发布公告列表（顶栏面板） */
export const getPublishedNoticeApi = limit =>
  http.get("/admin/system/notice/published", { limit });

/** 公告详情 */
export const getNoticeApi = id => http.get(`/admin/system/notice/${id}`);

/** 新增公告 */
export const addNoticeApi = data => http.post("/admin/system/notice", data);

/** 修改公告 */
export const updateNoticeApi = data => http.put("/admin/system/notice", data);

/** 删除公告（支持批量：ids 为逗号分隔或数组） */
export const deleteNoticeApi = ids =>
  http.delete(`/admin/system/notice/${Array.isArray(ids) ? ids.join(",") : ids}`);

/** 发布公告 */
export const publishNoticeApi = id => http.put(`/admin/system/notice/${id}/publish`);

/** 撤回公告 */
export const revokeNoticeApi = id => http.put(`/admin/system/notice/${id}/revoke`);

/** 标记当前用户已读 */
export const markNoticeReadApi = id => http.post(`/admin/system/notice/${id}/read`);

/** 公告已读统计 */
export const getNoticeReadStatsApi = id => http.get(`/admin/system/notice/${id}/read-stats`);

/** 公告已读记录列表 */
export const getNoticeReadListApi = id => http.get(`/admin/system/notice/${id}/read-list`);
