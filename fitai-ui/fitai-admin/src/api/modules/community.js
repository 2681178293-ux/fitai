import http from "@/api";

/**
 * @name 社区管理模块
 */

// 分页查询社区动态
export const getCommunityPostPageApi = params => {
  return http.get(`/admin/community/post/page`, params);
};

// 获取动态详情
export const getCommunityPostDetailApi = id => {
  return http.get(`/admin/community/post/${id}`);
};

// 删除动态
export const deleteCommunityPostApi = id => {
  return http.delete(`/admin/community/post/${id}`);
};
