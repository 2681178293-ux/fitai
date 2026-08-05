import http from "@/api";

/**
 * @name 文件上传模块
 * 按业务模块区分存储路径
 */

// ==================== 通用上传 ====================
export const uploadImage = params => {
  return http.post(`/file/upload/image`, params, { cancel: false });
};

export const uploadCover = params => {
  return http.post(`/file/upload/cover`, params, { cancel: false });
};

export const uploadFile = (params, folder = "default") => {
  return http.post(`/file/upload?folder=${folder}`, params, { cancel: false });
};

// ==================== 课程模块 ====================
export const uploadCourseCover = params => {
  return http.post(`/file/upload/course/cover`, params, { cancel: false });
};

// ==================== 商品模块 ====================
export const uploadProductCover = params => {
  return http.post(`/file/upload/product/cover`, params, { cancel: false });
};

export const uploadProductImage = params => {
  return http.post(`/file/upload/product/image`, params, { cancel: false });
};

// ==================== 秒杀模块 ====================
export const uploadSeckillCover = params => {
  return http.post(`/file/upload/seckill/cover`, params, { cancel: false });
};

// ==================== 图片库 ====================

// 获取图片列表
export const getImageLibrary = (folder = "", limit = 100) => {
  return http.get(`/file/library`, { folder, limit });
};

// 获取分类列表
export const getImageFolders = () => {
  return http.get(`/file/library/folders`);
};

// 新建分类
export const addFolderApi = (folderKey, label) => {
  return http.post(`/file/library/folder`, { folderKey, label });
};

// 重命名分类
export const renameFolderApi = (key, label) => {
  return http.put(`/file/library/folder`, { key, label });
};

// 删除分类（删除该分类下所有文件）。参数拼接到 URL 并编码，避免 params 被序列成 params[folder] 及空格导致 400
export const deleteFolderApi = folder => {
  const encoded = encodeURIComponent((folder || "").trim());
  return http.delete(`/file/library/folder?folder=${encoded}`);
};

// ==================== 删除 ====================

// 单个删除
export const deleteFile = url => {
  return http.delete(`/file`, { params: { url } });
};

// 批量删除
export const deleteFileBatch = urls => {
  return http.post(`/file/batch/delete`, urls, { loading: true });
};
