import http from "@/api";

/**
 * @name 视频管理模块
 */

// 分页查询视频列表（第二个参数可传 { loading: false } 关闭全屏 loading，用于后台轮询）
export const getVideoPageApi = (params, config = {}) => {
  return http.get(`/admin/video/page`, params, config);
};

// 获取上传凭证
export const getUploadCredentialApi = params => {
  return http.post(`/admin/video/upload/credential`, null, { params });
};

// 上传完成回调
export const uploadCompleteApi = (videoId, data) => {
  return http.post(`/admin/video/upload/complete/${videoId}`, data);
};

// 编辑视频信息
export const updateVideoApi = (id, data) => {
  return http.put(`/admin/video/${id}`, data);
};

// 删除视频
export const deleteVideoApi = id => {
  return http.delete(`/admin/video/${id}`);
};

// 获取播放地址
export const getPlayUrlApi = id => {
  return http.get(`/admin/video/play/${id}`);
};

// 同步单个视频状态
export const syncVideoApi = id => {
  return http.post(`/admin/video/sync/${id}`);
};

// 批量同步视频状态（可传 { loading: false } 关闭全屏 loading，用于后台轮询）
export const syncVideoBatchApi = (config = {}) => {
  return http.post(`/admin/video/sync/batch`, null, config);
};

// ==================== 视频分类 ====================

// 获取视频分类列表（含视频数量）
export const getVideoCategoryListApi = () => {
  return http.get(`/admin/video/category/list`);
};

// 新增视频分类
export const addVideoCategoryApi = data => {
  return http.post(`/admin/video/category`, data);
};

// 更新视频分类
export const updateVideoCategoryApi = (id, data) => {
  return http.put(`/admin/video/category/${id}`, data);
};

// 删除视频分类
export const deleteVideoCategoryApi = id => {
  return http.delete(`/admin/video/category/${id}`);
};
