import http from "@/api";

/**
 * @name 商品管理模块
 * 对齐 AdminProductController：/admin/product
 * 对齐 PrdCategoryController：/admin/product/category
 */

// 分页查询商品列表 GET /admin/product/page
export const getProductPageApi = params => {
  return http.get(`/admin/product/page`, params);
};

// 商品详情 GET /admin/product/{spuId}
export const getProductDetailApi = spuId => {
  return http.get(`/admin/product/${spuId}`);
};

// 新增商品 POST /admin/product
export const addProductApi = data => {
  return http.post(`/admin/product`, data);
};

// 编辑商品 PUT /admin/product/{spuId}
export const updateProductApi = (id, data) => {
  const spuId = data?.spuId ?? id;
  return http.put(`/admin/product/${spuId}`, data);
};

// 修改商品状态 PUT /admin/product/{spuId}/status?status=
export const updateProductStatusApi = (id, data) => {
  const spuId = data?.spuId ?? id;
  const status = data?.status;
  return http.put(`/admin/product/${spuId}/status?status=${encodeURIComponent(status ?? "")}`);
};

// 删除商品（AdminProduct 无删除接口，走 PrdSpuController）
export const deleteProductApi = id => {
  return http.delete(`/admin/product/spu/${id}`);
};

// SPU 销售属性（SKU 规格维度）GET /admin/product/{spuId}/sale-attr
export const getSaleAttrApi = spuId => {
  return http.get(`/admin/product/${spuId}/sale-attr`);
};

// 按 SPU 拉取 SKU 列表 GET /admin/product/{spuId}/sku
export const getProductSkuListApi = spuId => {
  return http.get(`/admin/product/${spuId}/sku`);
};

// 新增 SKU POST /admin/product/{spuId}/sku（对齐 SkuAddDTO）
export const addSkuApi = (spuId, data) => {
  return http.post(`/admin/product/${spuId}/sku`, data);
};

// 更新 SKU PUT /admin/product/sku/{skuId}（AdminProductController）
export const updateSkuApi = (id, data) => {
  const skuId = data?.skuId ?? id;
  return http.put(`/admin/product/sku/${skuId}`, data);
};

// 删除 SKU DELETE /admin/product/sku/{skuId}（AdminProductController）
export const deleteSkuApi = id => {
  return http.delete(`/admin/product/sku/${id}`);
};

// 获取商品分类树 GET /admin/product/category/tree
export const getProductCategoryTreeApi = () => {
  return http.get(`/admin/product/category/tree`);
};

// 商品分类详情（回显）GET /admin/product/category/{categoryId}
export const getProductCategoryDetailApi = categoryId => {
  return http.get(`/admin/product/category/${categoryId}`);
};

// 新增商品分类 POST /admin/product/category
export const addProductCategoryApi = data => {
  return http.post(`/admin/product/category`, data);
};

// 编辑商品分类 PUT /admin/product/category（body 含 categoryId / attrIds）
export const updateProductCategoryApi = data => {
  return http.put(`/admin/product/category`, data);
};

// 修改商品分类状态 PUT /admin/product/category/{categoryId}/status?status=
export const updateProductCategoryStatusApi = (categoryId, data) => {
  const status = typeof data === "object" ? data?.status : data;
  return http.put(
    `/admin/product/category/${categoryId}/status?status=${encodeURIComponent(status ?? "")}`
  );
};

// 删除商品分类 DELETE /admin/product/category/{id}
export const deleteProductCategoryApi = id => {
  return http.delete(`/admin/product/category/${id}`);
};

// 品牌下拉 GET /admin/product/brand/select
export const getProductBrandListApi = () => {
  return http.get(`/admin/product/brand/select`);
};

// 分页查询品牌
export const getProductBrandPageApi = params => {
  return http.get(`/admin/product/brand/page`, params);
};

// 新增品牌
export const addProductBrandApi = data => {
  return http.post(`/admin/product/brand`, data);
};

// 编辑品牌（PUT /admin/product/brand，body 含 brandId）
export const updateProductBrandApi = data => {
  return http.put(`/admin/product/brand`, data);
};

// 删除品牌
export const deleteProductBrandApi = id => {
  return http.delete(`/admin/product/brand/${id}`);
};

// 获取所有可选SKU（用于秒杀商品选择等，走 PrdSkuController）
export const getSkuOptionsApi = params => {
  return http.get(`/admin/product/sku/list`, { pageNum: 1, pageSize: 500, ...params }).then(res => {
    const list = res.data?.records || [];
    return { ...res, data: list };
  });
};
