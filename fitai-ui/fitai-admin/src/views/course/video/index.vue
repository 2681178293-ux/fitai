<template>
  <div class="media-vod-page">
    <div class="library-container">
      <!-- 左侧分类 -->
      <div class="library-sidebar">
        <div class="sidebar-title">分类</div>
        <div class="folder-list" v-loading="treeLoading">
          <div
            v-for="cat in flatCategories"
            :key="cat.categoryId"
            class="folder-item"
            :class="{ active: currentCategoryId === cat.categoryId }"
            :style="{ paddingLeft: 12 + cat.depth * 14 + 'px' }"
            @click="editingCategoryId === cat.categoryId ? null : selectCategory(cat)"
          >
            <el-icon class="folder-icon">
              <FolderOpened v-if="currentCategoryId === cat.categoryId" />
              <Folder v-else />
            </el-icon>
            <el-input
              v-if="editingCategoryId === cat.categoryId"
              ref="editInputRef"
              v-model="editingCategoryName"
              size="small"
              class="folder-edit-input"
              @click.stop
              @keyup.enter="handleSaveCategoryName"
              @blur="handleSaveCategoryName"
            />
            <span v-else class="folder-name" :title="cat.categoryName">{{ cat.categoryName }}</span>
            <span class="folder-count">{{ cat.videoCount || 0 }}</span>
            <div class="folder-actions" @click.stop>
              <el-icon class="folder-action" title="新建子分类" @click="openNewCategoryDialog(cat.categoryId)">
                <Plus />
              </el-icon>
              <el-icon
                class="folder-action"
                title="上移"
                :class="{ disabled: !canMove(cat, 'up') }"
                @click="canMove(cat, 'up') && handleMove(cat, 'up')"
              >
                <Top />
              </el-icon>
              <el-icon
                class="folder-action"
                title="下移"
                :class="{ disabled: !canMove(cat, 'down') }"
                @click="canMove(cat, 'down') && handleMove(cat, 'down')"
              >
                <Bottom />
              </el-icon>
              <el-icon
                v-if="editingCategoryId !== cat.categoryId"
                class="folder-action"
                title="重命名"
                @click="startEditCategory(cat)"
              >
                <Edit />
              </el-icon>
              <el-icon class="folder-action danger" title="删除" @click="handleDeleteCategory(cat)">
                <Delete />
              </el-icon>
            </div>
          </div>
          <el-empty v-if="!treeLoading && flatCategories.length === 0" description="暂无分类" :image-size="64" />
        </div>
        <div class="sidebar-footer">
          <el-button type="primary" link :icon="Plus" @click="openNewCategoryDialog()">新建一级文件夹</el-button>
        </div>
      </div>

      <!-- 右侧视频区 -->
      <div class="library-content">
        <div class="library-toolbar">
          <div class="toolbar-left">
            <el-checkbox
              v-model="selectAll"
              :indeterminate="isIndeterminate"
              :disabled="videos.length === 0"
              @change="handleSelectAll"
            >
              全选
            </el-checkbox>
          </div>
          <div class="toolbar-right">
            <el-input
              v-model="keyword"
              placeholder="搜索视频名称"
              clearable
              style="width: 200px"
              :prefix-icon="Search"
              @keyup.enter="handleSearch"
              @clear="handleSearch"
            />
            <el-button type="danger" :disabled="selectedIds.length === 0" @click="handleBatchDelete">
              批量删除
            </el-button>
            <el-button type="success" :icon="Upload" :disabled="!currentCategoryId" @click="openUploadDialog">
              上传视频
            </el-button>
            <el-button :icon="Refresh" @click="refreshAll">刷新</el-button>
          </div>
        </div>

        <div v-loading="loading" class="video-grid-container">
          <el-empty v-if="!loading && !currentCategoryId" description="请先选择左侧分类" />
          <el-empty v-else-if="!loading && videos.length === 0" description="暂无视频，请上传" />
          <div v-else class="video-grid">
            <div
              v-for="video in videos"
              :key="video.vodAssetId"
              class="video-card"
              :class="{ selected: selectedIds.includes(video.vodAssetId) }"
            >
              <div class="video-checkbox">
                <el-checkbox
                  :model-value="selectedIds.includes(video.vodAssetId)"
                  @change="toggleSelect(video.vodAssetId)"
                />
              </div>
              <div class="video-thumb" @click="handlePreview(video)">
                <el-image
                  v-if="video.vodCoverUrl"
                  :src="video.vodCoverUrl"
                  fit="cover"
                  class="thumb-img"
                  :hide-on-click-modal="true"
                >
                  <template #error>
                    <div class="thumb-placeholder">
                      <el-icon :size="32"><VideoCamera /></el-icon>
                    </div>
                  </template>
                  <template #placeholder>
                    <div class="thumb-placeholder">
                      <el-icon :size="24" class="is-loading"><Loading /></el-icon>
                    </div>
                  </template>
                </el-image>
                <div v-else class="thumb-placeholder">
                  <el-icon :size="32"><VideoCamera /></el-icon>
                </div>
                <div v-if="video.vodDuration" class="video-duration">{{ formatDuration(video.vodDuration) }}</div>
                <div class="play-overlay">
                  <el-icon :size="40"><VideoPlay /></el-icon>
                </div>
              </div>
              <div class="video-info">
                <span class="video-name" :title="video.vodName">{{ video.vodName }}</span>
                <div class="video-id-row" v-if="video.vodVideoId">
                  <span class="video-id" :title="video.vodVideoId">{{ video.vodVideoId }}</span>
                  <el-tag size="small" type="success" effect="plain">正常</el-tag>
                </div>
                <span class="video-meta">{{ formatSize(video.vodSize) }}</span>
              </div>
              <!-- 悬停底部操作：播放 / 复制链接 / 删除 -->
              <div class="video-actions" @click.stop>
                <button type="button" class="action-btn" title="播放" @click="handlePreview(video)">
                  <el-icon :size="16"><VideoPlay /></el-icon>
                </button>
                <button type="button" class="action-btn" title="复制播放地址" @click="handleCopyLink(video)">
                  <el-icon :size="16"><Link /></el-icon>
                </button>
                <button type="button" class="action-btn danger" title="删除" @click="handleDeleteSingle(video)">
                  <el-icon :size="16"><Delete /></el-icon>
                </button>
              </div>
            </div>
          </div>
        </div>

        <div class="library-pager" v-if="currentCategoryId">
          <el-pagination
            v-model:current-page="pageNum"
            v-model:page-size="pageSize"
            :total="total"
            :page-sizes="[20, 40, 60, 100]"
            layout="total, sizes, prev, pager, next"
            background
            @size-change="loadVideos"
            @current-change="loadVideos"
          />
        </div>
      </div>
    </div>

    <!-- 新建分类 -->
    <el-dialog
      v-model="showNewCategoryDialog"
      :title="newCategoryParentId ? '新建子分类' : '新建一级文件夹'"
      width="420px"
      destroy-on-close
      @closed="resetNewCategoryForm"
    >
      <el-form label-position="top" @submit.prevent>
        <el-form-item label="分类名称" required>
          <el-input
            v-model="newCategoryName"
            clearable
            placeholder="请输入分类名称"
            @keyup.enter="handleCreateCategory"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showNewCategoryDialog = false">取消</el-button>
        <el-button type="primary" @click="handleCreateCategory">创建</el-button>
      </template>
    </el-dialog>

    <!-- 上传视频 -->
    <el-dialog v-model="uploadVisible" title="上传视频" width="520px" destroy-on-close>
      <el-form label-width="90px" @submit.prevent>
        <el-form-item label="所属分类">
          <el-input :model-value="currentCategoryName" disabled />
        </el-form-item>
        <el-form-item label="选择视频" required>
          <el-upload
            ref="uploadRef"
            drag
            :auto-upload="false"
            :limit="1"
            accept="video/*"
            :on-change="handleFileChange"
            :on-exceed="() => ElMessage.warning('一次只能上传一个视频')"
          >
            <el-icon class="el-icon--upload"><UploadFilled /></el-icon>
            <div class="el-upload__text">将视频拖到此处，或<em>点击上传</em></div>
            <template #tip>
              <div class="el-upload__tip">支持 MP4、MOV 等格式</div>
            </template>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="uploadVisible = false">取消</el-button>
        <el-button type="primary" :loading="uploading" :disabled="!uploadFile" @click="handleUploadSubmit">
          {{ uploading ? "上传中..." : "开始上传" }}
        </el-button>
      </template>
    </el-dialog>

    <!-- 视频预览 -->
    <el-dialog
      v-model="showPreview"
      :title="previewVideo?.vodName || '视频预览'"
      width="800px"
      append-to-body
      destroy-on-close
      @closed="resetPreview"
    >
      <video
        v-if="previewUrl"
        :src="previewUrl"
        controls
        autoplay
        style="width: 100%; max-height: 500px"
        controlsList="nodownload"
      />
      <el-empty v-else description="暂无播放地址" />
    </el-dialog>
  </div>
</template>

<script setup name="courseVideo">
import { ref, computed, nextTick, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import {
  Folder,
  FolderOpened,
  Plus,
  Edit,
  Delete,
  Upload,
  Refresh,
  Search,
  Top,
  Bottom,
  VideoCamera,
  VideoPlay,
  UploadFilled,
  Loading,
  Link
} from "@element-plus/icons-vue";
import {
  getMediaVodCategoryTreeApi,
  addMediaVodCategoryApi,
  updateMediaVodCategoryApi,
  deleteMediaVodCategoryApi,
  getMediaVodPageApi,
  deleteMediaVodApi,
  uploadMediaVodApi,
  getMediaVodPlayUrlApi
} from "@/api/modules/media";

const treeLoading = ref(false);
const categoryTree = ref([]);
const flatCategories = ref([]);
const currentCategoryId = ref(null);
const currentCategoryName = ref("");

const loading = ref(false);
const videos = ref([]);
const selectedIds = ref([]);
const keyword = ref("");
const pageNum = ref(1);
const pageSize = ref(40);
const total = ref(0);

const editingCategoryId = ref(null);
const editingCategoryName = ref("");
const editInputRef = ref();

const showNewCategoryDialog = ref(false);
const newCategoryName = ref("");
const newCategoryParentId = ref(null);

const uploadVisible = ref(false);
const uploadFile = ref(null);
const uploading = ref(false);
const uploadRef = ref();

const showPreview = ref(false);
const previewVideo = ref(null);
const previewUrl = ref("");
const previewLoading = ref(false);

const selectAll = computed({
  get: () => videos.value.length > 0 && selectedIds.value.length === videos.value.length,
  set: () => {}
});
const isIndeterminate = computed(
  () => selectedIds.value.length > 0 && selectedIds.value.length < videos.value.length
);

const flattenTree = (nodes, depth = 0, list = []) => {
  (nodes || []).forEach(n => {
    list.push({ ...n, depth, children: undefined, _children: n.children || [] });
    if (n.children?.length) flattenTree(n.children, depth + 1, list);
  });
  return list;
};

const siblingsOf = cat => {
  return flatCategories.value.filter(c => c.parentId === cat.parentId);
};

const canMove = (cat, direction) => {
  const siblings = siblingsOf(cat);
  const idx = siblings.findIndex(c => c.categoryId === cat.categoryId);
  if (idx < 0) return false;
  return direction === "up" ? idx > 0 : idx < siblings.length - 1;
};

const formatSize = size => {
  if (!size && size !== 0) return "";
  if (size < 1024) return size + " B";
  if (size < 1024 * 1024) return (size / 1024).toFixed(1) + " KB";
  if (size < 1024 * 1024 * 1024) return (size / 1024 / 1024).toFixed(1) + " MB";
  return (size / 1024 / 1024 / 1024).toFixed(2) + " GB";
};

const formatDuration = duration => {
  if (!duration && duration !== 0) return "";
  const total = Math.floor(Number(duration));
  const minutes = Math.floor(total / 60);
  const seconds = total % 60;
  return `${minutes}:${String(seconds).padStart(2, "0")}`;
};

const loadCategoryTree = async preferCategoryId => {
  treeLoading.value = true;
  try {
    const res = await getMediaVodCategoryTreeApi({ loading: false });
    categoryTree.value = res.data || [];
    flatCategories.value = flattenTree(categoryTree.value);
    const preferId = preferCategoryId ?? currentCategoryId.value;
    let target = flatCategories.value.find(c => c.categoryId === preferId);
    if (!target && flatCategories.value.length) target = flatCategories.value[0];
    if (target) {
      currentCategoryId.value = target.categoryId;
      currentCategoryName.value = target.categoryName;
      await loadVideos();
    } else {
      currentCategoryId.value = null;
      currentCategoryName.value = "";
      videos.value = [];
      total.value = 0;
    }
  } catch (e) {
    console.error("加载分类失败:", e);
    categoryTree.value = [];
    flatCategories.value = [];
  } finally {
    treeLoading.value = false;
  }
};

const loadVideos = async () => {
  if (!currentCategoryId.value) {
    videos.value = [];
    total.value = 0;
    return;
  }
  loading.value = true;
  selectedIds.value = [];
  try {
    const res = await getMediaVodPageApi(
      {
        categoryId: currentCategoryId.value,
        vodName: keyword.value || undefined,
        pageNum: pageNum.value,
        pageSize: pageSize.value
      },
      { loading: false }
    );
    videos.value = res.data?.records || [];
    total.value = res.data?.total || 0;
  } catch (e) {
    console.error("加载视频失败:", e);
    videos.value = [];
    total.value = 0;
  } finally {
    loading.value = false;
  }
};

const selectCategory = cat => {
  if (editingCategoryId.value) return;
  currentCategoryId.value = cat.categoryId;
  currentCategoryName.value = cat.categoryName;
  pageNum.value = 1;
  keyword.value = "";
  loadVideos();
};

const handleSearch = () => {
  pageNum.value = 1;
  loadVideos();
};

const refreshAll = async () => {
  await loadCategoryTree(currentCategoryId.value);
};

const handleSelectAll = val => {
  selectedIds.value = val ? videos.value.map(v => v.vodAssetId) : [];
};

const toggleSelect = id => {
  const idx = selectedIds.value.indexOf(id);
  if (idx > -1) selectedIds.value.splice(idx, 1);
  else selectedIds.value.push(id);
};

const openNewCategoryDialog = (parentId = null) => {
  newCategoryName.value = "";
  newCategoryParentId.value = parentId;
  showNewCategoryDialog.value = true;
};

const resetNewCategoryForm = () => {
  newCategoryName.value = "";
  newCategoryParentId.value = null;
};

const handleCreateCategory = async () => {
  const name = newCategoryName.value.trim();
  if (!name) {
    ElMessage.warning("请输入分类名称");
    return;
  }
  const parentId = newCategoryParentId.value || 0;
  const siblings = flatCategories.value.filter(c => c.parentId === parentId);
  const maxOrder = siblings.reduce((m, c) => Math.max(m, c.orderNum || 0), 0);
  let ancestors = "0";
  if (parentId) {
    const parent = flatCategories.value.find(c => c.categoryId === parentId);
    ancestors = parent ? `${parent.ancestors || "0"},${parentId}` : `0,${parentId}`;
  }
  try {
    await addMediaVodCategoryApi({
      categoryName: name,
      parentId,
      ancestors,
      orderNum: maxOrder + 1,
      status: "0"
    });
    ElMessage.success("创建成功");
    showNewCategoryDialog.value = false;
    await loadCategoryTree(currentCategoryId.value);
  } catch (e) {
    // 全局拦截已提示
  }
};

const startEditCategory = async cat => {
  editingCategoryId.value = cat.categoryId;
  editingCategoryName.value = cat.categoryName;
  await nextTick();
  const el = Array.isArray(editInputRef.value) ? editInputRef.value[0] : editInputRef.value;
  el?.focus?.();
};

const handleSaveCategoryName = async () => {
  if (editingCategoryId.value == null) return;
  const name = editingCategoryName.value.trim();
  const id = editingCategoryId.value;
  if (!name) {
    editingCategoryId.value = null;
    return;
  }
  const old = flatCategories.value.find(c => c.categoryId === id);
  if (old && old.categoryName === name) {
    editingCategoryId.value = null;
    return;
  }
  try {
    await updateMediaVodCategoryApi({ categoryId: id, categoryName: name });
    ElMessage.success("重命名成功");
    if (currentCategoryId.value === id) currentCategoryName.value = name;
    editingCategoryId.value = null;
    await loadCategoryTree(currentCategoryId.value);
  } catch (e) {
    editingCategoryId.value = null;
  }
};

const handleMove = async (cat, direction) => {
  const siblings = siblingsOf(cat);
  const idx = siblings.findIndex(c => c.categoryId === cat.categoryId);
  const targetIdx = direction === "up" ? idx - 1 : idx + 1;
  if (targetIdx < 0 || targetIdx >= siblings.length) return;
  const target = siblings[targetIdx];
  const currentOrder = cat.orderNum ?? idx;
  const targetOrder = target.orderNum ?? targetIdx;
  try {
    await updateMediaVodCategoryApi({ categoryId: cat.categoryId, orderNum: targetOrder });
    await updateMediaVodCategoryApi({ categoryId: target.categoryId, orderNum: currentOrder });
    await loadCategoryTree(currentCategoryId.value);
  } catch (e) {
    // 全局拦截已提示
  }
};

const handleDeleteCategory = async cat => {
  try {
    await ElMessageBox.confirm(
      cat.videoCount > 0
        ? `确定删除分类「${cat.categoryName}」？该分类下约有 ${cat.videoCount} 个视频统计，请确认无子分类后再删。`
        : `确定删除分类「${cat.categoryName}」？`,
      "删除分类",
      { type: "warning", confirmButtonText: "确定", cancelButtonText: "取消" }
    );
    await deleteMediaVodCategoryApi(cat.categoryId);
    ElMessage.success("删除成功");
    if (currentCategoryId.value === cat.categoryId) {
      currentCategoryId.value = null;
      currentCategoryName.value = "";
    }
    await loadCategoryTree();
  } catch (e) {
    // cancel / 全局拦截
  }
};

const openUploadDialog = () => {
  if (!currentCategoryId.value) {
    ElMessage.warning("请先选择分类");
    return;
  }
  uploadFile.value = null;
  uploadVisible.value = true;
};

const handleFileChange = file => {
  uploadFile.value = file.raw;
};

const handleUploadSubmit = async () => {
  if (!uploadFile.value) {
    ElMessage.warning("请选择视频文件");
    return;
  }
  if (!currentCategoryId.value) {
    ElMessage.warning("请先选择分类");
    return;
  }
  uploading.value = true;
  try {
    const formData = new FormData();
    formData.append("file", uploadFile.value);
    formData.append("categoryId", currentCategoryId.value);
    await uploadMediaVodApi(formData);
    ElMessage.success("上传成功");
    uploadVisible.value = false;
    await loadCategoryTree(currentCategoryId.value);
  } catch (e) {
    // 上传接口后端若未完成，全局拦截会提示
  } finally {
    uploading.value = false;
  }
};

const handlePreview = async video => {
  if (!video?.vodAssetId) return;
  if (!video.vodVideoId) {
    ElMessage.warning("视频尚未就绪，请稍后再试");
    return;
  }
  if (previewLoading.value) return;
  previewLoading.value = true;
  try {
    const res = await getMediaVodPlayUrlApi(video.vodAssetId);
    previewUrl.value = res.data || "";
    previewVideo.value = video;
    showPreview.value = true;
    if (!previewUrl.value) {
      ElMessage.warning("暂无播放地址，视频可能仍在转码中");
    }
  } catch (e) {
    // 全局拦截已提示（如转码未完成）
  } finally {
    previewLoading.value = false;
  }
};

const handleCopyLink = async video => {
  if (!video?.vodAssetId) return;
  if (!video.vodVideoId) {
    ElMessage.warning("视频尚未就绪，请稍后再试");
    return;
  }
  try {
    const res = await getMediaVodPlayUrlApi(video.vodAssetId);
    const url = res.data || "";
    if (!url) {
      ElMessage.warning("暂无播放地址，视频可能仍在转码中");
      return;
    }
    await navigator.clipboard.writeText(url);
    ElMessage.success("播放地址已复制");
  } catch (e) {
    // 全局拦截已提示
  }
};

const resetPreview = () => {
  previewUrl.value = "";
  previewVideo.value = null;
};

const handleDeleteSingle = async video => {
  try {
    await ElMessageBox.confirm(`确定删除视频「${video.vodName}」？`, "提示", { type: "warning" });
    await deleteMediaVodApi(video.vodAssetId);
    ElMessage.success("删除成功");
    await loadCategoryTree(currentCategoryId.value);
  } catch (e) {
    // cancel
  }
};

const handleBatchDelete = async () => {
  if (!selectedIds.value.length) return;
  try {
    await ElMessageBox.confirm(`确定删除选中的 ${selectedIds.value.length} 个视频？`, "批量删除", {
      type: "warning"
    });
    await deleteMediaVodApi([...selectedIds.value]);
    ElMessage.success("删除成功");
    selectedIds.value = [];
    await loadCategoryTree(currentCategoryId.value);
  } catch (e) {
    // cancel
  }
};

onMounted(() => {
  loadCategoryTree();
});
</script>

<style scoped>
.media-vod-page {
  height: calc(100vh - 120px);
  min-height: 560px;
  background: #fff;
  border-radius: 8px;
  padding: 12px;
}

.library-container {
  display: flex;
  height: 100%;
  border: 1px solid var(--el-border-color-lighter);
  border-radius: 6px;
  overflow: hidden;
}

.library-sidebar {
  width: 280px;
  border-right: 1px solid var(--el-border-color-lighter);
  background: var(--el-fill-color-lighter);
  display: flex;
  flex-direction: column;
  flex-shrink: 0;
}

.sidebar-title {
  padding: 12px 16px;
  font-size: 13px;
  font-weight: 600;
  border-bottom: 1px solid var(--el-border-color-lighter);
}

.folder-list {
  flex: 1;
  overflow: auto;
  padding: 8px 4px;
}

.folder-item {
  display: flex;
  align-items: center;
  gap: 6px;
  height: 36px;
  margin: 1px 4px;
  padding-right: 6px;
  border-radius: 6px;
  cursor: pointer;
  min-width: 0;
}

.folder-item:hover,
.folder-item.active {
  background: var(--el-color-primary-light-9);
}

.folder-icon {
  flex-shrink: 0;
  color: var(--el-text-color-secondary);
}

.folder-name {
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  font-size: 13px;
}

.folder-edit-input {
  flex: 1;
}

.folder-count {
  font-size: 12px;
  color: var(--el-text-color-secondary);
  background: var(--el-fill-color);
  padding: 1px 6px;
  border-radius: 6px;
  flex-shrink: 0;
}

.folder-actions {
  display: flex;
  gap: 4px;
  opacity: 0;
  flex-shrink: 0;
}

.folder-item:hover .folder-actions,
.folder-item.active .folder-actions {
  opacity: 1;
}

.folder-action {
  cursor: pointer;
  color: var(--el-text-color-secondary);
  font-size: 14px;
}

.folder-action:hover {
  color: var(--el-color-primary);
}

.folder-action.danger:hover {
  color: var(--el-color-danger);
}

.folder-action.disabled {
  opacity: 0.35;
  cursor: not-allowed;
  pointer-events: none;
}

.sidebar-footer {
  padding: 10px 12px;
  border-top: 1px solid var(--el-border-color-lighter);
}

.library-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
  overflow: hidden;
}

.library-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  border-bottom: 1px solid var(--el-border-color-lighter);
  flex-wrap: wrap;
}

.toolbar-left,
.toolbar-right {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.video-grid-container {
  flex: 1;
  overflow: auto;
  padding: 16px;
}

.video-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
  gap: 14px;
}

.video-card {
  position: relative;
  border-radius: 8px;
  overflow: hidden;
  border: 2px solid transparent;
  background: var(--el-fill-color-lighter);
  transition: border-color 0.2s;
}

.video-card:hover {
  border-color: var(--el-color-primary-light-3);
}

.video-card.selected {
  border-color: var(--el-color-primary);
}

.video-checkbox {
  position: absolute;
  top: 8px;
  left: 8px;
  z-index: 2;
}

.video-thumb {
  position: relative;
  aspect-ratio: 16 / 9;
  background: #eef0f3;
  cursor: pointer;
}

.play-overlay {
  position: absolute;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  background: rgba(0, 0, 0, 0.35);
  opacity: 0;
  transition: opacity 0.2s;
}

.video-thumb:hover .play-overlay {
  opacity: 1;
}

.thumb-img {
  width: 100%;
  height: 100%;
}

.thumb-img :deep(.el-image__inner),
.thumb-img :deep(.el-image__error),
.thumb-img :deep(.el-image__placeholder),
.thumb-img :deep(.el-image__wrapper) {
  width: 100%;
  height: 100%;
}

.thumb-placeholder {
  width: 100%;
  height: 100%;
  min-height: 100px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #c0c4cc;
  background: #eef0f3;
}

.video-duration {
  position: absolute;
  right: 6px;
  bottom: 6px;
  background: rgba(0, 0, 0, 0.7);
  color: #fff;
  font-size: 12px;
  padding: 1px 6px;
  border-radius: 4px;
}

.video-info {
  padding: 8px 10px 10px;
}

.video-name {
  display: block;
  font-size: 13px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.video-id-row {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-top: 4px;
  min-width: 0;
}

.video-id {
  flex: 1;
  min-width: 0;
  font-size: 11px;
  color: var(--el-text-color-secondary);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.video-meta {
  display: block;
  margin-top: 4px;
  font-size: 12px;
  color: var(--el-text-color-secondary);
}

.video-actions {
  position: absolute;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 3;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  padding: 12px 10px 10px;
  background: linear-gradient(transparent, rgba(0, 0, 0, 0.55));
  opacity: 0;
  pointer-events: none;
  transition: opacity 0.2s;
}

.video-card:hover .video-actions {
  opacity: 1;
  pointer-events: auto;
}

.action-btn {
  width: 34px;
  height: 34px;
  border: none;
  border-radius: 50%;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  color: #303133;
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.18);
  transition: transform 0.15s, background 0.15s;
}

.action-btn:hover {
  transform: scale(1.06);
  background: #f5f7fa;
}

.action-btn.danger {
  color: #fff;
  background: var(--el-color-danger);
}

.action-btn.danger:hover {
  background: var(--el-color-danger-light-3);
  color: #fff;
}

.library-pager {
  padding: 10px 16px;
  border-top: 1px solid var(--el-border-color-lighter);
  display: flex;
  justify-content: flex-end;
}
</style>
