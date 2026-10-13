<template>
  <div class="media-library-page">
    <div class="library-container">
      <!-- 左侧分类树 -->
      <div class="library-sidebar">
        <div class="sidebar-title">
          <span>图片分类</span>
          <el-button type="primary" link :icon="Plus" @click="openNewCategoryDialog()">新增</el-button>
        </div>
        <div class="category-tree-wrap" v-loading="treeLoading">
          <el-tree
            ref="treeRef"
            :data="categoryTree"
            node-key="categoryId"
            :props="treeProps"
            highlight-current
            default-expand-all
            :expand-on-click-node="false"
            @node-click="handleNodeClick"
          >
            <template #default="{ data }">
              <div class="tree-node" :class="{ active: currentCategoryId === data.categoryId }">
                <el-icon class="node-icon"><Folder /></el-icon>
                <span v-if="editingCategoryId !== data.categoryId" class="node-label" :title="data.categoryName">
                  {{ data.categoryName }}
                </span>
                <el-input
                  v-else
                  ref="editInputRef"
                  v-model="editingCategoryName"
                  size="small"
                  class="node-edit-input"
                  @click.stop
                  @keyup.enter="handleSaveCategoryName"
                  @blur="handleSaveCategoryName"
                />
                <span class="node-count">{{ data.imageCount || 0 }}</span>
                <div class="node-actions" @click.stop>
                  <el-icon
                    class="node-action"
                    title="上移"
                    :class="{ disabled: !canMoveCategory(data, 'up') }"
                    @click="handleMoveCategory(data, 'up')"
                  >
                    <Top />
                  </el-icon>
                  <el-icon
                    class="node-action"
                    title="下移"
                    :class="{ disabled: !canMoveCategory(data, 'down') }"
                    @click="handleMoveCategory(data, 'down')"
                  >
                    <Bottom />
                  </el-icon>
                  <el-icon
                    v-if="editingCategoryId !== data.categoryId"
                    class="node-action"
                    title="重命名"
                    @click="startEditCategory(data)"
                  >
                    <Edit />
                  </el-icon>
                  <el-icon class="node-action" title="新增子分类" @click="openNewCategoryDialog(data.categoryId)">
                    <Plus />
                  </el-icon>
                  <el-icon class="node-action danger" title="删除" @click="handleDeleteCategory(data)">
                    <Delete />
                  </el-icon>
                </div>
              </div>
            </template>
          </el-tree>
          <el-empty v-if="!treeLoading && categoryTree.length === 0" description="暂无分类" :image-size="64" />
        </div>
        <div class="sidebar-footer">
          <el-button type="primary" :icon="Plus" @click="openNewCategoryDialog()">新建分类</el-button>
          <el-button
            type="primary"
            plain
            :icon="Plus"
            :disabled="!currentCategoryId"
            @click="openNewCategoryDialog(currentCategoryId)"
          >
            新建子分类
          </el-button>
        </div>
      </div>

      <!-- 右侧图片区 -->
      <div class="library-content">
        <div class="library-toolbar">
          <div class="toolbar-left">
            <el-checkbox
              v-model="selectAll"
              :indeterminate="isIndeterminate"
              :disabled="images.length === 0"
              @change="handleSelectAll"
            >
              全选
            </el-checkbox>
            <el-button
              v-if="selectedIds.length > 0"
              type="danger"
              size="small"
              :icon="Delete"
              @click="handleBatchDelete"
            >
              删除 ({{ selectedIds.length }})
            </el-button>
            <span v-if="currentCategoryName" class="current-cat">当前：{{ currentCategoryName }}</span>
          </div>
          <div class="toolbar-right">
            <el-input
              v-model="keyword"
              placeholder="搜索图片名称"
              clearable
              style="width: 200px"
              :prefix-icon="Search"
              @keyup.enter="handleSearch"
              @clear="handleSearch"
            />
            <el-upload
              :action="uploadUrl"
              :headers="uploadHeaders"
              :data="uploadData"
              :show-file-list="false"
              :disabled="!currentCategoryId"
              :before-upload="beforeUpload"
              :on-success="handleUploadSuccess"
              :on-error="handleUploadError"
              accept="image/*"
              multiple
            >
              <el-button type="success" :icon="Upload" :disabled="!currentCategoryId">上传图片</el-button>
            </el-upload>
            <el-button :icon="Refresh" @click="refreshAll">刷新</el-button>
          </div>
        </div>

        <div v-loading="loading" class="image-grid-container">
          <el-empty
            v-if="!loading && !currentCategoryId"
            description="请先选择左侧分类"
          />
          <el-empty
            v-else-if="!loading && images.length === 0"
            description="暂无图片，请上传"
          />
          <div v-else class="image-grid">
            <div
              v-for="img in images"
              :key="img.ossId"
              class="image-card"
              :class="{ selected: selectedIds.includes(img.ossId) }"
            >
              <div class="image-checkbox">
                <el-checkbox
                  :model-value="selectedIds.includes(img.ossId)"
                  @change="toggleSelect(img.ossId)"
                />
              </div>
              <el-image
                :src="img.ossUrl"
                fit="cover"
                class="grid-image"
                :preview-src-list="previewList"
                :initial-index="images.findIndex(i => i.ossId === img.ossId)"
                preview-teleported
              />
              <div class="image-info">
                <span class="image-name" :title="img.ossName">{{ img.ossName }}</span>
                <span class="image-meta">{{ formatSize(img.ossFileSize) }}</span>
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

        <div class="library-pager" v-if="currentCategoryId">
          <el-pagination
            v-model:current-page="pageNum"
            v-model:page-size="pageSize"
            :total="total"
            :page-sizes="[20, 40, 60, 100]"
            layout="total, sizes, prev, pager, next"
            background
            @size-change="loadImages"
            @current-change="loadImages"
          />
        </div>
      </div>
    </div>

    <!-- 新建分类 -->
    <el-dialog
      v-model="showNewCategoryDialog"
      title="新建分类"
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
        <el-form-item label="父分类">
          <el-tree-select
            v-model="newCategoryParentId"
            :data="categoryTree"
            check-strictly
            clearable
            :props="{ label: 'categoryName', value: 'categoryId', children: 'children' }"
            placeholder="不选则为顶级分类"
            style="width: 100%"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showNewCategoryDialog = false">取消</el-button>
        <el-button type="primary" @click="handleCreateCategory">创建</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="MediaLibrary">
import { ref, computed, nextTick, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { Folder, Plus, Edit, Delete, Upload, Refresh, Search, Top, Bottom } from "@element-plus/icons-vue";
import { useUserStore } from "@/stores/modules/user";
import {
  getMediaOssPageApi,
  getMediaOssCategoryTreeApi,
  addMediaOssCategoryApi,
  updateMediaOssCategoryNameApi,
  moveMediaOssCategoryApi,
  deleteMediaOssCategoryApi,
  deleteMediaOssApi
} from "@/api/modules/media";

const userStore = useUserStore();
const treeRef = ref();
const editInputRef = ref();

const treeProps = { label: "categoryName", children: "children" };
const categoryTree = ref([]);
const treeLoading = ref(false);
const currentCategoryId = ref(null);
const currentCategoryName = ref("");

const loading = ref(false);
const images = ref([]);
const selectedIds = ref([]);
const keyword = ref("");
const pageNum = ref(1);
const pageSize = ref(40);
const total = ref(0);

const editingCategoryId = ref(null);
const editingCategoryName = ref("");

const showNewCategoryDialog = ref(false);
const newCategoryName = ref("");
const newCategoryParentId = ref(null);

const uploadUrl = computed(() => import.meta.env.VITE_API_URL + "/admin/media/oss/upload");
const uploadHeaders = computed(() => ({ Authorization: userStore.token }));
const uploadData = computed(() => ({ categoryId: currentCategoryId.value }));

const previewList = computed(() => images.value.map(i => i.ossUrl));
const selectAll = computed({
  get: () => images.value.length > 0 && selectedIds.value.length === images.value.length,
  set: () => {}
});
const isIndeterminate = computed(
  () => selectedIds.value.length > 0 && selectedIds.value.length < images.value.length
);

const formatSize = size => {
  if (!size && size !== 0) return "";
  if (size < 1024) return size + " B";
  if (size < 1024 * 1024) return (size / 1024).toFixed(1) + " KB";
  return (size / 1024 / 1024).toFixed(1) + " MB";
};

const flattenCategories = (nodes, list = []) => {
  (nodes || []).forEach(n => {
    list.push(n);
    if (n.children?.length) flattenCategories(n.children, list);
  });
  return list;
};

/** 获取同级分类列表（同一 parentId） */
const getSiblingCategories = category => {
  const parentId = category.parentId ?? 0;
  if (!parentId) {
    return categoryTree.value || [];
  }
  const flat = flattenCategories(categoryTree.value);
  const parent = flat.find(c => c.categoryId === parentId);
  return parent?.children || [];
};

const canMoveCategory = (category, direction) => {
  const siblings = getSiblingCategories(category);
  const index = siblings.findIndex(c => c.categoryId === category.categoryId);
  if (index < 0) return false;
  return direction === "up" ? index > 0 : index < siblings.length - 1;
};

/** 上移 / 下移：与同级相邻节点交换 orderNum（调用后端 move） */
const handleMoveCategory = async (category, direction) => {
  if (!canMoveCategory(category, direction)) return;
  const siblings = getSiblingCategories(category);
  const index = siblings.findIndex(c => c.categoryId === category.categoryId);
  const target = direction === "up" ? siblings[index - 1] : siblings[index + 1];
  if (!target) return;
  try {
    await moveMediaOssCategoryApi({
      categoryId: category.categoryId,
      targetCategoryId: target.categoryId,
      direction
    });
    ElMessage.success(direction === "up" ? "已上移" : "已下移");
    await loadCategoryTree(currentCategoryId.value);
  } catch (e) {
    // 全局拦截已提示
  }
};

const loadCategoryTree = async (preferCategoryId) => {
  treeLoading.value = true;
  try {
    const res = await getMediaOssCategoryTreeApi({ loading: false });
    categoryTree.value = res.data || [];
    const flat = flattenCategories(categoryTree.value);
    const preferId = preferCategoryId ?? currentCategoryId.value;
    let target = flat.find(c => c.categoryId === preferId);
    if (!target && flat.length) target = flat[0];
    if (target) {
      currentCategoryId.value = target.categoryId;
      currentCategoryName.value = target.categoryName;
      await nextTick();
      treeRef.value?.setCurrentKey(target.categoryId);
      await loadImages();
    } else {
      currentCategoryId.value = null;
      currentCategoryName.value = "";
      images.value = [];
      total.value = 0;
    }
  } catch (e) {
    console.error("加载分类失败:", e);
    categoryTree.value = [];
  } finally {
    treeLoading.value = false;
  }
};

const loadImages = async () => {
  if (!currentCategoryId.value) {
    images.value = [];
    total.value = 0;
    return;
  }
  loading.value = true;
  selectedIds.value = [];
  try {
    const res = await getMediaOssPageApi(
      {
        categoryId: currentCategoryId.value,
        keyword: keyword.value || undefined,
        pageNum: pageNum.value,
        pageSize: pageSize.value
      },
      { loading: false }
    );
    images.value = res.data?.records || [];
    total.value = res.data?.total || 0;
  } catch (e) {
    console.error("加载图片失败:", e);
    images.value = [];
    total.value = 0;
  } finally {
    loading.value = false;
  }
};

const handleNodeClick = data => {
  if (editingCategoryId.value) return;
  currentCategoryId.value = data.categoryId;
  currentCategoryName.value = data.categoryName;
  pageNum.value = 1;
  keyword.value = "";
  loadImages();
};

const handleSearch = () => {
  pageNum.value = 1;
  loadImages();
};

const refreshAll = async () => {
  await loadCategoryTree(currentCategoryId.value);
};

const handleSelectAll = val => {
  selectedIds.value = val ? images.value.map(i => i.ossId) : [];
};

const toggleSelect = ossId => {
  const idx = selectedIds.value.indexOf(ossId);
  if (idx > -1) selectedIds.value.splice(idx, 1);
  else selectedIds.value.push(ossId);
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
  try {
    await addMediaOssCategoryApi({
      categoryName: name,
      parentId: newCategoryParentId.value || 0
    });
    ElMessage.success("创建成功");
    showNewCategoryDialog.value = false;
    await loadCategoryTree(currentCategoryId.value);
  } catch (e) {
    // 全局拦截已提示
  }
};

const startEditCategory = async data => {
  editingCategoryId.value = data.categoryId;
  editingCategoryName.value = data.categoryName;
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
  const flat = flattenCategories(categoryTree.value);
  const old = flat.find(c => c.categoryId === id);
  if (old && old.categoryName === name) {
    editingCategoryId.value = null;
    return;
  }
  try {
    await updateMediaOssCategoryNameApi({ categoryId: id, categoryName: name });
    ElMessage.success("重命名成功");
    if (currentCategoryId.value === id) currentCategoryName.value = name;
    editingCategoryId.value = null;
    await loadCategoryTree(currentCategoryId.value);
  } catch (e) {
    editingCategoryId.value = null;
  }
};

const handleDeleteCategory = async data => {
  try {
    await ElMessageBox.confirm(
      data.imageCount > 0
        ? `确定删除分类「${data.categoryName}」？该分类下约有 ${data.imageCount} 张图片统计，请确认无子分类后再删。`
        : `确定删除分类「${data.categoryName}」？`,
      "删除分类",
      { type: "warning", confirmButtonText: "确定", cancelButtonText: "取消" }
    );
    await deleteMediaOssCategoryApi(data.categoryId);
    ElMessage.success("删除成功");
    if (currentCategoryId.value === data.categoryId) {
      currentCategoryId.value = null;
      currentCategoryName.value = "";
    }
    await loadCategoryTree();
  } catch (e) {
    if (e !== "cancel") {
      // 全局拦截已提示
    }
  }
};

const uploadPendingCount = ref(0);
const uploadBatchOk = ref(0);
const uploadBatchFail = ref(0);

const beforeUpload = file => {
  if (!currentCategoryId.value) {
    ElMessage.warning("请先选择分类");
    return false;
  }
  if (!file.type.startsWith("image/")) {
    ElMessage.error("只能上传图片文件");
    return false;
  }
  if (file.size / 1024 / 1024 >= 10) {
    ElMessage.error("图片大小不能超过 10MB");
    return false;
  }
  if (uploadPendingCount.value === 0) {
    uploadBatchOk.value = 0;
    uploadBatchFail.value = 0;
  }
  uploadPendingCount.value++;
  return true;
};

const finishUploadBatch = async () => {
  await loadCategoryTree(currentCategoryId.value);
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
    await ElMessageBox.confirm("确定删除此图片？", "提示", { type: "warning" });
    await deleteMediaOssApi([img.ossId]);
    ElMessage.success("删除成功");
    await loadCategoryTree(currentCategoryId.value);
  } catch (e) {
    if (e !== "cancel") {
      // ignore
    }
  }
};

const handleBatchDelete = async () => {
  if (!selectedIds.value.length) return;
  try {
    await ElMessageBox.confirm(`确定删除选中的 ${selectedIds.value.length} 张图片？`, "批量删除", {
      type: "warning"
    });
    await deleteMediaOssApi([...selectedIds.value]);
    ElMessage.success("删除成功");
    selectedIds.value = [];
    await loadCategoryTree(currentCategoryId.value);
  } catch (e) {
    if (e !== "cancel") {
      // ignore
    }
  }
};

onMounted(() => {
  loadCategoryTree();
});
</script>

<style scoped>
.media-library-page {
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
  width: 300px;
  border-right: 1px solid var(--el-border-color-lighter);
  background: var(--el-fill-color-lighter);
  display: flex;
  flex-direction: column;
  flex-shrink: 0;
}

.sidebar-title {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 12px 12px 16px;
  font-size: 13px;
  font-weight: 600;
  border-bottom: 1px solid var(--el-border-color-lighter);
  flex-shrink: 0;
}

.category-tree-wrap {
  flex: 1;
  overflow: auto;
  padding: 8px 4px;
  min-height: 0;
}

.category-tree-wrap :deep(.el-tree) {
  background: transparent;
}

.category-tree-wrap :deep(.el-tree-node__content) {
  height: 36px;
  border-radius: 6px;
  margin: 1px 4px;
}

.tree-node {
  display: flex;
  align-items: center;
  gap: 4px;
  width: 100%;
  padding-right: 4px;
  min-width: 0;
}

.node-icon {
  flex-shrink: 0;
  color: var(--el-text-color-secondary);
}

.node-label {
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  font-size: 13px;
}

.node-edit-input {
  flex: 1;
}

.node-count {
  font-size: 12px;
  color: var(--el-text-color-secondary);
  background: var(--el-fill-color);
  padding: 1px 6px;
  border-radius: 6px;
  flex-shrink: 0;
}

.node-actions {
  display: flex;
  gap: 2px;
  opacity: 0;
  flex-shrink: 0;
}

.tree-node:hover .node-actions,
.tree-node.active .node-actions {
  opacity: 1;
}

.node-action {
  cursor: pointer;
  color: var(--el-text-color-secondary);
  font-size: 14px;
  padding: 2px;
}

.node-action:hover {
  color: var(--el-color-primary);
}

.node-action.danger:hover {
  color: var(--el-color-danger);
}

.node-action.disabled {
  opacity: 0.35;
  cursor: not-allowed;
  pointer-events: none;
}

.sidebar-footer {
  display: flex;
  flex-direction: column;
  gap: 8px;
  padding: 10px 12px;
  border-top: 1px solid var(--el-border-color-lighter);
  flex-shrink: 0;
  background: var(--el-fill-color-lighter);
}

.sidebar-footer .el-button {
  width: 100%;
  margin: 0;
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

.current-cat {
  font-size: 13px;
  color: var(--el-text-color-secondary);
}

.image-grid-container {
  flex: 1;
  overflow: auto;
  padding: 16px;
}

.image-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(140px, 1fr));
  gap: 12px;
}

.image-card {
  position: relative;
  aspect-ratio: 1;
  border-radius: 6px;
  overflow: hidden;
  border: 2px solid transparent;
  background: var(--el-fill-color-lighter);
  transition: border-color 0.2s;
}

.image-card:hover {
  border-color: var(--el-color-primary-light-3);
}

.image-card.selected {
  border-color: var(--el-color-primary);
}

.image-checkbox {
  position: absolute;
  top: 6px;
  left: 6px;
  z-index: 2;
}

.grid-image {
  width: 100%;
  height: 100%;
}

.image-info {
  position: absolute;
  left: 0;
  right: 0;
  bottom: 0;
  padding: 6px 8px;
  background: linear-gradient(transparent, rgba(0, 0, 0, 0.7));
  color: #fff;
}

.image-name {
  display: block;
  font-size: 12px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.image-meta {
  font-size: 11px;
  opacity: 0.85;
}

.delete-btn {
  position: absolute;
  top: 6px;
  right: 6px;
  opacity: 0;
  z-index: 2;
}

.image-card:hover .delete-btn {
  opacity: 1;
}

.library-pager {
  padding: 10px 16px;
  border-top: 1px solid var(--el-border-color-lighter);
  display: flex;
  justify-content: flex-end;
}
</style>
