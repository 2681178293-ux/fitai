import http from "@/api";

/**
 * @name 动作库（训练管理）模块
 */

// 分页查询动作
export const getExercisePageApi = params => {
  return http.get(`/admin/exercise/page`, params);
};

// 根据ID查询动作
export const getExerciseByIdApi = id => {
  return http.get(`/admin/exercise/${id}`);
};

// 新增动作
export const addExerciseApi = data => {
  return http.post(`/admin/exercise`, data);
};

// 编辑动作
export const updateExerciseApi = (id, data) => {
  return http.put(`/admin/exercise/${id}`, data);
};

// 删除动作
export const deleteExerciseApi = id => {
  return http.delete(`/admin/exercise/${id}`);
};
