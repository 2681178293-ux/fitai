import http from "@/api";

/**
 * @name 媒体 / OSS 图片中心
 */

// ==================== 图片 ====================

/** 按分类分页查询图片（categoryId 必填） */
export const getMediaOssPageApi = (params, config = {}) => {
  return http.get(`/admin/media/oss/page`, params, config);
};

/** 上传图片：FormData 含 file + categoryId */
export const uploadMediaOssApi = formData => {
  return http.post(`/admin/media/oss/upload`, formData, { cancel: false });
};

/** 批量删除图片 body: { ossIds: [] } */
export const deleteMediaOssApi = ossIds => {
  const ids = Array.isArray(ossIds) ? ossIds : [ossIds];
  return http.delete(`/admin/media/oss`, {}, { data: { ossIds: ids } });
};

/** 按 id 删除单张图片 */
export const deleteMediaOssByIdApi = id => {
  return http.delete(`/admin/media/oss/${id}`);
};

// ==================== 分类 ====================

/** 分类树（含 imageCount） */
export const getMediaOssCategoryTreeApi = (config = {}) => {
  return http.get(`/admin/media/oss/category/tree`, {}, config);
};

/** 新建分类 { categoryName, parentId } */
export const addMediaOssCategoryApi = data => {
  return http.post(`/admin/media/oss/category`, data);
};

/** 重命名分类 { categoryId, categoryName } */
export const updateMediaOssCategoryNameApi = data => {
  return http.put(`/admin/media/oss/category`, data);
};

/** 分类排序移动 { categoryId, targetCategoryId, direction } */
export const moveMediaOssCategoryApi = data => {
  return http.put(`/admin/media/oss/category/move`, data);
};

/** 删除分类 */
export const deleteMediaOssCategoryApi = categoryId => {
  return http.delete(`/admin/media/oss/category/${categoryId}`);
};

// ==================== VOD 视频中心 ====================

/** 视频分类树（含 videoCount） */
export const getMediaVodCategoryTreeApi = (config = {}) => {
  return http.get(`/admin/media/vod/category/tree`, {}, config);
};

/** 新建视频分类 { categoryName, parentId, orderNum?, status? } */
export const addMediaVodCategoryApi = data => {
  return http.post(`/admin/media/vod-category`, data);
};

/** 更新视频分类（重命名 / 排序） */
export const updateMediaVodCategoryApi = data => {
  return http.put(`/admin/media/vod-category`, data);
};

/** 删除视频分类 */
export const deleteMediaVodCategoryApi = categoryId => {
  return http.delete(`/admin/media/vod-category/${categoryId}`);
};

/** 按分类分页查询视频（categoryId / vodName） */
export const getMediaVodPageApi = (params, config = {}) => {
  return http.get(`/admin/media/vod/list`, params, config);
};

/** 删除视频（支持逗号分隔批量） */
export const deleteMediaVodApi = ids => {
  const idStr = Array.isArray(ids) ? ids.join(",") : ids;
  return http.delete(`/admin/media/vod/${idStr}`);
};

/** 上传视频：FormData 含 file + categoryId（后端完善后可用） */
export const uploadMediaVodApi = formData => {
  return http.post(`/admin/media/vod/upload`, formData, { cancel: false });
};

/** 获取视频播放地址（MediaVodController：GET /{videoId}/play-url） */
export const getMediaVodPlayUrlApi = id => {
  return http.get(`/admin/media/vod/${id}/play-url`);
};

/** 同源代理播放地址（给 video 标签用，避免直连 OSS 黑屏） */
export const getMediaVodStreamUrl = id => {
  const base = (import.meta.env.VITE_API_URL || "").replace(/\/$/, "");
  return `${base}/admin/media/vod/stream/${id}`;
};
