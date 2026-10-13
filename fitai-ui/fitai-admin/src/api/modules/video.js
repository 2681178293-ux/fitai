import http from "@/api";

/**
 * @name 视频管理模块（对接媒体中心 VOD）
 * 对外仍保持旧方法名，内部字段会映射成 VideoLibrary 使用的 id/title/coverUrl 等
 */

const mapVodToVideo = v => {
  if (!v) return v;
  // 有阿里云 VideoId 即可选；封面/时长可能仍在回调中
  const ready = !!v.vodVideoId;
  return {
    ...v,
    id: v.vodAssetId,
    title: v.vodName,
    coverUrl: v.vodCoverUrl,
    duration: v.vodDuration,
    size: v.vodSize,
    status: ready ? 2 : 1,
    categoryId: v.categoryId,
    categoryName: v.categoryName
  };
};

const flattenCategoryTree = (nodes = [], list = []) => {
  nodes.forEach(n => {
    list.push({
      id: n.categoryId,
      name: n.categoryName,
      count: n.videoCount || 0,
      parentId: n.parentId
    });
    if (n.children?.length) flattenCategoryTree(n.children, list);
  });
  return list;
};

/** 分页查询视频列表 */
export const getVideoPageApi = async (params = {}, config = {}) => {
  const res = await http.get(
    `/admin/media/vod/list`,
    {
      categoryId: params.categoryId || undefined,
      keyword: params.keyword || params.title || undefined,
      pageNum: params.pageNum || 1,
      pageSize: params.pageSize || 500
    },
    config
  );
  if (res?.data?.records) {
    res.data.records = res.data.records.map(mapVodToVideo);
  }
  return res;
};

/** 获取播放地址（MediaVodController：GET /{videoId}/play-url） */
export const getPlayUrlApi = id => {
  return http.get(`/admin/media/vod/${id}/play-url`);
};

/** 删除视频 */
export const deleteVideoApi = id => {
  return http.delete(`/admin/media/vod/${id}`);
};

/** 编辑视频信息（媒体侧仅部分字段） */
export const updateVideoApi = (id, data) => {
  return http.put(`/admin/media/vod`, {
    vodAssetId: id,
    vodName: data.title,
    categoryId: data.categoryId
  });
};

/** 旧同步接口：媒体中心靠回调，这里做空实现避免报错 */
export const syncVideoApi = async () => ({ code: 200, data: null });
export const syncVideoBatchApi = async () => ({ code: 200, data: null });

/** 旧上传凭证接口：组件改为直传 FormData，保留占位避免引用报错 */
export const getUploadCredentialApi = async () => {
  throw new Error("请使用媒体中心上传接口");
};
export const uploadCompleteApi = async () => ({ code: 200 });

// ==================== 视频分类 ====================

/** 获取视频分类列表（含视频数量） */
export const getVideoCategoryListApi = async () => {
  const res = await http.get(`/admin/media/vod/category/tree`);
  return {
    ...res,
    data: flattenCategoryTree(res.data || [])
  };
};

/** 新增视频分类 */
export const addVideoCategoryApi = data => {
  return http.post(`/admin/media/vod-category`, {
    categoryName: data.name || data.categoryName,
    parentId: data.parentId ?? 0,
    ancestors: data.ancestors || "0",
    orderNum: data.orderNum ?? 0,
    status: data.status || "0"
  });
};

/** 更新视频分类 */
export const updateVideoCategoryApi = (id, data) => {
  return http.put(`/admin/media/vod-category`, {
    categoryId: id,
    categoryName: data.name || data.categoryName,
    orderNum: data.orderNum
  });
};

/** 删除视频分类 */
export const deleteVideoCategoryApi = id => {
  return http.delete(`/admin/media/vod-category/${id}`);
};

/** 媒体中心直传（VideoLibrary 上传使用） */
export const uploadMediaVodDirectApi = formData => {
  return http.post(`/admin/media/vod/upload`, formData, { cancel: false });
};
