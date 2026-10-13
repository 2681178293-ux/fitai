<template>
  <div class="multi-image-library">
    <!-- 已选图片列表 -->
    <div class="image-list">
      <draggable
        v-model="localImages"
        item-key="url"
        class="draggable-container"
        @end="handleDragEnd"
      >
        <template #item="{ element, index }">
          <div class="image-item">
            <el-image :src="element" fit="cover" class="item-image" />
            <div class="item-actions">
              <el-icon class="action-icon" @click="removeImage(index)"><Delete /></el-icon>
            </div>
          </div>
        </template>
      </draggable>
      <div v-if="canAddMore" class="add-btn" @click="openLibrary">
        <el-icon class="add-icon"><Plus /></el-icon>
        <span class="add-text">添加图片</span>
      </div>
    </div>

    <!-- 图片库弹窗 -->
    <el-dialog
      v-model="showLibrary"
      title="选择图片"
      width="1000px"
      destroy-on-close
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
              @click="selectCategory(cat.categoryId)"
            >
              <el-icon class="folder-icon">
                <Folder v-if="currentCategoryId !== cat.categoryId" />
                <FolderOpened v-else />
              </el-icon>
              <span class="folder-name">{{ cat.categoryName }}</span>
              <span class="folder-count">{{ cat.imageCount || 0 }}</span>
            </div>
          </div>
        </div>

        <!-- 右侧内容 -->
        <div class="library-content">
          <!-- 工具栏 -->
          <div class="library-toolbar">
            <div class="toolbar-left">
              <span class="selected-count">已选 {{ selectedUrls.length }} 张</span>
            </div>
            <div class="toolbar-right">
              <el-upload
                :action="uploadUrl"
                :headers="uploadHeaders"
                :data="uploadData"
                :show-file-list="false"
                :disabled="!currentCategoryId"
                :before-upload="beforeLibraryUpload"
                :on-success="handleUploadSuccess"
                :on-error="handleUploadError"
                accept="image/*"
                multiple
              >
                <el-button type="success" :icon="Upload" :disabled="!currentCategoryId">上传图片</el-button>
              </el-upload>
              <el-button :icon="Refresh" @click="loadLibrary">刷新</el-button>
            </div>
          </div>

          <!-- 图片网格 -->
          <div v-loading="loading" class="image-grid-container">
            <el-empty v-if="!loading && !currentCategoryId" description="请先选择分类" />
            <el-empty v-else-if="!loading && images.length === 0" description="暂无图片，请上传" />
            <div v-else class="image-grid">
              <div
                v-for="img in images"
                :key="img.ossId"
                class="image-card"
                :class="{ selected: selectedUrls.includes(img.ossUrl) }"
                @click="toggleSelect(img.ossUrl)"
              >
                <el-image :src="img.ossUrl" fit="cover" class="grid-image" />
                <div class="select-indicator">
                  <el-icon v-if="selectedUrls.includes(img.ossUrl)" class="check-icon"><Check /></el-icon>
                  <span v-else class="select-order">{{ getSelectOrder(img.ossUrl) }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <span class="selected-info">已选择 {{ selectedUrls.length }} 张图片</span>
          <el-button @click="showLibrary = false">取消</el-button>
          <el-button type="primary" :disabled="selectedUrls.length === 0" @click="handleConfirm">
            确定选择
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, watch } from "vue";
import { ElMessage } from "element-plus";
import { Plus, Delete, Folder, FolderOpened, Upload, Refresh, Check } from "@element-plus/icons-vue";
import draggable from "vuedraggable";
import { useUserStore } from "@/stores/modules/user";
import { getMediaOssPageApi, getMediaOssCategoryTreeApi } from "@/api/modules/media";

const FOLDER_HINT_MAP = {
  course: ["课程封面", "课程图片", "课时封面"],
  exercise: ["动作图片"],
  product: ["商品图片", "商品封面", "商品相关"],
  banner: ["轮播图片"],
  seckill: ["秒杀商品"],
  coach: ["人物头像"]
};

const props = defineProps({
  modelValue: {
    type: Array,
    default: () => []
  },
  folder: {
    type: String,
    default: ""
  },
  uploadAction: {
    type: String,
    default: "/file/upload/image"
  },
  limit: {
    type: Number,
    default: 9
  }
});

const emit = defineEmits(["update:modelValue"]);

const userStore = useUserStore();
const uploadUrl = computed(() => import.meta.env.VITE_API_URL + "/admin/media/oss/upload");
const uploadHeaders = computed(() => ({
  Authorization: userStore.token
}));
const uploadData = computed(() => ({ categoryId: currentCategoryId.value }));

const localImages = ref([]);

watch(
  () => props.modelValue,
  val => {
    localImages.value = [...(val || [])];
  },
  { immediate: true }
);

const canAddMore = computed(() => localImages.value.length < props.limit);

const handleDragEnd = () => {
  emit("update:modelValue", [...localImages.value]);
};

const removeImage = index => {
  localImages.value.splice(index, 1);
  emit("update:modelValue", [...localImages.value]);
};

const showLibrary = ref(false);
const loading = ref(false);
const categories = ref([]);
const images = ref([]);
const currentCategoryId = ref(null);
const selectedUrls = ref([]);

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
  return flat[0].categoryId;
};

const openLibrary = () => {
  showLibrary.value = true;
  selectedUrls.value = [];
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
    images.value = [];
  } finally {
    loading.value = false;
  }
};

const selectCategory = id => {
  currentCategoryId.value = id;
  loadLibrary();
};

const toggleSelect = url => {
  const index = selectedUrls.value.indexOf(url);
  if (index > -1) {
    selectedUrls.value.splice(index, 1);
  } else {
    const total = localImages.value.length + selectedUrls.value.length;
    if (total >= props.limit) {
      ElMessage.warning(`最多只能选择 ${props.limit} 张图片`);
      return;
    }
    selectedUrls.value.push(url);
  }
};

const getSelectOrder = url => {
  const index = selectedUrls.value.indexOf(url);
  return index > -1 ? index + 1 : "";
};

const handleConfirm = () => {
  const newImages = [...localImages.value, ...selectedUrls.value];
  localImages.value = newImages;
  emit("update:modelValue", newImages);
  showLibrary.value = false;
  ElMessage.success(`已添加 ${selectedUrls.value.length} 张图片`);
};

const uploadPendingCount = ref(0);
const uploadBatchOk = ref(0);
const uploadBatchFail = ref(0);

const beforeLibraryUpload = file => {
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
</script>

<style scoped>
.multi-image-library {
  width: 100%;
}

.image-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.draggable-container {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.image-item {
  position: relative;
  width: 80px;
  height: 80px;
  border-radius: 4px;
  overflow: hidden;
  cursor: move;
}

.item-image {
  width: 100%;
  height: 100%;
}

.item-actions {
  position: absolute;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(0, 0, 0, 0.5);
  opacity: 0;
  transition: opacity 0.2s;
}

.image-item:hover .item-actions {
  opacity: 1;
}

.action-icon {
  color: white;
  font-size: 20px;
  cursor: pointer;
}

.add-btn {
  width: 80px;
  height: 80px;
  border: 1px dashed var(--el-border-color);
  border-radius: 4px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 4px;
  cursor: pointer;
  transition: all 0.2s;
}

.add-btn:hover {
  border-color: var(--el-color-primary);
  color: var(--el-color-primary);
}

.add-icon {
  font-size: 20px;
}

.add-text {
  font-size: 12px;
}

/* 弹窗样式 */
.library-container {
  display: flex;
  height: 500px;
  border: 1px solid var(--el-border-color-lighter);
  border-radius: 4px;
  overflow: hidden;
}

.library-sidebar {
  width: 220px;
  border-right: 1px solid var(--el-border-color-lighter);
  background: var(--el-fill-color-lighter);
  flex-shrink: 0;
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
  max-height: calc(100% - 45px);
  overflow-y: auto;
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
}

.toolbar-left,
.toolbar-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.selected-count {
  color: var(--el-color-primary);
  font-weight: 500;
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
  cursor: pointer;
  transition: all 0.2s;
}

.image-card:hover {
  border-color: var(--el-color-primary-light-3);
}

.image-card.selected {
  border-color: var(--el-color-primary);
}

.grid-image {
  width: 100%;
  height: 100%;
}

.select-indicator {
  position: absolute;
  top: 6px;
  right: 6px;
  width: 24px;
  height: 24px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.9);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: 600;
}

.image-card.selected .select-indicator {
  background: var(--el-color-primary);
}

.check-icon {
  color: white;
  font-size: 16px;
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
