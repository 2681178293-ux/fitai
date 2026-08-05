import request from '@/utils/request'

const API_BASE = import.meta.env.VITE_API_BASE || '/api/pc'

/**
 * 文件上传 API
 * 按业务模块区分存储路径
 */

// ==================== 通用上传 ====================

export function uploadImage(file) {
  const formData = new FormData()
  formData.append('file', file)
  return request.post('/file/upload/image', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

export function uploadFile(file, folder = 'default') {
  const formData = new FormData()
  formData.append('file', file)
  return request.post(`/file/upload?folder=${folder}`, formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

// ==================== 用户模块 ====================

export function uploadAvatar(file) {
  const formData = new FormData()
  formData.append('file', file)
  return request.post('/file/upload/avatar', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

// ==================== 社区模块 ====================

export function uploadCommunityImage(file) {
  const formData = new FormData()
  formData.append('file', file)
  return request.post('/file/upload/community/image', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

// ==================== 训练模块 ====================

export function uploadTrainingImage(file) {
  const formData = new FormData()
  formData.append('file', file)
  return request.post('/file/upload/training/image', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

// ==================== 删除 ====================

export function deleteFile(url) {
  return request.delete('/file', { params: { url } })
}

// ==================== 上传 URL 常量 ====================

export const UPLOAD_URLS = {
  image: `${API_BASE}/file/upload/image`,
  avatar: `${API_BASE}/file/upload/avatar`,
  community: `${API_BASE}/file/upload/community/image`,
  training: `${API_BASE}/file/upload/training/image`
}
