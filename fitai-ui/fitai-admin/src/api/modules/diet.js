import http from "@/api";

/** 管理端 - 饮食管理 */

export const getDietMealPlanPageApi = params => {
  return http.get(`/admin/diet/meal-plan/page`, params);
};

export const getDietRecordPageApi = params => {
  return http.get(`/admin/diet/record/page`, params);
};

export const getDietFoodPageApi = params => {
  return http.get(`/admin/diet/food/page`, params);
};

export const addDietFoodApi = data => {
  return http.post(`/admin/diet/food`, data);
};

export const updateDietFoodApi = (id, data) => {
  return http.put(`/admin/diet/food/${id}`, data);
};

export const deleteDietFoodApi = id => {
  return http.delete(`/admin/diet/food/${id}`);
};

/** 食物分类下拉（仅启用） */
export const getDietFoodCategoryOptionsApi = () => {
  return http.get(`/admin/diet/food-category/options`);
};

/** 食物分类分页 */
export const getDietFoodCategoryPageApi = params => {
  return http.get(`/admin/diet/food-category/page`, params);
};

export const addDietFoodCategoryApi = data => {
  return http.post(`/admin/diet/food-category`, data);
};

export const updateDietFoodCategoryApi = (id, data) => {
  return http.put(`/admin/diet/food-category/${id}`, data);
};

export const deleteDietFoodCategoryApi = id => {
  return http.delete(`/admin/diet/food-category/${id}`);
};
