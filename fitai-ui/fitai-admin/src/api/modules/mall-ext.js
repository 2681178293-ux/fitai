import http from "@/api";

/**
 * @name 商城扩展模块（退款/物流/支付渠道/商品属性/库存/ES/秒杀订单/挑战赛/优惠券领取记录）
 * 独立文件，避免与现有 order.js / product.js 等冲突。
 */

/* ================= 退款管理 /admin/pay/refund ================= */
export const getRefundPageApi = params => http.get("/admin/pay/refund/page", params);
export const getRefundDetailApi = id => http.get(`/admin/pay/refund/${id}`);
export const approveRefundApi = id => http.put(`/admin/pay/refund/${id}/approve`);
export const rejectRefundApi = (id, remark) => http.put(`/admin/pay/refund/${id}/reject`, null, { params: { remark } });
export const completeRefundApi = id => http.put(`/admin/pay/refund/${id}/complete`);
export const getRefundStatsApi = () => http.get("/admin/pay/refund/stats");

/* ================= 物流管理 /admin/order/logistics ================= */
export const getLogisticsPageApi = params => http.get("/admin/order/logistics/page", params);
export const getLogisticsDetailApi = id => http.get(`/admin/order/logistics/${id}`);
export const createLogisticsApi = data => http.post("/admin/order/logistics/create", data);
export const refreshLogisticsApi = id => http.put(`/admin/order/logistics/${id}/refresh`);

/* ================= 支付渠道 /admin/pay/channel ================= */
export const getChannelPageApi = params => http.get("/admin/pay/channel/page", params);
export const getChannelDetailApi = id => http.get(`/admin/pay/channel/${id}`);
export const addChannelApi = data => http.post("/admin/pay/channel", data);
export const updateChannelApi = (id, data) => http.put(`/admin/pay/channel/${id}`, data);
export const toggleChannelStatusApi = id => http.put(`/admin/pay/channel/${id}/status`);
export const deleteChannelApi = id => http.delete(`/admin/pay/channel/${id}`);

/* ================= 商品属性 /admin/product/attr ================= */
export const getAttrPageApi = params => http.get("/admin/product/attr/page", params);
export const getAttrListApi = () => http.get("/admin/product/attr/list");
export const getAttrDetailApi = id => http.get(`/admin/product/attr/${id}`);
export const addAttrApi = data => http.post("/admin/product/attr", data);
export const updateAttrApi = data => http.put("/admin/product/attr", data);
export const deleteAttrApi = id => http.delete(`/admin/product/attr/${id}`);
export const getCategoryAttrsApi = categoryId => http.get(`/admin/product/attr/category/${categoryId}`);
export const setCategoryAttrsApi = (categoryId, attrIds) => http.put(`/admin/product/attr/category/${categoryId}`, attrIds);
/** 分类-属性关联分页（prd_category_attr） */
export const getCategoryAttrPageApi = params => http.get("/admin/product/category-attr/list", params);

/* ================= 库存管理 /admin/product/stock ================= */
export const getStockPageApi = params => http.get("/admin/product/stock/page", params);
export const adjustStockApi = data => http.put("/admin/product/stock/adjust", data);
export const getStockWarningApi = params => http.get("/admin/product/stock/warning", params);
export const getStockLogApi = skuId => http.get(`/admin/product/stock/log/${skuId}`);

/* ================= ES 索引 /admin/product/es ================= */
export const getEsStatusApi = () => http.get("/admin/product/es/status");
export const rebuildEsApi = () => http.post("/admin/product/es/rebuild");
export const deleteEsIndexApi = () => http.delete("/admin/product/es/index");

/* ================= 秒杀订单 /admin/seckill/plus ================= */
export const getSeckillOrderPageApi = params => http.get("/admin/seckill/plus/order/page", params);
export const getSeckillStatsApi = () => http.get("/admin/seckill/plus/stats");

/* ================= 挑战赛管理 /admin/challenge ================= */
export const getChallengePageApi = params => http.get("/admin/challenge/page", params);
export const getChallengeDetailApi = id => http.get(`/admin/challenge/${id}`);
export const addChallengeApi = data => http.post("/admin/challenge", data);
export const updateChallengeApi = (id, data) => http.put(`/admin/challenge/${id}`, data);
export const deleteChallengeApi = id => http.delete(`/admin/challenge/${id}`);
export const changeChallengeStatusApi = (id, status) => http.put(`/admin/challenge/${id}/status/${status}`);
export const getChallengeRecordPageApi = params => http.get("/admin/challenge/record/page", params);
export const markChallengeCheatApi = (recordId, cheatFlag) => http.put(`/admin/challenge/record/${recordId}/cheat/${cheatFlag}`);
export const adjustChallengeScoreApi = data => http.put("/admin/challenge/record/adjust", data);

/* ================= 优惠券领取记录 /admin/marketing/coupon-recv ================= */
export const getCouponRecvPageApi = params => http.get("/admin/marketing/coupon-recv/page", params);
export const voidCouponRecvApi = id => http.put(`/admin/marketing/coupon-recv/${id}/void`);
export const getCouponRecvStatsApi = () => http.get("/admin/marketing/coupon-recv/stats");
