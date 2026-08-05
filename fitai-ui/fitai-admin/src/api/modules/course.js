import http from "@/api";

/**
 * @name 课程管理模块
 */

// 分页查询课程列表
export const getCoursePageApi = params => {
  return http.get(`/admin/course/page`, params);
};

// 新增课程
export const addCourseApi = data => {
  return http.post(`/admin/course`, data);
};

// 编辑课程
export const updateCourseApi = (id, data) => {
  return http.put(`/admin/course/${id}`, data);
};

// 删除课程
export const deleteCourseApi = id => {
  return http.delete(`/admin/course/${id}`);
};

// 获取课程分类树
export const getCourseCategoryTreeApi = () => {
  return http.get(`/admin/course/category/tree`);
};

// 新增课程分类
export const addCourseCategoryApi = data => {
  return http.post(`/admin/course/category`, data);
};

// 编辑课程分类
export const updateCourseCategoryApi = (id, data) => {
  return http.put(`/admin/course/category/${id}`, data);
};

// 删除课程分类
export const deleteCourseCategoryApi = id => {
  return http.delete(`/admin/course/category/${id}`);
};
