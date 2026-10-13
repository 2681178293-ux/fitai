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
              v-for="cat in categories"
              :key="cat.categoryId"
              class="folder-item"
              :class="{ active: currentCategoryId === cat.categoryId }"
              :style="{ paddingLeft: 12 + cat.depth * 12 + 'px' }"
              @click="editingCategoryId === cat.categoryId ? null : selectCategory(cat.categoryId)"
            >
              <el-icon class="folder-icon">
                <Folder v-if="currentCategoryId !== cat.categoryId" />
                <FolderOpened v-else />
              </el-icon>
              <el-input
                v-if="editingCategoryId === cat.categoryId"
                ref="folderEditInputRef"
                v-model="editingCategoryName"
                size="small"
                class="folder-edit-input"
                @keyup.enter="handleSaveCategoryName"
                @blur="handleSaveCategoryName"
              />
              <span v-else class="folder-name">{{ cat.categoryName }}</span>
              <span class="folder-count">{{ cat.imageCount || 0 }}</span>
              <div class="folder-actions" @click.stop>
                <el-icon
                  v-if="editingCategoryId !== cat.categoryId"
                  class="folder-action"
                  @click="handleEditCategory(cat)"
                >
                  <Edit />
                </el-icon>
                <el-icon v-else class="folder-action" @click="handleCancelEdit"><Close /></el-icon>
                <el-icon class="folder-action folder-action-delete" @click="handleDeleteCategory(cat)">
                  <Delete />
                </el-icon>
              </div>
            </div>
          </div>
          <div class="sidebar-footer">
            <el-button type="primary" link :icon="Plus" @click="openNewCategoryDialog">
              新建分类
            </el-button>
          </div>
        </div>

        <!-- 右侧内容 -->
        <div class="library-content">
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
                v-if="selectedIds.length > 0"
                type="danger"
                size="small"
                @click="handleBatchDelete"
              >
                <el-icon><Delete /></el-icon>
                删除 ({{ selectedIds.length }})
              </el-button>
            </div>
            <div class="toolbar-right">
              <el-upload
                :action="libraryUploadUrl"
                :headers="uploadHeaders"
                :data="uploadData"
                :show-file-list="false"
                :disabled="!currentCategoryId"
                :on-success="handleUploadSuccess"
                :on-error="handleUploadError"
                :before-upload="beforeUpload"
                accept="image/*"
                multiple
              >
                <el-button type="success" :disabled="!currentCategoryId">
                  <el-icon><Upload /></el-icon>
                  上传图片
                </el-button>
              </el-upload>
              <el-button :icon="Refresh" @click="loadLibrary">刷新</el-button>
            </div>
          </div>

          <div v-loading="loading" class="image-grid-container">
            <el-empty v-if="!loading && !currentCategoryId" description="请先选择分类" />
            <el-empty v-else-if="!loading && images.length === 0" description="暂无图片，请上传" />
            <div v-else class="image-grid">
              <div
                v-for="img in images"
                :key="img.ossId"
                class="image-card"
                :class="[
                  { selected: multiple ? selectedIds.includes(img.ossId) : pickUrl === img.ossUrl },
                  { 'image-card-banner': isBannerCategory }
                ]"
              >
                <div class="image-checkbox">
                  <el-checkbox
                    :model-value="selectedIds.includes(img.ossId)"
                    @change="toggleSelect(img.ossId)"
                  />
                </div>
                <el-image
                  :src="img.ossUrl"
                  :fit="isBannerCategory ? 'contain' : 'cover'"
                  class="grid-image"
                  :preview-src-list="previewList"
                  :initial-index="images.findIndex(i => i.ossId === img.ossId)"
                  @click.stop
                />
                <div class="image-info">
                  <span class="image-name" :title="img.ossName">{{ img.ossName }}</span>
                </div>
                <div class="image-overlay" @click="handleImageClick(img)">
                  <el-icon v-if="!multiple && pickUrl === img.ossUrl" class="check-icon"><Check /></el-icon>
                  <span v-else class="pick-text">点击选择</span>
                </div>
                <el-button
                  class="delete-btn"
                  type="danger"
                  size="small"
                  circle
                  :icon="Delete"
                  @click.stop="handleDeleteSingle(img)"
                />
              </div>
            </div>
          </div>
        </div>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <span class="selected-info" v-if="pickUrl">已选择 1 张图片</span>
          <el-button @click="showLibrary = false">取消</el-button>
          <el-button type="primary" :disabled="!pickUrl" @click="handleConfirm">确定选择</el-button>
        </div>
      </template>
    </el-dialog>

    <el-dialog
      v-model="showNewCategoryDialog"
      title="新建分类"
      width="440px"
      append-to-body
      destroy-on-close
      class="new-folder-dialog"
      @closed="resetNewCategoryForm"
    >
      <el-form label-position="top" class="new-folder-form" @submit.prevent>
        <el-form-item label="分类名称" required>
          <el-input
            v-model="newCategoryName"
            clearable
            placeholder="侧栏展示用，可用中文"
            @keyup.enter="handleCreateCategory"
          />
        </el-form-item>
        <el-form-item label="父分类">
          <el-select v-model="newCategoryParentId" clearable placeholder="不选则为顶级分类" style="width: 100%">
            <el-option
              v-for="cat in categories"
              :key="cat.categoryId"
              :label="cat.categoryName"
              :value="cat.categoryId"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="cancelCreateCategory">取消</el-button>
        <el-button type="primary" @click="handleCreateCategory">创建</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, nextTick } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { Picture, Delete, FolderOpened, Upload, Check, Folder, Refresh, Plus, Edit, Close } from "@element-plus/icons-vue";
import { useUserStore } from "@/stores/modules/user";
import {
  getMediaOssPageApi,
  getMediaOssCategoryTreeApi,
  addMediaOssCategoryApi,
  updateMediaOssCategoryNameApi,
  deleteMediaOssCategoryApi,
  deleteMediaOssApi
} from "@/api/modules/media";

/** folder 业务别名 → 优先匹配的分类名 */
const FOLDER_HINT_MAP = {
  course: ["课程封面", "课程图片", "课时封面"],
  exercise: ["动作图片"],
  product: ["商品封面", "商品图片", "商品相关"],
  banner: ["轮播图片"],
  seckill: ["秒杀商品"],
  coach: ["人物头像"],
  level: ["人物头像"],
  benefit: ["人物头像"],
  badge: ["人物头像"]
};

const props = defineProps({
  modelValue: { type: String, default: "" },
  /** 业务别名，用于打开时定位默认分类 */
  folder: { type: String, default: "" },
  uploadAction: { type: String, default: "/file/upload/image" },
  multiple: { type: Boolean, default: false },
  width: { type: String, default: "200px" },
  height: { type: String, default: "120px" }
});

const emit = defineEmits(["update:modelValue"]);
const userStore = useUserStore();

const libraryUploadUrl = computed(() => import.meta.env.VITE_API_URL + "/admin/media/oss/upload");
const uploadHeaders = computed(() => ({ Authorization: userStore.token }));
const uploadData = computed(() => ({ categoryId: currentCategoryId.value }));

const showLibrary = ref(false);
const loading = ref(false);
const categories = ref([]);
const images = ref([]);
const currentCategoryId = ref(null);
const selectedIds = ref([]);
const pickUrl = ref("");

const showNewCategoryDialog = ref(false);
const newCategoryName = ref("");
const newCategoryParentId = ref(null);

const editingCategoryId = ref(null);
const editingCategoryName = ref("");
const folderEditInputRef = ref();

const previewList = computed(() => images.value.map(img => img.ossUrl));
const isBannerCategory = computed(() => {
  const cat = categories.value.find(c => c.categoryId === currentCategoryId.value);
  return cat?.categoryName?.includes("轮播");
});
const selectAll = computed({
  get: () => selectedIds.value.length === images.value.length && images.value.length > 0,
  set: () => {}
});
const isIndeterminate = computed(
  () => selectedIds.value.length > 0 && selectedIds.value.length < images.value.length
);

const flattenTree = (nodes, depth = 0, list = []) => {
  (nodes || []).forEach(n => {
    list.push({ ...n, depth, children: undefined });
    if (n.children?.length) flattenTree(n.children, depth + 1, list);
  });
  return list;
};

const resolveDefaultCategoryId = flat => {
  if (!flat.length) return null;
  const hints = FOLDER_HINT_MAP[props.folder] || [];
  for (const hint of hints) {
    const found = flat.find(c => c.categoryName === hint);
    if (found) return found.categoryId;
  }
  for (const hint of hints) {
    const found = flat.find(c => c.categoryName?.includes(hint) || hint.includes(c.categoryName));
    if (found) return found.categoryId;
  }
  if (props.folder) {
    const byName = flat.find(c => c.categoryName?.toLowerCase().includes(props.folder.toLowerCase()));
    if (byName) return byName.categoryId;
  }
  return flat[0].categoryId;
};

const openLibrary = () => {
  showLibrary.value = true;
  pickUrl.value = props.modelValue || "";
  selectedIds.value = [];
  loadCategories().then(() => loadLibrary());
};

const loadCategories = async () => {
  try {
    const res = await getMediaOssCategoryTreeApi({ loading: false });
    categories.value = flattenTree(res.data || []);
    if (!currentCategoryId.value || !categories.value.some(c => c.categoryId === currentCategoryId.value)) {
      currentCategoryId.value = resolveDefaultCategoryId(categories.value);
    }
  } catch (error) {
    console.error("加载分类失败:", error);
    categories.value = [];
  }
};

const loadLibrary = async () => {
  if (!currentCategoryId.value) {
    images.value = [];
    return;
  }
  loading.value = true;
  try {
    const res = await getMediaOssPageApi(
      {
        categoryId: currentCategoryId.value,
        pageNum: 1,
        pageSize: 200
      },
      { loading: false }
    );
    images.value = res.data?.records || [];
  } catch (error) {
    console.error("加载图片库失败:", error);
    images.value = [];
  } finally {
    loading.value = false;
  }
};

const selectCategory = id => {
  currentCategoryId.value = id;
  selectedIds.value = [];
  loadLibrary();
};

const openNewCategoryDialog = () => {
  newCategoryName.value = "";
  newCategoryParentId.value = currentCategoryId.value;
  showNewCategoryDialog.value = true;
};

const resetNewCategoryForm = () => {
  newCategoryName.value = "";
  newCategoryParentId.value = null;
};

const handleCreateCategory = async () => {
  const name = (newCategoryName.value || "").trim();
  if (!name) {
    ElMessage.warning("请填写分类名称");
    return;
  }
  try {
    await addMediaOssCategoryApi({
      categoryName: name,
      parentId: newCategoryParentId.value || 0
    });
    await loadCategories();
    const created = categories.value.find(c => c.categoryName === name);
    if (created) {
      currentCategoryId.value = created.categoryId;
      selectedIds.value = [];
      loadLibrary();
    }
    showNewCategoryDialog.value = false;
    ElMessage.success("分类已创建");
  } catch (error) {
    // 全局已提示
  }
};

const cancelCreateCategory = () => {
  showNewCategoryDialog.value = false;
};

const handleEditCategory = async cat => {
  editingCategoryId.value = cat.categoryId;
  editingCategoryName.value = cat.categoryName;
  await nextTick();
  const el = Array.isArray(folderEditInputRef.value) ? folderEditInputRef.value[0] : folderEditInputRef.value;
  el?.focus?.();
};

const handleSaveCategoryName = async () => {
  if (editingCategoryId.value == null) return;
  const name = editingCategoryName.value.trim();
  if (!name) {
    handleCancelEdit();
    return;
  }
  const old = categories.value.find(c => c.categoryId === editingCategoryId.value);
  if (name === old?.categoryName) {
    handleCancelEdit();
    return;
  }
  try {
    await updateMediaOssCategoryNameApi({
      categoryId: editingCategoryId.value,
      categoryName: name
    });
    ElMessage.success("重命名成功");
    loadCategories();
    handleCancelEdit();
  } catch (error) {
    // 全局已提示
  }
};

const handleCancelEdit = () => {
  editingCategoryId.value = null;
  editingCategoryName.value = "";
};

const handleDeleteCategory = async cat => {
  try {
    await ElMessageBox.confirm(
      cat.imageCount > 0
        ? `确定删除分类「${cat.categoryName}」？该分类下有 ${cat.imageCount} 张图片统计，有子分类时无法删除。`
        : `确定删除分类「${cat.categoryName}」？`,
      "删除分类",
      { confirmButtonText: "确定", cancelButtonText: "取消", type: "warning" }
    );
    await deleteMediaOssCategoryApi(cat.categoryId);
    ElMessage.success("删除成功");
    if (currentCategoryId.value === cat.categoryId) currentCategoryId.value = null;
    await loadCategories();
    loadLibrary();
  } catch (error) {
    if (error !== "cancel") {
      // ignore
    }
  }
};

const handleImageClick = img => {
  pickUrl.value = img.ossUrl;
};

const handleSelectAll = val => {
  selectedIds.value = val ? images.value.map(img => img.ossId) : [];
};

const toggleSelect = ossId => {
  const index = selectedIds.value.indexOf(ossId);
  if (index > -1) selectedIds.value.splice(index, 1);
  else selectedIds.value.push(ossId);
};

const handleConfirm = () => {
  emit("update:modelValue", pickUrl.value);
  showLibrary.value = false;
  ElMessage.success("已选择图片");
};

const handleClear = () => {
  emit("update:modelValue", "");
};

const uploadPendingCount = ref(0);
const uploadBatchOk = ref(0);
const uploadBatchFail = ref(0);

const beforeUpload = file => {
  if (!currentCategoryId.value) {
    ElMessage.warning("请先选择分类");
    return false;
  }
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
  loadCategories();
  if (uploadBatchFail.value === 0) {
    ElMessage.success(uploadBatchOk.value <= 1 ? "上传成功" : `成功上传 ${uploadBatchOk.value} 张图片`);
  } else if (uploadBatchOk.value === 0) {
    ElMessage.error("上传失败，请重试");
  } else {
    ElMessage.warning(`上传完成：成功 ${uploadBatchOk.value} 张，失败 ${uploadBatchFail.value} 张`);
  }
};

const handleUploadSuccess = response => {
  uploadPendingCount.value--;
  if (uploadPendingCount.value < 0) uploadPendingCount.value = 0;
  if (response?.code === 200) uploadBatchOk.value++;
  else uploadBatchFail.value++;
  if (uploadPendingCount.value === 0) finishUploadBatch();
};

const handleUploadError = () => {
  uploadPendingCount.value--;
  if (uploadPendingCount.value < 0) uploadPendingCount.value = 0;
  uploadBatchFail.value++;
  if (uploadPendingCount.value === 0) finishUploadBatch();
};

const handleDeleteSingle = async img => {
  try {
    await ElMessageBox.confirm("确定删除此图片？", "提示", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning"
    });
    await deleteMediaOssApi([img.ossId]);
    ElMessage.success("删除成功");
    loadLibrary();
    loadCategories();
    if (pickUrl.value === img.ossUrl) pickUrl.value = "";
  } catch (error) {
    if (error !== "cancel") {
      // ignore
    }
  }
};

const open = () => {
  showLibrary.value = true;
  pickUrl.value = "";
  selectedIds.value = [];
  loadCategories().then(() => loadLibrary());
};

defineExpose({ open });

const handleBatchDelete = async () => {
  if (selectedIds.value.length === 0) return;
  try {
    await ElMessageBox.confirm(`确定删除选中的 ${selectedIds.value.length} 张图片？`, "批量删除", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning"
    });
    await deleteMediaOssApi([...selectedIds.value]);
    ElMessage.success(`成功删除 ${selectedIds.value.length} 张图片`);
    selectedIds.value = [];
    loadLibrary();
    loadCategories();
  } catch (error) {
    if (error !== "cancel") {
      // ignore
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

.folder-item:hover .folder-actions {
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
