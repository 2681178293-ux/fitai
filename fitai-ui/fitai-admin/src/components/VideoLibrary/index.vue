<template>
  <el-dialog v-model="visible" title="视频库" width="1100px" destroy-on-close class="video-library-dialog">
    <div class="video-library">
      <!-- 左侧分类 -->
      <div class="library-sidebar">
        <div class="sidebar-title">分类</div>
        <div class="category-list">
          <div
            v-for="cat in categories"
            :key="cat.id"
            class="category-item"
            :class="{ active: currentCategory === cat.id }"
            @click="selectCategory(cat.id)"
          >
            <el-icon><Folder /></el-icon>
            <span class="category-name">{{ cat.name }}</span>
            <span class="category-count">{{ cat.count }}</span>
            <el-icon
              v-if="cat.id !== null"
              class="category-delete"
              @click.stop="handleDeleteCategory(cat)"
            >
              <Close />
            </el-icon>
          </div>
        </div>
        <div class="sidebar-footer">
          <el-button
            v-if="!showNewCategoryInput"
            type="primary"
            link
            :icon="Plus"
            @click="showNewCategoryInput = true"
          >
            新增分类
          </el-button>
          <div v-else class="new-category-input">
            <el-input
              ref="newCategoryInputRef"
              v-model="newCategoryName"
              size="small"
              placeholder="输入分类名称"
              @keyup.enter="handleCreateCategory"
            />
            <div class="new-category-btns">
              <el-button type="primary" size="small" @click="handleCreateCategory">确定</el-button>
              <el-button size="small" @click="cancelCreateCategory">取消</el-button>
            </div>
          </div>
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
              v-if="selectedIds.length > 0"
              type="danger"
              size="small"
              :icon="Delete"
              @click="handleBatchDelete"
            >
              删除 ({{ selectedIds.length }})
            </el-button>
          </div>
          <div class="toolbar-right">
            <el-input
              v-model="searchKeyword"
              placeholder="搜索视频..."
              style="width: 180px"
              clearable
              :prefix-icon="Search"
            />
            <el-select v-model="statusFilter" placeholder="状态" style="width: 100px" clearable>
              <el-option label="已就绪" :value="2" />
              <el-option label="转码中" :value="1" />
            </el-select>
            <el-button :icon="Refresh" @click="loadVideos">刷新</el-button>
            <el-button type="primary" :icon="Upload" @click="showUploadDialog = true">上传视频</el-button>
          </div>
        </div>

        <!-- 视频网格 -->
        <div class="video-grid-container" v-loading="loading">
          <el-empty v-if="!loading && filteredVideos.length === 0" description="暂无视频，请上传" />
          <div v-else class="video-grid">
            <div
              v-for="video in filteredVideos"
              :key="video.id"
              class="video-card"
              :class="{ selected: selectedIds.includes(video.id), disabled: video.status !== 2 }"
            >
              <!-- 复选框 -->
              <div class="video-checkbox">
                <el-checkbox
                  :model-value="selectedIds.includes(video.id)"
                  @change="toggleSelect(video.id)"
                />
              </div>

              <!-- 封面 -->
              <div class="video-thumbnail" @click="handleVideoClick(video)">
                <el-image v-if="video.coverUrl" :src="video.coverUrl" fit="cover" />
                <!-- 转码中显示加载动画 -->
                <div v-else-if="video.status === 1" class="video-no-cover video-processing">
                  <el-icon :size="24" class="is-loading"><Loading /></el-icon>
                  <span class="processing-text">封面生成中</span>
                </div>
                <div v-else class="video-no-cover">
                  <el-icon :size="32"><VideoCamera /></el-icon>
                </div>
                <div class="video-duration">{{ formatDuration(video.duration) }}</div>
                <el-tag v-if="video.status !== 2" class="video-status" size="small" type="warning">
                  {{ video.status === 1 ? '转码中' : video.status === 0 ? '上传中' : '失败' }}
                </el-tag>
                <!-- 播放按钮遮罩 -->
                <div v-if="video.status === 2" class="play-overlay">
                  <el-icon :size="40"><VideoPlay /></el-icon>
                </div>
              </div>

              <!-- 信息 -->
              <div class="video-info">
                <div class="video-title" :title="video.title">{{ video.title }}</div>
                <div class="video-meta">
                  <span>{{ formatSize(video.size) }}</span>
                  <span>{{ video.categoryName || '未分类' }}</span>
                </div>
              </div>

              <!-- 选中/点击遮罩 -->
              <div
                v-if="!multiple"
                class="video-overlay"
                :class="{ active: pickVideo?.id === video.id }"
                @click="handleVideoClick(video)"
              >
                <el-icon v-if="pickVideo?.id === video.id" class="check-icon"><Check /></el-icon>
                <span v-else class="pick-text">点击选择</span>
              </div>

              <!-- 操作按钮 -->
              <div class="video-actions">
                <el-button
                  type="primary"
                  :icon="Edit"
                  circle
                  size="small"
                  @click.stop="handleEdit(video)"
                />
                <el-button
                  type="danger"
                  :icon="Delete"
                  circle
                  size="small"
                  @click.stop="handleDeleteSingle(video)"
                />
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <template #footer>
      <div class="dialog-footer">
        <span class="selected-info" v-if="pickVideo">
          已选择：{{ pickVideo.title }}
        </span>
        <div>
          <el-button @click="visible = false">取消</el-button>
          <el-button type="primary" :disabled="!pickVideo || pickVideo.status !== 2" @click="confirmSelect">
            确定选择
          </el-button>
        </div>
      </div>
    </template>
  </el-dialog>

  <!-- 上传弹窗 -->
  <el-dialog v-model="showUploadDialog" title="上传视频" width="600px" append-to-body destroy-on-close>
    <el-form ref="uploadFormRef" :model="uploadForm" :rules="uploadRules" label-width="100px">
      <el-form-item label="视频标题" prop="title">
        <el-input v-model="uploadForm.title" placeholder="请输入视频标题" />
      </el-form-item>
      <el-form-item label="选择视频" prop="file">
        <el-upload
          ref="uploadRef"
          drag
          :auto-upload="false"
          :show-file-list="false"
          :on-change="handleFileChange"
          :limit="1"
          :on-exceed="handleExceed"
          accept="video/*"
        >
          <div v-if="uploadForm.file" class="upload-file-info">
            <el-icon :size="24"><VideoCamera /></el-icon>
            <span>{{ uploadForm.file.name }}</span>
            <span class="file-size">({{ formatSize(uploadForm.file.size) }})</span>
          </div>
          <div v-else>
            <el-icon class="el-icon--upload" :size="40"><UploadFilled /></el-icon>
            <div class="el-upload__text">将视频拖到此处，或<em>点击上传</em></div>
          </div>
          <template #tip>
            <div class="el-upload__tip">支持 MP4、AVI、MOV 等格式，最大 2GB</div>
          </template>
        </el-upload>
      </el-form-item>
      <el-form-item label="所属分类">
        <el-select v-model="uploadForm.categoryId" placeholder="请选择分类" clearable style="width: 100%">
          <el-option v-for="cat in categories.slice(1)" :key="cat.id" :label="cat.name" :value="cat.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="视频描述">
        <el-input v-model="uploadForm.description" type="textarea" :rows="3" placeholder="请输入视频描述" />
      </el-form-item>
    </el-form>

    <!-- 上传进度 -->
    <div v-if="uploading" class="upload-progress">
      <el-progress :percentage="uploadProgress" :status="uploadProgress === 100 ? 'success' : ''" />
      <span class="progress-text">{{ uploadProgress < 100 ? '上传中...' : '处理中...' }}</span>
    </div>

    <template #footer>
      <el-button @click="showUploadDialog = false" :disabled="uploading">取消</el-button>
      <el-button type="primary" :loading="uploading" @click="handleUploadSubmit">
        {{ uploading ? `上传中 ${uploadProgress}%` : '开始上传' }}
      </el-button>
    </template>
  </el-dialog>

  <!-- 视频预览弹窗 -->
  <el-dialog v-model="showPreview" :title="previewVideo?.title" width="800px" append-to-body destroy-on-close>
    <video
      v-if="previewUrl"
      :src="previewUrl"
      controls
      autoplay
      style="width: 100%; max-height: 500px"
      controlsList="nodownload"
    />
  </el-dialog>

  <!-- 编辑弹窗 -->
  <el-dialog v-model="showEditDialog" title="编辑视频" width="500px" append-to-body destroy-on-close>
    <el-form ref="editFormRef" :model="editForm" :rules="editRules" label-width="100px">
      <el-form-item label="视频标题" prop="title">
        <el-input v-model="editForm.title" placeholder="请输入视频标题" />
      </el-form-item>
      <el-form-item label="所属分类">
        <el-select v-model="editForm.categoryId" placeholder="请选择分类" clearable style="width: 100%">
          <el-option v-for="cat in categories.slice(1)" :key="cat.id" :label="cat.name" :value="cat.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="视频描述">
        <el-input v-model="editForm.description" type="textarea" :rows="3" placeholder="请输入视频描述" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="showEditDialog = false">取消</el-button>
      <el-button type="primary" :loading="editLoading" @click="handleEditSubmit">保存</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, computed, watch, onBeforeUnmount } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { Search, Refresh, Upload, Delete, VideoCamera, VideoPlay, Check, Folder, UploadFilled, Edit, Plus, Close, Loading } from "@element-plus/icons-vue";
import { getVideoPageApi, deleteVideoApi, getUploadCredentialApi, uploadCompleteApi, getPlayUrlApi, updateVideoApi, syncVideoBatchApi, getVideoCategoryListApi, addVideoCategoryApi, deleteVideoCategoryApi } from "@/api/modules/video";
import { uploadCover } from "@/api/modules/upload";
import axios from "axios";
import { uploadToAliyunVod, isAliyunVodCredential, isMinioCredential } from "@/utils/aliyunVodUpload";
import { captureVideoFrame } from "@/utils/videoCapture";

const props = defineProps({
  multiple: { type: Boolean, default: false }
});

const emit = defineEmits(["select"]);

// 基础状态
const visible = ref(false);
const loading = ref(false);
const searchKeyword = ref("");
const statusFilter = ref(null);
const currentCategory = ref(null);
const videoList = ref([]);
const selectedIds = ref([]);
const pickVideo = ref(null);

// 分类
const categories = ref([
  { id: null, name: "全部", count: 0 }
]);

// 新建分类
const showNewCategoryInput = ref(false);
const newCategoryName = ref("");
const newCategoryInputRef = ref();

// 上传相关
const showUploadDialog = ref(false);
const uploadFormRef = ref();
const uploadRef = ref();
const uploading = ref(false);
const uploadProgress = ref(0);
const uploadForm = reactive({
  title: "",
  file: null,
  categoryId: null,
  description: ""
});
const uploadRules = {
  title: [{ required: true, message: "请输入视频标题", trigger: "blur" }]
};

// 预览
const showPreview = ref(false);
const previewVideo = ref(null);
const previewUrl = ref("");

// 编辑相关
const showEditDialog = ref(false);
const editFormRef = ref();
const editLoading = ref(false);
const editForm = reactive({
  id: null,
  title: "",
  categoryId: null,
  description: ""
});
const editRules = {
  title: [{ required: true, message: "请输入视频标题", trigger: "blur" }]
};

// 计算属性
const filteredVideos = computed(() => {
  let list = videoList.value;

  // 分类过滤
  if (currentCategory.value !== null) {
    list = list.filter(v => v.categoryId === currentCategory.value);
  }

  // 状态过滤
  if (statusFilter.value !== null && statusFilter.value !== "") {
    list = list.filter(v => v.status === statusFilter.value);
  }

  // 关键词搜索
  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase();
    list = list.filter(v => v.title.toLowerCase().includes(keyword));
  }

  return list;
});

const selectAll = computed({
  get: () => selectedIds.value.length === filteredVideos.value.length && filteredVideos.value.length > 0,
  set: () => {}
});

const isIndeterminate = computed(() =>
  selectedIds.value.length > 0 && selectedIds.value.length < filteredVideos.value.length
);

/** 仅在上传中/转码中时轮询；就绪(status=2)后停止，避免个别视频长期无封面导致永不结束，也避免与全屏 loading 叠加 */
const hasPendingVideos = list => {
  if (!list || list.length === 0) return false;
  return list.some(v => v.status === 0 || v.status === 1);
};

/** 轮询与静默同步请求不触发 axios 全屏 Loading */
const silentRequest = { loading: false };

const VIDEO_POLL_MS = 3000;
const VIDEO_POLL_MAX_TICKS = 120;
let videoPollTimer = null;

const stopVideoPoll = () => {
  if (videoPollTimer !== null) {
    clearInterval(videoPollTimer);
    videoPollTimer = null;
  }
};

/** 对话框打开且仍有未就绪视频时，定时批量同步并刷新列表，避免只同步一次、转码未完成则界面一直停在「封面生成中」 */
const startVideoPollIfNeeded = () => {
  stopVideoPoll();
  if (!visible.value || !hasPendingVideos(videoList.value)) return;
  let ticks = 0;
  videoPollTimer = setInterval(async () => {
    if (!visible.value) {
      stopVideoPoll();
      return;
    }
    ticks += 1;
    if (ticks > VIDEO_POLL_MAX_TICKS) {
      stopVideoPoll();
      return;
    }
    try {
      await syncVideoBatchApi(silentRequest);
      const res = await getVideoPageApi({ pageNum: 1, pageSize: 500 }, silentRequest);
      videoList.value = res.data?.records || [];
      updateCategoryCount();
      if (!hasPendingVideos(videoList.value)) {
        stopVideoPoll();
      }
    } catch (error) {
      console.error("同步视频状态失败:", error);
    }
  }, VIDEO_POLL_MS);
};

// 加载视频列表
const loadVideos = async (autoSync = true) => {
  loading.value = true;
  stopVideoPoll();
  try {
    const res = await getVideoPageApi({ pageNum: 1, pageSize: 500 });
    videoList.value = res.data?.records || [];
    updateCategoryCount();

    // 自动同步：未就绪的视频 或 没有封面的视频（首次 await，避免与 loading 竞态）
    if (autoSync) {
      const needSyncVideos = videoList.value.filter(v => v.status === 0 || v.status === 1 || !v.coverUrl);
      if (needSyncVideos.length > 0) {
        await syncPendingVideos();
      }
    }
    if (hasPendingVideos(videoList.value)) {
      startVideoPollIfNeeded();
    }
  } catch (error) {
    console.error("加载视频列表失败:", error);
    ElMessage.error("加载视频列表失败");
  } finally {
    loading.value = false;
  }
};

// 同步未就绪的视频状态（静默执行，不显示提示）
const syncPendingVideos = async () => {
  try {
    await syncVideoBatchApi(silentRequest);
    const res = await getVideoPageApi({ pageNum: 1, pageSize: 500 }, silentRequest);
    videoList.value = res.data?.records || [];
    updateCategoryCount();
  } catch (error) {
    console.error("同步视频状态失败:", error);
  }
};

// 加载分类
const loadCategories = async () => {
  try {
    const res = await getVideoCategoryListApi();
    // 先计算"全部"的数量
    const totalCount = videoList.value.length;
    const cats = [{ id: null, name: "全部", count: totalCount }];
    (res.data || []).forEach(item => {
      cats.push({ id: item.id, name: item.name, count: item.count || 0 });
    });
    categories.value = cats;
  } catch (error) {
    console.error("加载分类失败:", error);
  }
};

// 更新分类计数
const updateCategoryCount = () => {
  const countMap = {};
  videoList.value.forEach(v => {
    countMap[v.categoryId] = (countMap[v.categoryId] || 0) + 1;
  });
  categories.value.forEach(cat => {
    if (cat.id === null) {
      cat.count = videoList.value.length;
    } else {
      cat.count = countMap[cat.id] || 0;
    }
  });
};

// 选择分类
const selectCategory = id => {
  currentCategory.value = id;
  selectedIds.value = [];
};

// 新建分类
const handleCreateCategory = async () => {
  const name = newCategoryName.value.trim();
  if (!name) {
    ElMessage.warning("请输入分类名称");
    return;
  }

  try {
    await addVideoCategoryApi({ name });
    ElMessage.success("分类创建成功");
    newCategoryName.value = "";
    showNewCategoryInput.value = false;
    loadCategories();
  } catch (error) {
    ElMessage.error(error.message || "创建分类失败");
  }
};

const cancelCreateCategory = () => {
  newCategoryName.value = "";
  showNewCategoryInput.value = false;
};

// 删除分类
const handleDeleteCategory = async cat => {
  if (cat.count > 0) {
    ElMessage.warning(`该分类下有 ${cat.count} 个视频，无法删除`);
    return;
  }

  try {
    await ElMessageBox.confirm(`确定删除分类「${cat.name}」吗？`, "提示", {
      type: "warning"
    });
    await deleteVideoCategoryApi(cat.id);
    ElMessage.success("删除成功");
    loadCategories();
    if (currentCategory.value === cat.id) {
      currentCategory.value = null;
    }
  } catch (error) {
    if (error !== "cancel") {
      ElMessage.error(error.message || "删除失败");
    }
  }
};

// 视频选择
const handleVideoClick = video => {
  if (video.status !== 2) {
    ElMessage.warning("该视频尚未就绪，无法选择");
    return;
  }
  pickVideo.value = video;
};

// 复选框选择
const toggleSelect = id => {
  const idx = selectedIds.value.indexOf(id);
  if (idx > -1) {
    selectedIds.value.splice(idx, 1);
  } else {
    selectedIds.value.push(id);
  }
};

// 全选
const handleSelectAll = val => {
  if (val) {
    selectedIds.value = filteredVideos.value.map(v => v.id);
  } else {
    selectedIds.value = [];
  }
};

// 确认选择
const confirmSelect = () => {
  if (pickVideo.value) {
    emit("select", pickVideo.value);
    visible.value = false;
  }
};

// 编辑视频
const handleEdit = video => {
  Object.assign(editForm, {
    id: video.id,
    title: video.title,
    categoryId: video.categoryId,
    description: video.description || ""
  });
  showEditDialog.value = true;
};

// 提交编辑
const handleEditSubmit = async () => {
  try {
    await editFormRef.value.validate();
  } catch {
    return;
  }

  editLoading.value = true;
  try {
    await updateVideoApi(editForm.id, {
      title: editForm.title,
      categoryId: editForm.categoryId,
      description: editForm.description
    });
    ElMessage.success("保存成功");
    showEditDialog.value = false;
    loadVideos();
  } catch (error) {
    console.error("保存失败:", error);
    ElMessage.error("保存失败");
  } finally {
    editLoading.value = false;
  }
};

// 单个删除
const handleDeleteSingle = async video => {
  try {
    await ElMessageBox.confirm(`确定删除视频「${video.title}」？删除后无法恢复`, "提示", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning"
    });
    await deleteVideoApi(video.id);
    ElMessage.success("删除成功");
    // 删除后刷新列表（不自动同步，避免干扰）
    await loadVideos(false);
    loadCategories();
    if (pickVideo.value?.id === video.id) {
      pickVideo.value = null;
    }
  } catch (error) {
    if (error !== "cancel") {
      ElMessage.error("删除失败");
    }
  }
};

// 批量删除
const handleBatchDelete = async () => {
  if (selectedIds.value.length === 0) return;

  try {
    await ElMessageBox.confirm(
      `确定删除选中的 ${selectedIds.value.length} 个视频？删除后无法恢复`,
      "批量删除",
      {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }
    );

    // 逐个删除
    let successCount = 0;
    for (const id of selectedIds.value) {
      try {
        await deleteVideoApi(id);
        successCount++;
      } catch (e) {
        console.error("删除视频失败:", id, e);
      }
    }

    ElMessage.success(`成功删除 ${successCount} 个视频`);
    selectedIds.value = [];
    if (pickVideo.value && selectedIds.value.includes(pickVideo.value.id)) {
      pickVideo.value = null;
    }
    loadVideos();
  } catch (error) {
    if (error !== "cancel") {
      ElMessage.error("批量删除失败");
    }
  }
};

// ==================== 上传相关 ====================
const handleFileChange = file => {
  uploadForm.file = file.raw;
  if (!uploadForm.title) {
    uploadForm.title = file.name.replace(/\.[^/.]+$/, "");
  }
};

const handleExceed = () => {
  ElMessage.warning("只能上传一个视频文件");
};

const handleUploadSubmit = async () => {
  if (!uploadForm.file) {
    ElMessage.warning("请选择视频文件");
    return;
  }

  try {
    await uploadFormRef.value.validate();
  } catch {
    return;
  }

  uploading.value = true;
  uploadProgress.value = 0;

  try {
    // 1. 获取上传凭证
    const credentialRes = await getUploadCredentialApi({
      filename: uploadForm.file.name,
      title: uploadForm.title
    });
    const credential = credentialRes.data;
    const { videoId } = credential;

    // 2. 上传视频
    if (isMinioCredential(credential)) {
      await axios.put(credential.uploadUrl, uploadForm.file, {
        headers: { "Content-Type": uploadForm.file.type || "video/mp4" },
        onUploadProgress: e => {
          uploadProgress.value = Math.round((e.loaded / e.total) * 100);
        }
      });
    } else if (isAliyunVodCredential(credential)) {
      await uploadToAliyunVod(credential, uploadForm.file, percent => {
        uploadProgress.value = percent;
      });
    } else {
      throw new Error("无效的上传凭证");
    }

    // 3. MinIO 环境：截取封面并上传；阿里云 VOD 自动生成封面
    let coverUrl = null;
    let duration = null;
    if (isMinioCredential(credential)) {
      try {
        const { coverBlob, duration: d } = await captureVideoFrame(uploadForm.file);
        duration = d;
        const formData = new FormData();
        formData.append("file", coverBlob, `cover-${videoId}.jpg`);
        const coverRes = await uploadCover(formData);
        coverUrl = coverRes.data;
      } catch (e) {
        console.warn("视频封面截取失败，将使用默认占位", e);
      }
    }

    // 4. 通知后端上传完成
    await uploadCompleteApi(videoId, {
      categoryId: uploadForm.categoryId,
      description: uploadForm.description,
      size: uploadForm.file.size,
      duration,
      coverUrl
    });

    ElMessage.success(coverUrl ? "上传成功" : "上传成功，封面正在自动生成中...");
    showUploadDialog.value = false;

    // 重置表单
    Object.assign(uploadForm, { title: "", file: null, categoryId: null, description: "" });
    uploadProgress.value = 0;

    // 刷新列表
    loadVideos();
  } catch (error) {
    console.error("上传失败:", error);
    ElMessage.error(error.message || "上传失败，请重试");
  } finally {
    uploading.value = false;
  }
};

// ==================== 预览 ====================
const handlePreview = async video => {
  if (video.status !== 2) {
    ElMessage.warning("视频尚未就绪");
    return;
  }
  try {
    const res = await getPlayUrlApi(video.id);
    previewUrl.value = res.data;
    previewVideo.value = video;
    showPreview.value = true;
  } catch (error) {
    ElMessage.error("获取播放地址失败");
  }
};

// 格式化工具
const formatDuration = seconds => {
  if (!seconds) return "-";
  const mins = Math.floor(seconds / 60);
  const secs = seconds % 60;
  return `${mins}:${secs.toString().padStart(2, "0")}`;
};

const formatSize = bytes => {
  if (!bytes) return "-";
  if (bytes < 1024) return bytes + " B";
  if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + " KB";
  if (bytes < 1024 * 1024 * 1024) return (bytes / 1024 / 1024).toFixed(1) + " MB";
  return (bytes / 1024 / 1024 / 1024).toFixed(2) + " GB";
};

// 打开视频库
const open = () => {
  pickVideo.value = null;
  selectedIds.value = [];
  searchKeyword.value = "";
  statusFilter.value = null;
  currentCategory.value = null;
  visible.value = true;
  loadCategories();
  loadVideos();
};

watch(visible, v => {
  if (!v) stopVideoPoll();
});

onBeforeUnmount(() => {
  stopVideoPoll();
});

// 暴露方法
defineExpose({ open });
</script>

<style scoped>
.video-library {
  display: flex;
  height: 550px;
  gap: 16px;
}

/* 左侧分类 */
.library-sidebar {
  width: 180px;
  flex-shrink: 0;
  border-right: 1px solid #ebeef5;
  padding-right: 16px;
  display: flex;
  flex-direction: column;
}

.sidebar-title {
  font-weight: 600;
  color: #303133;
  margin-bottom: 12px;
  padding-left: 8px;
}

.category-list {
  display: flex;
  flex-direction: column;
  gap: 4px;
  flex: 1;
  overflow-y: auto;
}

.sidebar-footer {
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid #ebeef5;
}

.new-category-input {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.new-category-btns {
  display: flex;
  gap: 8px;
}

.category-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s;
  color: #606266;
}

.category-item:hover {
  background: #f5f7fa;
}

.category-item.active {
  background: #ecf5ff;
  color: #409eff;
}

.category-name {
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  font-size: 14px;
}

.category-count {
  font-size: 12px;
  color: #909399;
  background: #f0f0f0;
  padding: 2px 6px;
  border-radius: 10px;
}

.category-item.active .category-count {
  background: #d9ecff;
  color: #409eff;
}

.category-delete {
  display: none;
  color: #909399;
  font-size: 14px;
  margin-left: 4px;
  cursor: pointer;
}

.category-delete:hover {
  color: #f56c6c;
}

.category-item:hover .category-delete {
  display: block;
}

.category-item:hover .category-count {
  display: none;
}

/* 右侧内容 */
.library-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
}

.library-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  flex-wrap: wrap;
  gap: 12px;
}

.toolbar-left,
.toolbar-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

/* 视频网格 */
.video-grid-container {
  flex: 1;
  overflow-y: auto;
}

.video-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.video-card {
  position: relative;
  border: 2px solid #ebeef5;
  border-radius: 8px;
  overflow: hidden;
  background: #fff;
  transition: all 0.2s;
}

.video-card:hover {
  border-color: #409eff;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.15);
}

.video-card.selected {
  border-color: #67c23a;
  background: #f0f9eb;
}

.video-card.disabled {
  opacity: 0.7;
}

.video-checkbox {
  position: absolute;
  top: 8px;
  left: 8px;
  z-index: 10;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 4px;
  padding: 2px;
}

.video-thumbnail {
  position: relative;
  height: 110px;
  background: #1a1a1a;
  cursor: pointer;
}

.video-thumbnail .el-image {
  width: 100%;
  height: 100%;
}

.video-no-cover {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #606266;
}

.video-no-cover.video-processing {
  flex-direction: column;
  gap: 8px;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e7ed 100%);
  color: #409eff;
}

.processing-text {
  font-size: 12px;
  color: #909399;
}

.video-duration {
  position: absolute;
  bottom: 6px;
  right: 6px;
  background: rgba(0, 0, 0, 0.75);
  color: #fff;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
}

.video-status {
  position: absolute;
  top: 6px;
  left: 6px;
}

.play-overlay {
  position: absolute;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(0, 0, 0, 0.3);
  opacity: 0;
  transition: opacity 0.2s;
  color: #fff;
}

.video-thumbnail:hover .play-overlay {
  opacity: 1;
}

.video-info {
  padding: 10px;
}

.video-title {
  font-size: 13px;
  font-weight: 500;
  color: #303133;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.video-meta {
  font-size: 12px;
  color: #909399;
  margin-top: 6px;
  display: flex;
  justify-content: space-between;
}

.video-overlay {
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

.video-card:hover .video-overlay {
  opacity: 1;
}

.video-overlay.active {
  opacity: 1;
  background: rgba(103, 194, 58, 0.3);
}

.pick-text {
  color: #fff;
  font-size: 14px;
  font-weight: 500;
}

.check-icon {
  color: #67c23a;
  font-size: 40px;
  background: #fff;
  border-radius: 50%;
  padding: 4px;
}

.video-actions {
  position: absolute;
  top: 8px;
  right: 8px;
  display: flex;
  gap: 4px;
  opacity: 0;
  transition: opacity 0.2s;
  z-index: 10;
}

.video-card:hover .video-actions {
  opacity: 1;
}

/* Footer */
.dialog-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.selected-info {
  color: #67c23a;
  font-size: 14px;
}

/* 上传 */
.upload-file-info {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #409eff;
}

.file-size {
  color: #909399;
  font-size: 12px;
}

.upload-progress {
  margin-top: 16px;
  display: flex;
  align-items: center;
  gap: 12px;
}

.upload-progress .el-progress {
  flex: 1;
}

.progress-text {
  font-size: 13px;
  color: #606266;
}
</style>
