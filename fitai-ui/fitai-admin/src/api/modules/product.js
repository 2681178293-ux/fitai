import http from "@/api";

/**
 * @name 商品管理模块
 */

// 分页查询商品列表
export const getProductPageApi = params => {
  return http.get(`/admin/product/page`, params);
};

// 新增商品
export const addProductApi = data => {
  return http.post(`/admin/product`, data);
};

// 编辑商品
export const updateProductApi = (id, data) => {
  return http.put(`/admin/product/${id}`, data);
};

// 删除商品
export const deleteProductApi = id => {
  return http.delete(`/admin/product/${id}`);
};

// 获取商品SKU列表
export const getProductSkuListApi = spuId => {
  return http.get(`/admin/product/${spuId}/sku`);
};

// 更新SKU
export const updateSkuApi = (id, data) => {
  return http.put(`/admin/product/sku/${id}`, data);
};

// 新增SKU
export const addSkuApi = (spuId, data) => {
  return http.post(`/admin/product/${spuId}/sku`, data);
};

// 删除SKU
export const deleteSkuApi = id => {
  return http.delete(`/admin/product/sku/${id}`);
};

// 获取商品分类树
export const getProductCategoryTreeApi = () => {
  return http.get(`/admin/product/category/tree`);
};

// 新增商品分类
export const addProductCategoryApi = data => {
  return http.post(`/admin/product/category`, data);
};

// 编辑商品分类
export const updateProductCategoryApi = (id, data) => {
  return http.put(`/admin/product/category/${id}`, data);
};

// 删除商品分类
export const deleteProductCategoryApi = id => {
  return http.delete(`/admin/product/category/${id}`);
};

// 获取品牌列表（启用的）
export const getProductBrandListApi = () => {
  return http.get(`/admin/product/brand/list`);
};

// 分页查询品牌
export const getProductBrandPageApi = params => {
  return http.get(`/admin/product/brand/page`, params);
};

// 新增品牌
export const addProductBrandApi = data => {
  return http.post(`/admin/product/brand`, data);
};

// 编辑品牌
export const updateProductBrandApi = (id, data) => {
  return http.put(`/admin/product/brand/${id}`, data);
};

// 删除品牌
export const deleteProductBrandApi = id => {
  return http.delete(`/admin/product/brand/${id}`);
};

// 获取所有可选SKU（用于秒杀商品选择等）
export const getSkuOptionsApi = params => {
  return http.get(`/admin/product/sku/options`, params);
};
