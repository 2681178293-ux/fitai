<template>
  <div class="image-library-wrapper">
    <!-- 预览区域 + 操作按钮 -->
    <div class="preview-area">
      <div v-if="modelValue" class="current-image" :style="{ width, height }">
        <el-image :src="modelValue" fit="cover" class="image-preview" />
        <div class="image-actions">
          <el-icon class="action-icon" @click="handleClear"><Delete /></el-icon>
        </div>
      </div>
      <div v-else class="upload-placeholder" :style="{ width, height }">
        <el-icon class="placeholder-icon"><Picture /></el-icon>
        <span class="placeholder-text">暂无图片</span>
      </div>
      <div class="action-buttons">
        <el-button type="primary" @click="openLibrary">
          <el-icon><FolderOpened /></el-icon>
          图片库
        </el-button>
      </div>
    </div>

    <!-- 图片库弹窗 -->
    <el-dialog
      v-model="showLibrary"
      title="图片库"
      width="1000px"
      destroy-on-close
      class="image-library-dialog"
    >
      <div class="library-container">
        <!-- 左侧分类 -->
        <div class="library-sidebar">
          <div class="sidebar-title">分类</div>
          <div class="folder-list">
            <div
              v-for="folder in folders"
              :key="folder.key"
              class="folder-item"
              :class="{ active: currentFolder === folder.key }"
              @click="editingFolderKey === folder.key ? null : selectFolder(folder.key)"
            >
              <el-icon class="folder-icon">
                <Folder v-if="currentFolder !== folder.key" />
                <FolderOpened v-else />
              </el-icon>
              <el-input
                v-if="editingFolderKey === folder.key"
                ref="folderEditInputRef"
                v-model="editingFolderName"
                size="small"
                class="folder-edit-input"
                @keyup.enter="handleSaveFolderName"
                @blur="handleSaveFolderName"
              />
              <span v-else class="folder-name">{{ folder.label }}</span>
              <span class="folder-count">{{ folder.count }}</span>
              <div class="folder-actions" :class="{ 'folder-actions-hidden': folder.key === '' }" @click.stop>
                <el-icon v-if="editingFolderKey !== folder.key" class="folder-action" @click="handleEditFolder(folder)">
                  <Edit />
                </el-icon>
                <el-icon v-else class="folder-action" @click="handleCancelEdit"><Close /></el-icon>
                <el-icon class="folder-action folder-action-delete" @click="handleDeleteFolder(folder)">
                  <Delete />
                </el-icon>
              </div>
            </div>
          </div>
          <div class="sidebar-footer">
            <el-button type="primary" link :icon="Plus" @click="openNewFolderDialog">
              新建文件夹
            </el-button>
          </div>
        </div>

        <!-- 右侧内容 -->
        <div class="library-content">
          <!-- 工具栏 -->
          <div class="library-toolbar">
            <div class="toolbar-left">
              <el-checkbox
                v-model="selectAll"
                :indeterminate="isIndeterminate"
                @change="handleSelectAll"
              >
                全选
              </el-checkbox>
              <el-button
                v-if="selectedUrls.length > 0"
                type="danger"
                size="small"
                @click="handleBatchDelete"
              >
                <el-icon><Delete /></el-icon>
                删除 ({{ selectedUrls.length }})
              </el-button>
            </div>
            <div class="toolbar-right">
              <el-upload
                :action="libraryUploadUrl"
                :headers="uploadHeaders"
                :data="uploadData"
                :show-file-list="false"
                :on-success="handleUploadSuccess"
                :on-error="handleUploadError"
                :before-upload="beforeUpload"
                accept="image/*"
                multiple
              >
                <el-button type="success">
                  <el-icon><Upload /></el-icon>
                  上传图片
                </el-button>
              </el-upload>
              <el-button :icon="Refresh" @click="loadLibrary">刷新</el-button>
            </div>
          </div>

          <!-- 图片网格 -->
          <div v-loading="loading" class="image-grid-container">
            <el-empty v-if="!loading && images.length === 0" description="暂无图片，请上传" />
            <div v-else class="image-grid">
              <div
                v-for="img in images"
                :key="img.url"
                class="image-card"
                :class="[
                  { selected: multiple ? selectedUrls.includes(img.url) : pickUrl === img.url },
                  { 'image-card-banner': currentFolder === 'banner' }
                ]"
              >
                <div class="image-checkbox">
                  <el-checkbox
                    :model-value="selectedUrls.includes(img.url)"
                    @change="toggleSelect(img.url)"
                  />
                </div>
                <el-image
                  :src="img.url"
                  :fit="currentFolder === 'banner' ? 'contain' : 'cover'"
                  class="grid-image"
                  :preview-src-list="previewList"
                  :initial-index="images.findIndex(i => i.url === img.url)"
                  @click.stop
                />
                <div class="image-info">
                  <span class="image-name" :title="img.name">{{ img.name }}</span>
                </div>
                <div class="image-overlay" @click="handleImageClick(img)">
                  <el-icon v-if="!multiple && pickUrl === img.url" class="check-icon"><Check /></el-icon>
                  <span v-else class="pick-text">点击选择</span>
                </div>
                <el-button
                  class="delete-btn"
                  type="danger"
                  size="small"
                  circle
                  :icon="Delete"
                  @click.stop="handleDeleteSingle(img.url)"
                />
              </div>
            </div>
          </div>
        </div>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <span class="selected-info" v-if="pickUrl">
            已选择 1 张图片
          </span>
          <el-button @click="showLibrary = false">取消</el-button>
          <el-button type="primary" :disabled="!pickUrl" @click="handleConfirm">
            确定选择
          </el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 新建分类：独立模态框，避免侧栏过窄挤压表单 -->
    <el-dialog
      v-model="showNewFolderDialog"
      title="新建分类"
      width="440px"
      append-to-body
      destroy-on-close
      class="new-folder-dialog"
      @closed="resetNewFolderForm"
    >
      <el-form label-position="top" class="new-folder-form" @submit.prevent>
        <el-form-item label="存储路径" required>
          <el-input
            v-model="newFolderKey"
            clearable
            placeholder="例如 brand/logos"
            @keyup.enter="handleCreateFolder"
          />
          <p class="form-item-hint">对应 MinIO 目录前缀，仅英文、数字、/、_、-，勿含空格</p>
        </el-form-item>
        <el-form-item label="显示名称" required>
          <el-input
            v-model="newFolderLabel"
            clearable
            placeholder="侧栏展示用，可用中文"
            @keyup.enter="handleCreateFolder"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="cancelCreateFolder">取消</el-button>
        <el-button type="primary" @click="handleCreateFolder">创建</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, watch, nextTick } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { Picture, Delete, FolderOpened, Upload, Check, Folder, Refresh, Plus, Edit, Close } from "@element-plus/icons-vue";
import { useUserStore } from "@/stores/modules/user";
import { getImageLibrary, getImageFolders, addFolderApi, deleteFile, deleteFileBatch, renameFolderApi, deleteFolderApi } from "@/api/modules/upload";

const props = defineProps({
  modelValue: {
    type: String,
    default: ""
  },
  // 默认文件夹
  folder: {
    type: String,
    default: ""
  },
  // 上传接口
  uploadAction: {
    type: String,
    default: "/file/upload/image"
  },
  // 是否多选
  multiple: {
    type: Boolean,
    default: false
  },
  // 预览尺寸
  width: {
    type: String,
    default: "200px"
  },
  height: {
    type: String,
    default: "120px"
  }
});

const emit = defineEmits(["update:modelValue"]);

const userStore = useUserStore();
// 图片库内上传使用专用接口，支持指定文件夹
const libraryUploadUrl = computed(() => import.meta.env.VITE_API_URL + "/file/upload/library");
const uploadHeaders = computed(() => ({
  Authorization: userStore.token
}));
// 上传时携带当前文件夹参数
const uploadData = computed(() => ({
  folder: currentFolder.value || props.folder || "images"
}));

// 状态
const showLibrary = ref(false);
const loading = ref(false);
const folders = ref([]);
const images = ref([]);
const currentFolder = ref("");
const selectedUrls = ref([]);  // 用于批量删除
const pickUrl = ref("");       // 用于选择图片

// 新建文件夹（folder_key 对应 MinIO 前缀，label 为侧边栏展示名）
const showNewFolderDialog = ref(false);
const newFolderKey = ref("");
const newFolderLabel = ref("");

const openNewFolderDialog = () => {
  newFolderKey.value = "";
  newFolderLabel.value = "";
  showNewFolderDialog.value = true;
};

const resetNewFolderForm = () => {
  newFolderKey.value = "";
  newFolderLabel.value = "";
};

/** 去掉首尾斜杠，避免 MinIO 路径异常 */
const normalizeFolderKey = (raw) => {
  if (!raw || typeof raw !== "string") return "";
  return raw.trim().replace(/^\/+|\/+$/g, "");
};

const isValidStorageFolderKey = (key) => {
  if (!key) return false;
  if (key.includes(" ")) return false;
  return /^[a-zA-Z0-9/_-]+$/.test(key);
};

// 编辑分类
const editingFolderKey = ref(null);
const editingFolderName = ref("");
const folderEditInputRef = ref();

// 计算属性
const previewList = computed(() => images.value.map(img => img.url));
const selectAll = computed({
  get: () => selectedUrls.value.length === images.value.length && images.value.length > 0,
  set: () => {}
});
const isIndeterminate = computed(() => 
  selectedUrls.value.length > 0 && selectedUrls.value.length < images.value.length
);

// 打开图片库
const openLibrary = () => {
  showLibrary.value = true;
  currentFolder.value = props.folder || "";
  pickUrl.value = props.modelValue || "";
  selectedUrls.value = [];
  loadFolders();
  loadLibrary();
};

// 加载分类
const loadFolders = async () => {
  try {
    const res = await getImageFolders();
    folders.value = res.data || [];
  } catch (error) {
    console.error("加载分类失败:", error);
    folders.value = [{ key: "", label: "全部", count: 0 }];
  }
};

// 加载图片
const loadLibrary = async () => {
  loading.value = true;
  try {
    const res = await getImageLibrary(currentFolder.value, 200);
    images.value = res.data || [];
  } catch (error) {
    console.error("加载图片库失败:", error);
    images.value = [];
  } finally {
    loading.value = false;
  }
};

// 选择分类
const selectFolder = (key) => {
  currentFolder.value = key;
  selectedUrls.value = [];
  loadLibrary();
};

// 新建文件夹（调用后端持久化到 file_folder 表）
const handleCreateFolder = async () => {
  const folderKey = normalizeFolderKey(newFolderKey.value);
  const label = (newFolderLabel.value || "").trim();
  if (!folderKey || !label) {
    ElMessage.warning("请填写存储路径和显示名称");
    return;
  }
  if (!isValidStorageFolderKey(folderKey)) {
    ElMessage.warning("存储路径仅允许英文、数字、/、_、-，且不能含空格");
    return;
  }

  const exists = folders.value.some(f => f.key === folderKey || f.label === label);
  if (exists) {
    ElMessage.warning("同路径或同名称的分类已存在");
    return;
  }

  try {
    await addFolderApi(folderKey, label);
    await loadFolders();
    currentFolder.value = folderKey;
    selectedUrls.value = [];
    loadLibrary();
    showNewFolderDialog.value = false;
    ElMessage.success("文件夹已创建");
  } catch (error) {
    const msg = error?.response?.data?.msg || error?.message || "创建失败";
    ElMessage.error(msg);
  }
};

const cancelCreateFolder = () => {
  showNewFolderDialog.value = false;
};

// 编辑分类
const handleEditFolder = async folder => {
  editingFolderKey.value = folder.key;
  editingFolderName.value = folder.label;
  await nextTick();
  const el = Array.isArray(folderEditInputRef.value) ? folderEditInputRef.value[0] : folderEditInputRef.value;
  el?.focus?.();
};

// 保存分类名称
const handleSaveFolderName = async () => {
  if (editingFolderKey.value == null) return;
  const name = editingFolderName.value.trim();
  if (!name) {
    handleCancelEdit();
    return;
  }
  if (name === folders.value.find(f => f.key === editingFolderKey.value)?.label) {
    handleCancelEdit();
    return;
  }
  try {
    await renameFolderApi(editingFolderKey.value, name);
    ElMessage.success("重命名成功");
    loadFolders();
    handleCancelEdit();
  } catch (error) {
    ElMessage.error(error.message || "重命名失败");
  }
};

// 取消编辑
const handleCancelEdit = () => {
  editingFolderKey.value = null;
  editingFolderName.value = "";
};

// 删除分类
const handleDeleteFolder = async folder => {
  if (folder.key === "") return;
  try {
    await ElMessageBox.confirm(
      folder.count > 0
        ? `确定删除分类「${folder.label}」？该分类下有 ${folder.count} 张图片，删除后无法恢复`
        : `确定删除分类「${folder.label}」？`,
      "删除分类",
      {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }
    );
    await deleteFolderApi(folder.key);
    ElMessage.success("删除成功");
    if (currentFolder.value === folder.key) {
      currentFolder.value = "";
    }
    loadFolders();
    loadLibrary();
  } catch (error) {
    if (error !== "cancel") {
      ElMessage.error(error.message || "删除失败");
    }
  }
};

// 点击图片选择
const handleImageClick = (img) => {
  pickUrl.value = img.url;
};

// 全选/取消全选
const handleSelectAll = (val) => {
  if (val) {
    selectedUrls.value = images.value.map(img => img.url);
  } else {
    selectedUrls.value = [];
  }
};

// 切换选择
const toggleSelect = (url) => {
  const index = selectedUrls.value.indexOf(url);
  if (index > -1) {
    selectedUrls.value.splice(index, 1);
  } else {
    selectedUrls.value.push(url);
  }
};

// 确认选择
const handleConfirm = () => {
  emit("update:modelValue", pickUrl.value);
  showLibrary.value = false;
  ElMessage.success("已选择图片");
};

// 清除图片
const handleClear = () => {
  emit("update:modelValue", "");
};

// 批量上传：el-upload 对每个文件各触发一次 success，合并为一条提示、一次刷新
const uploadPendingCount = ref(0);
const uploadBatchOk = ref(0);
const uploadBatchFail = ref(0);

// 上传前校验
const beforeUpload = (file) => {
  const isImage = file.type.startsWith("image/");
  const isLt5M = file.size / 1024 / 1024 < 5;

  if (!isImage) {
    ElMessage.error("只能上传图片文件");
    return false;
  }
  if (!isLt5M) {
    ElMessage.error("图片大小不能超过 5MB");
    return false;
  }
  if (uploadPendingCount.value === 0) {
    uploadBatchOk.value = 0;
    uploadBatchFail.value = 0;
  }
  uploadPendingCount.value++;
  return true;
};

const finishUploadBatch = () => {
  loadLibrary();
  loadFolders();
  if (uploadBatchFail.value === 0) {
    ElMessage.success(
      uploadBatchOk.value <= 1 ? "上传成功" : `成功上传 ${uploadBatchOk.value} 张图片`
    );
  } else if (uploadBatchOk.value === 0) {
    ElMessage.error("上传失败，请重试");
  } else {
    ElMessage.warning(`上传完成：成功 ${uploadBatchOk.value} 张，失败 ${uploadBatchFail.value} 张`);
  }
};

// 上传成功
const handleUploadSuccess = (response) => {
  uploadPendingCount.value--;
  if (uploadPendingCount.value < 0) uploadPendingCount.value = 0;

  if (response.code === 200) {
    uploadBatchOk.value++;
  } else {
    uploadBatchFail.value++;
  }

  if (uploadPendingCount.value === 0) {
    finishUploadBatch();
  }
};

// 上传失败
const handleUploadError = () => {
  uploadPendingCount.value--;
  if (uploadPendingCount.value < 0) uploadPendingCount.value = 0;
  uploadBatchFail.value++;

  if (uploadPendingCount.value === 0) {
    finishUploadBatch();
  }
};

// 单个删除
const handleDeleteSingle = async (url) => {
  try {
    await ElMessageBox.confirm("确定删除此图片？", "提示", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning"
    });
    await deleteFile(url);
    ElMessage.success("删除成功");
    loadLibrary();
    loadFolders();
    // 如果删除的是当前选中的图片，清空选择
    if (pickUrl.value === url) {
      pickUrl.value = "";
    }
  } catch (error) {
    if (error !== "cancel") {
      ElMessage.error("删除失败");
    }
  }
};

// 外部调用打开图片库（不绑定 v-model 时使用）
const open = () => {
  showLibrary.value = true;
  currentFolder.value = props.folder || "";
  pickUrl.value = "";
  selectedUrls.value = [];
  loadFolders();
  loadLibrary();
};

// 暴露方法
defineExpose({ open });

// 批量删除
const handleBatchDelete = async () => {
  if (selectedUrls.value.length === 0) return;
  
  try {
    await ElMessageBox.confirm(
      `确定删除选中的 ${selectedUrls.value.length} 张图片？`,
      "批量删除",
      {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }
    );
    await deleteFileBatch(selectedUrls.value);
    ElMessage.success(`成功删除 ${selectedUrls.value.length} 张图片`);
    selectedUrls.value = [];
    loadLibrary();
    loadFolders();
  } catch (error) {
    if (error !== "cancel") {
      ElMessage.error("批量删除失败");
    }
  }
};
</script>

<style scoped>
.image-library-wrapper {
  display: inline-block;
}

.preview-area {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.current-image {
  position: relative;
  border-radius: 6px;
  overflow: hidden;
  border: 1px solid var(--el-border-color);
}

.image-preview {
  width: 100%;
  height: 100%;
}

.image-actions {
  position: absolute;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(0, 0, 0, 0.5);
  opacity: 0;
  transition: opacity 0.3s;
}

.current-image:hover .image-actions {
  opacity: 1;
}

.action-icon {
  color: white;
  font-size: 24px;
  cursor: pointer;
}

.upload-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8px;
  border: 1px dashed var(--el-border-color);
  border-radius: 6px;
  background: var(--el-fill-color-lighter);
}

.placeholder-icon {
  font-size: 32px;
  color: var(--el-text-color-placeholder);
}

.placeholder-text {
  font-size: 12px;
  color: var(--el-text-color-placeholder);
}

.action-buttons {
  display: flex;
  align-items: center;
  gap: 8px;
}

/* 弹窗样式 */
.library-container {
  display: flex;
  height: 550px;
  border: 1px solid var(--el-border-color-lighter);
  border-radius: 4px;
  overflow: hidden;
}

.library-sidebar {
  width: 220px;
  border-right: 1px solid var(--el-border-color-lighter);
  background: var(--el-fill-color-lighter);
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
}

.sidebar-title {
  padding: 12px 16px;
  font-size: 13px;
  font-weight: 600;
  color: var(--el-text-color-regular);
  border-bottom: 1px solid var(--el-border-color-lighter);
}

.folder-list {
  padding: 8px 0;
  flex: 1;
  overflow-y: auto;
}

.sidebar-footer {
  padding: 12px;
  border-top: 1px solid var(--el-border-color-lighter);
}

.new-folder-form .form-item-hint {
  margin: 0;
  margin-top: 6px;
  font-size: 12px;
  line-height: 1.45;
  color: var(--el-text-color-secondary);
}

.folder-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 12px;
  margin: 0 6px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
}

.folder-item:hover {
  background: var(--el-fill-color);
}

.folder-item.active {
  background: var(--el-color-primary-light-9);
  color: var(--el-color-primary);
}

.folder-item.active .folder-icon {
  color: var(--el-color-primary);
}

.folder-item.active .folder-count {
  background: var(--el-color-primary-light-8);
  color: var(--el-color-primary);
  font-weight: 500;
}

.folder-name {
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.folder-edit-input {
  flex: 1;
}

.folder-edit-input :deep(.el-input__wrapper) {
  padding: 2px 8px;
}

.folder-actions {
  display: flex;
  gap: 4px;
  opacity: 0;
  transition: opacity 0.2s;
  min-width: 36px;
}

.folder-actions-hidden {
  visibility: hidden;
}

.folder-item:hover .folder-actions:not(.folder-actions-hidden) {
  opacity: 1;
}

.folder-action {
  font-size: 14px;
  cursor: pointer;
  color: var(--el-text-color-secondary);
}

.folder-action:hover {
  color: var(--el-color-primary);
}

.folder-action-delete:hover {
  color: var(--el-color-danger);
}

.folder-count {
  font-size: 12px;
  color: var(--el-text-color-secondary);
  background: var(--el-fill-color);
  padding: 2px 6px;
  border-radius: 6px;
  min-width: 22px;
  text-align: center;
  flex-shrink: 0;
}

.library-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.library-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  border-bottom: 1px solid var(--el-border-color-lighter);
  background: #fff;
}

.toolbar-left,
.toolbar-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.image-grid-container {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
}

.image-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 12px;
}

.image-card {
  position: relative;
  aspect-ratio: 1;
  border-radius: 6px;
  overflow: hidden;
  border: 2px solid transparent;
  transition: all 0.2s;
  background: var(--el-fill-color-lighter);
}

/* banner 文件夹下使用横版比例，完整展示宽幅图 */
.image-card-banner {
  aspect-ratio: 16 / 9;
}

.image-card:hover {
  border-color: var(--el-color-primary-light-3);
}

.image-card.selected {
  border-color: var(--el-color-primary);
  box-shadow: 0 0 0 2px var(--el-color-primary-light-5);
}

.image-card.selected .image-overlay {
  opacity: 1;
}

.image-checkbox {
  position: absolute;
  top: 6px;
  left: 6px;
  z-index: 10;
}

.grid-image {
  width: 100%;
  height: 100%;
}

.image-info {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 4px 8px;
  background: linear-gradient(transparent, rgba(0, 0, 0, 0.7));
}

.image-name {
  font-size: 11px;
  color: white;
  display: block;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.image-overlay {
  position: absolute;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(0, 0, 0, 0.4);
  opacity: 0;
  transition: opacity 0.2s;
  cursor: pointer;
}

.image-card:hover .image-overlay {
  opacity: 1;
}

.check-icon {
  width: 40px;
  height: 40px;
  background: var(--el-color-primary);
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
}

.pick-text {
  color: white;
  font-size: 13px;
  background: rgba(0, 0, 0, 0.5);
  padding: 6px 12px;
  border-radius: 4px;
}

.delete-btn {
  position: absolute;
  top: 6px;
  right: 6px;
  opacity: 0;
  transition: opacity 0.2s;
}

.image-card:hover .delete-btn {
  opacity: 1;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 12px;
}

.selected-info {
  color: var(--el-color-primary);
  font-size: 13px;
  margin-right: auto;
}
</style>
