<template>
  <div class="table-box p-5">
    <vxe-grid ref="xGrid" v-bind="gridOptions">
      <!-- 工具栏按钮 -->
      <template #toolbar_buttons>
        <el-button type="primary" :icon="Upload" @click="handleUpload">上传视频</el-button>
        <el-button type="success" :icon="Film" @click="videoLibraryRef?.open()">视频库</el-button>
        <el-button :icon="Refresh" @click="handleRefresh">刷新</el-button>
        <el-button type="warning" :icon="RefreshRight" :loading="syncing" @click="handleSyncAll">同步状态</el-button>
      </template>

      <!-- 封面列 -->
      <template #cover="{ row }">
        <div v-if="row.coverUrl" class="table-thumb-cell">
          <el-image :src="row.coverUrl" :preview-src-list="[row.coverUrl]" fit="contain" class="table-thumb-cover" />
        </div>
        <!-- 转码中显示加载动画 -->
        <div v-else-if="row.status === 1" class="table-thumb-cell">
          <div class="table-thumb-cover flex flex-col items-center justify-center gap-1 bg-gray-100">
            <el-icon :size="16" class="is-loading" color="#409eff"><Loading /></el-icon>
            <span class="text-xs text-gray-400">生成中</span>
          </div>
        </div>
        <!-- 无封面显示占位图 -->
        <div v-else class="table-thumb-cell">
          <div class="table-thumb-cover flex items-center justify-center bg-gray-200">
            <el-icon :size="20" color="#999"><VideoCamera /></el-icon>
          </div>
        </div>
      </template>

      <!-- 文件大小列 -->
      <template #size="{ row }">
        {{ formatSize(row.size) }}
      </template>

      <!-- 时长列 -->
      <template #duration="{ row }">
        {{ formatDuration(row.duration) }}
      </template>

      <!-- 状态列 -->
      <template #status="{ row }">
        <el-tag v-if="row.status === 0" type="warning">上传中</el-tag>
        <el-tag v-else-if="row.status === 1" type="info">转码中</el-tag>
        <el-tag v-else-if="row.status === 2" type="success">已就绪</el-tag>
        <el-tag v-else type="danger">失败</el-tag>
      </template>

      <!-- 操作列 -->
      <template #action="{ row }">
        <el-button v-if="row.status === 2" type="primary" link :icon="VideoPlay" @click="handlePlay(row)">播放</el-button>
        <el-button v-if="row.status !== 2" type="warning" link :icon="RefreshRight" @click="handleSync(row)">同步</el-button>
        <el-button type="primary" link :icon="Edit" @click="handleEdit(row)">编辑</el-button>
        <el-button type="danger" link :icon="Delete" @click="handleDelete(row)">删除</el-button>
      </template>
    </vxe-grid>

    <!-- 上传弹窗 -->
    <el-dialog v-model="uploadVisible" title="上传视频" width="600px" destroy-on-close>
      <el-form ref="uploadFormRef" :model="uploadForm" :rules="uploadRules" label-width="100px">
        <el-form-item label="视频标题" prop="title">
          <el-input v-model="uploadForm.title" placeholder="请输入视频标题" />
        </el-form-item>
        <el-form-item label="选择视频" prop="file">
          <el-upload
            ref="uploadRef"
            class="w-full"
            drag
            :auto-upload="false"
            :limit="1"
            accept="video/*"
            :on-change="handleFileChange"
            :on-exceed="handleExceed"
          >
            <el-icon class="el-icon--upload"><UploadFilled /></el-icon>
            <div class="el-upload__text">将视频拖到此处，或<em>点击上传</em></div>
            <template #tip>
              <div class="el-upload__tip">支持 MP4、AVI、MOV 等格式，最大 2GB</div>
            </template>
          </el-upload>
          <div class="video-library-tip">
            <span class="or-text">—— 或 ——</span>
            <el-button type="success" :icon="Film" @click="openVideoLibraryForUpload">从视频库选择</el-button>
          </div>
        </el-form-item>
        <el-form-item label="所属分类">
          <el-select v-model="uploadForm.categoryId" placeholder="请选择分类" clearable style="width: 100%">
            <el-option v-for="cat in categoryOptions" :key="cat.id" :label="cat.name" :value="cat.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="视频描述">
          <el-input v-model="uploadForm.description" type="textarea" :rows="3" placeholder="请输入视频描述" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="uploadVisible = false">取消</el-button>
        <el-button type="primary" :loading="uploading" @click="handleUploadSubmit">
          {{ uploading ? `上传中 ${uploadProgress}%` : '开始上传' }}
        </el-button>
      </template>
    </el-dialog>

    <!-- 编辑弹窗 -->
    <el-dialog v-model="editVisible" title="编辑视频" width="600px" destroy-on-close>
      <el-form ref="editFormRef" :model="editForm" :rules="editRules" label-width="100px">
        <el-form-item label="视频标题" prop="title">
          <el-input v-model="editForm.title" placeholder="请输入视频标题" />
        </el-form-item>
        <el-form-item label="所属分类">
          <el-select v-model="editForm.categoryId" placeholder="请选择分类" clearable style="width: 100%">
            <el-option v-for="cat in categoryOptions" :key="cat.id" :label="cat.name" :value="cat.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="封面预览">
          <div class="cover-preview-only">
            <el-image
              v-if="editForm.coverUrl"
              :src="editForm.coverUrl"
              fit="cover"
              style="width: 160px; height: 90px; border-radius: 4px"
              :preview-src-list="[editForm.coverUrl]"
            />
            <div v-else class="cover-placeholder-small">
              <el-icon :size="20"><VideoCamera /></el-icon>
              <span>自动截取</span>
            </div>
            <span class="cover-tip-text">封面由系统自动截取视频第一帧</span>
          </div>
        </el-form-item>
        <el-form-item label="更换视频">
          <el-button type="success" :icon="Film" @click="openVideoLibraryForEdit">从视频库选择</el-button>
          <span class="change-video-tip">选择后将切换到编辑该视频</span>
        </el-form-item>
        <el-form-item label="视频描述">
          <el-input v-model="editForm.description" type="textarea" :rows="3" placeholder="请输入视频描述" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editVisible = false">取消</el-button>
        <el-button type="primary" :loading="editLoading" @click="handleEditSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 播放弹窗 -->
    <el-dialog v-model="playVisible" :title="playTitle" width="800px" destroy-on-close @closed="onPlayDialogClosed">
      <video
        v-if="playUrl"
        :src="playUrl"
        controls
        autoplay
        style="width: 100%; max-height: 500px"
        controlsList="nodownload"
      />
    </el-dialog>

    <!-- 视频库组件 -->
    <VideoLibrary ref="videoLibraryRef" @select="handleVideoLibrarySelect" />

    <!-- 视频库选择弹窗（旧代码，保留给内部使用） -->
    <el-dialog v-model="videoLibraryVisible" title="选择视频" width="900px" destroy-on-close style="display: none;">
      <div class="video-library">
        <div class="video-library-toolbar">
          <el-input v-model="videoLibrarySearch" placeholder="搜索视频..." style="width: 200px" clearable />
          <el-button :icon="Refresh" @click="loadVideoLibrary">刷新</el-button>
        </div>
        <div class="video-library-grid" v-loading="videoLibraryLoading">
          <div
            v-for="video in filteredVideoLibrary"
            :key="video.id"
            class="video-library-item"
            :class="{ selected: selectedLibraryVideo?.id === video.id }"
            @click="selectedLibraryVideo = video"
          >
            <div class="video-thumbnail">
              <el-image v-if="video.coverUrl" :src="video.coverUrl" fit="cover" />
              <div v-else class="video-no-cover">
                <el-icon :size="32"><VideoCamera /></el-icon>
              </div>
              <div class="video-duration">{{ formatDuration(video.duration) }}</div>
            </div>
            <div class="video-info">
              <div class="video-title" :title="video.title">{{ video.title }}</div>
              <div class="video-meta">{{ formatSize(video.size) }}</div>
            </div>
            <el-icon v-if="selectedLibraryVideo?.id === video.id" class="check-icon"><Check /></el-icon>
          </div>
          <el-empty v-if="filteredVideoLibrary.length === 0" description="暂无视频" />
        </div>
      </div>
      <template #footer>
        <el-button @click="videoLibraryVisible = false">取消</el-button>
        <el-button type="primary" :disabled="!selectedLibraryVideo" @click="confirmVideoSelect">确定选择</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="courseVideo">
import { ref, reactive, computed, onUnmounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { Upload, Refresh, RefreshRight, Edit, Delete, VideoPlay, VideoCamera, UploadFilled, Check, Film, Loading } from "@element-plus/icons-vue";
import VideoLibrary from "@/components/VideoLibrary/index.vue";
import { getVideoPageApi, getUploadCredentialApi, uploadCompleteApi, updateVideoApi, deleteVideoApi, getPlayUrlApi, syncVideoApi, syncVideoBatchApi, getVideoCategoryListApi } from "@/api/modules/video";
import { uploadCover } from "@/api/modules/upload";
import axios from "axios";
import { uploadToAliyunVod, isAliyunVodCredential, isMinioCredential } from "@/utils/aliyunVodUpload";
import { captureVideoFrame } from "@/utils/videoCapture";

const xGrid = ref();
const uploadFormRef = ref();
const editFormRef = ref();
const uploadRef = ref();
const uploadVisible = ref(false);
const editVisible = ref(false);
const playVisible = ref(false);
const uploading = ref(false);
const editLoading = ref(false);
const syncing = ref(false);
const uploadProgress = ref(0);
const categoryOptions = ref([]);
const playUrl = ref("");
const playTitle = ref("");

// 视频库组件
const videoLibraryRef = ref();

// 视频库
const videoLibraryVisible = ref(false);
const videoLibraryLoading = ref(false);
const videoLibrarySearch = ref("");
const videoLibraryList = ref([]);
const selectedLibraryVideo = ref(null);

// 上传表单
const uploadForm = reactive({
  title: "",
  file: null,
  categoryId: null,
  description: ""
});

const uploadRules = {
  title: [{ required: true, message: "请输入视频标题", trigger: "blur" }]
};

// 编辑表单
const editForm = reactive({
  id: null,
  title: "",
  categoryId: null,
  coverUrl: "",
  description: ""
});

const editRules = {
  title: [{ required: true, message: "请输入视频标题", trigger: "blur" }]
};

// 表格配置
const gridOptions = reactive({
  border: true,
  showOverflow: true,
  height: 600,
  loading: false,
  rowConfig: { keyField: "id", isHover: true, height: 72 },
  columnConfig: { resizable: true },
  pagerConfig: {
    pageSize: 10,
    pageSizes: [10, 20, 50, 100]
  },
  toolbarConfig: {
    slots: { buttons: "toolbar_buttons" },
    refresh: true,
    zoom: false,
    export: true,
    custom: true
  },
  formConfig: {
    items: [
      { field: "keyword", title: "视频标题", span: 6, itemRender: { name: "$input", props: { placeholder: "请输入标题" } } },
      {
        field: "status",
        title: "状态",
        span: 6,
        itemRender: {
          name: "$select",
          options: [
            { label: "全部", value: "" },
            { label: "上传中", value: 0 },
            { label: "转码中", value: 1 },
            { label: "已就绪", value: 2 },
            { label: "失败", value: 3 }
          ],
          props: { placeholder: "请选择状态" }
        }
      },
      {
        span: 6,
        align: "center",
        itemRender: {
          name: "$buttons",
          children: [
            { props: { type: "submit", content: "搜索", status: "primary" } },
            { props: { type: "reset", content: "重置" } }
          ]
        }
      }
    ]
  },
  proxyConfig: {
    seq: true,
    form: true,
    // 关闭代理查询时的表格遮罩「加载中」，避免上传后定时刷新每 5 秒闪一次；全屏 axios loading 已对列表请求关闭
    showLoading: false,
    response: { result: "list", total: "total" },
    ajax: {
      query: async ({ page, form }) => {
        const params = {
          pageNum: page.currentPage,
          pageSize: page.pageSize,
          ...form
        };
        try {
          const res = await getVideoPageApi(params, { loading: false });
          return { list: res.data?.records || [], total: res.data?.total || 0 };
        } catch (error) {
          console.error("获取视频列表失败:", error);
          return { list: [], total: 0 };
        }
      }
    }
  },
  columns: [
    { type: "seq", width: 60, title: "序号" },
    { field: "coverUrl", title: "封面", width: 140, slots: { default: "cover" } },
    { field: "title", title: "视频标题", minWidth: 200 },
    { field: "originalName", title: "文件名", width: 200, showOverflow: true },
    { field: "categoryName", title: "分类", width: 120 },
    { field: "duration", title: "时长", width: 100, slots: { default: "duration" } },
    { field: "size", title: "大小", width: 100, slots: { default: "size" } },
    { field: "status", title: "状态", width: 100, slots: { default: "status" } },
    { field: "createTime", title: "上传时间", width: 170 },
    { title: "操作", width: 200, fixed: "right", slots: { default: "action" } }
  ]
});

// 加载分类
const loadCategories = async () => {
  try {
    const res = await getVideoCategoryListApi();
    categoryOptions.value = res.data || [];
  } catch (error) {
    console.error("获取分类失败:", error);
    categoryOptions.value = [];
  }
};

loadCategories();

// 页面加载时自动同步未就绪的视频
const autoSyncOnLoad = async () => {
  try {
    // 先检查是否有未就绪的视频
    const res = await getVideoPageApi({ pageNum: 1, pageSize: 100, status: 1 }, { loading: false });
    const pendingCount = res.data?.total || 0;
    if (pendingCount > 0) {
      await syncVideoBatchApi({ loading: false });
      // 同步完成后刷新表格
      setTimeout(() => {
        xGrid.value?.commitProxy("query");
      }, 500);
    }
  } catch (error) {
    // 静默失败
  }
};
autoSyncOnLoad();

// 格式化文件大小
const formatSize = size => {
  if (!size) return "-";
  if (size < 1024) return size + " B";
  if (size < 1024 * 1024) return (size / 1024).toFixed(2) + " KB";
  if (size < 1024 * 1024 * 1024) return (size / 1024 / 1024).toFixed(2) + " MB";
  return (size / 1024 / 1024 / 1024).toFixed(2) + " GB";
};

// 格式化时长
const formatDuration = duration => {
  if (!duration) return "-";
  const hours = Math.floor(duration / 3600);
  const minutes = Math.floor((duration % 3600) / 60);
  const seconds = duration % 60;
  if (hours > 0) {
    return `${hours}:${String(minutes).padStart(2, "0")}:${String(seconds).padStart(2, "0")}`;
  }
  return `${minutes}:${String(seconds).padStart(2, "0")}`;
};

// 刷新
const handleRefresh = () => {
  xGrid.value.commitProxy("query");
};

// 上传成功后的自动同步 + 刷新（无上传中/转码中即停，避免一直轮询）
let autoRefreshTimer = null;
let autoRefreshCount = 0;
let autoRefreshRunning = false;
const AUTO_REFRESH_MS = 5000;
const AUTO_REFRESH_MAX = 60;

const startAutoRefresh = () => {
  stopAutoRefresh();
  autoRefreshCount = 0;

  const tick = async () => {
    if (autoRefreshRunning) return;
    autoRefreshRunning = true;
    autoRefreshCount++;
    try {
      await syncVideoBatchApi({ loading: false });
      const res = await getVideoPageApi({ pageNum: 1, pageSize: 500 }, { loading: false });
      const records = res.data?.records || [];
      const hasPending = records.some(r => r.status === 0 || r.status === 1);
      handleRefresh();
      if (!hasPending) {
        stopAutoRefresh();
        return;
      }
    } catch (e) {
      console.error("自动同步视频失败:", e);
    } finally {
      autoRefreshRunning = false;
    }
    if (autoRefreshCount >= AUTO_REFRESH_MAX) {
      stopAutoRefresh();
    }
  };

  autoRefreshTimer = setInterval(tick, AUTO_REFRESH_MS);
  tick();
};

// 停止自动刷新
const stopAutoRefresh = () => {
  if (autoRefreshTimer) {
    clearInterval(autoRefreshTimer);
    autoRefreshTimer = null;
  }
};

// 组件卸载时清除定时器
onUnmounted(() => {
  stopAutoRefresh();
});

// 同步单个视频状态
const handleSync = async row => {
  try {
    await syncVideoApi(row.id);
    ElMessage.success("同步成功");
    handleRefresh();
  } catch (error) {
    ElMessage.error("同步失败");
  }
};

// 批量同步所有未就绪视频
const handleSyncAll = async () => {
  syncing.value = true;
  try {
    const res = await syncVideoBatchApi();
    ElMessage.success(res.data || "同步完成");
    handleRefresh();
  } catch (error) {
    ElMessage.error("同步失败");
  } finally {
    syncing.value = false;
  }
};

// 上传
const handleUpload = () => {
  Object.assign(uploadForm, {
    title: "",
    file: null,
    categoryId: null,
    description: ""
  });
  uploadProgress.value = 0;
  uploadVisible.value = true;
};

// 文件变化
const handleFileChange = file => {
  uploadForm.file = file.raw;
  if (!uploadForm.title) {
    uploadForm.title = file.name.replace(/\.[^/.]+$/, "");
  }
};

// 超出限制
const handleExceed = () => {
  ElMessage.warning("只能上传一个视频文件");
};

// 提交上传
const handleUploadSubmit = async () => {
  if (!uploadForm.file) {
    ElMessage.warning("请选择要上传的视频文件");
    return;
  }

  try {
    await uploadFormRef.value.validate();
    uploading.value = true;
    uploadProgress.value = 0;

    // 1. 获取上传凭证
    const credentialRes = await getUploadCredentialApi({
      filename: uploadForm.file.name,
      title: uploadForm.title
    });

    const credential = credentialRes.data;
    const { videoId } = credential;

    // 2. 根据凭证类型选择上传方式
    if (isMinioCredential(credential)) {
      // MinIO 模式：直接 PUT 上传
      await axios.put(credential.uploadUrl, uploadForm.file, {
        headers: {
          "Content-Type": uploadForm.file.type || "video/mp4"
        },
        onUploadProgress: progressEvent => {
          uploadProgress.value = Math.round((progressEvent.loaded * 100) / progressEvent.total);
        }
      });
    } else if (isAliyunVodCredential(credential)) {
      // 阿里云 VOD 模式：使用 OSS SDK 分片上传
      await uploadToAliyunVod(credential, uploadForm.file, percent => {
        uploadProgress.value = percent;
      });
    } else {
      throw new Error("无效的上传凭证");
    }

    // 3. MinIO：浏览器截取首帧并上传封面；阿里云 VOD 由云端生成封面
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
        console.warn("视频封面截取失败，将无封面与时长", e);
      }
    }

    // 4. 通知后端上传完成
    await uploadCompleteApi(videoId, {
      size: uploadForm.file.size,
      categoryId: uploadForm.categoryId,
      description: uploadForm.description,
      duration,
      coverUrl
    });

    if (isMinioCredential(credential)) {
      ElMessage.success(coverUrl ? "上传成功" : "上传成功，封面截取失败，可重新上传或换格式重试");
    } else {
      ElMessage.success("上传成功，封面正在自动生成中...");
    }
    uploadVisible.value = false;
    handleRefresh();
    
    // 启动自动刷新，等待封面生成
    startAutoRefresh();
  } catch (error) {
    console.error("上传失败:", error);
    ElMessage.error(error.message || "上传失败，请重试");
  } finally {
    uploading.value = false;
  }
};

// 编辑
const handleEdit = row => {
  Object.assign(editForm, {
    id: row.id,
    title: row.title,
    categoryId: row.categoryId,
    coverUrl: row.coverUrl,
    description: row.description
  });
  editVisible.value = true;
};

// 提交编辑
const handleEditSubmit = async () => {
  try {
    await editFormRef.value.validate();
    editLoading.value = true;

    await updateVideoApi(editForm.id, editForm);

    ElMessage.success("编辑成功");
    editVisible.value = false;
    handleRefresh();
  } catch (error) {
    console.error("编辑失败:", error);
    ElMessage.error("编辑失败，请重试");
  } finally {
    editLoading.value = false;
  }
};

// 删除
const handleDelete = row => {
  ElMessageBox.confirm("确定要删除该视频吗？删除后无法恢复", "提示", {
    type: "warning"
  }).then(async () => {
    try {
      await deleteVideoApi(row.id);
      ElMessage.success("删除成功");
      handleRefresh();
    } catch (error) {
      console.error("删除失败:", error);
      ElMessage.error("删除失败，请重试");
    }
  });
};

// 播放
const handlePlay = async row => {
  try {
    const res = await getPlayUrlApi(row.id);
    playUrl.value = res.data;
    playTitle.value = row.title;
    playVisible.value = true;
  } catch (error) {
    console.error("获取播放地址失败:", error);
    ElMessage.error("获取播放地址失败");
  }
};

// 播放弹窗关闭后移除焦点，避免固定列内 focus 边框被裁剪
const onPlayDialogClosed = () => {
  playUrl.value = "";
  requestAnimationFrame(() => {
    document.activeElement?.blur?.();
  });
};

// 视频库打开来源：'upload' 或 'edit'
const videoLibrarySource = ref("upload");

// 从上传弹窗打开视频库
const openVideoLibraryForUpload = () => {
  videoLibrarySource.value = "upload";
  uploadVisible.value = false;
  videoLibraryRef.value?.open();
};

// 从编辑弹窗打开视频库
const openVideoLibraryForEdit = () => {
  videoLibrarySource.value = "edit";
  editVisible.value = false;
  videoLibraryRef.value?.open();
};

// 视频库选择回调
const handleVideoLibrarySelect = video => {
  if (videoLibrarySource.value === "edit") {
    // 切换到编辑选中的视频
    Object.assign(editForm, {
      id: video.id,
      title: video.title,
      categoryId: video.categoryId,
      coverUrl: video.coverUrl,
      description: video.description || ""
    });
    editVisible.value = true;
    ElMessage.success(`已切换到编辑：${video.title}`);
  } else {
    // 从上传弹窗选择，刷新列表
    ElMessage.success(`已选择视频：${video.title}`);
    handleRefresh();
  }
};

// ==================== 视频库 ====================
const filteredVideoLibrary = computed(() => {
  if (!videoLibrarySearch.value) {
    return videoLibraryList.value;
  }
  const keyword = videoLibrarySearch.value.toLowerCase();
  return videoLibraryList.value.filter(v => v.title.toLowerCase().includes(keyword));
});

// 加载视频库（只显示已就绪的视频）
const loadVideoLibrary = async () => {
  videoLibraryLoading.value = true;
  try {
    const res = await getVideoPageApi({ pageNum: 1, pageSize: 100, status: 2 });
    videoLibraryList.value = res.data?.records || [];
  } catch (error) {
    console.error("加载视频库失败:", error);
  } finally {
    videoLibraryLoading.value = false;
  }
};

// 打开视频库
const openVideoLibrary = () => {
  selectedLibraryVideo.value = null;
  videoLibrarySearch.value = "";
  videoLibraryVisible.value = true;
  loadVideoLibrary();
};

// 确认选择视频
const confirmVideoSelect = () => {
  if (selectedLibraryVideo.value) {
    videoLibraryVisible.value = false;
    // 可以触发一个事件，让外部使用
    ElMessage.success(`已选择视频：${selectedLibraryVideo.value.title}`);
  }
};

// 暴露视频库方法供外部使用
defineExpose({
  openVideoLibrary,
  getSelectedVideo: () => selectedLibraryVideo.value
});
</script>

<style scoped>
.table-box {
  background: #fff;
  border-radius: 8px;
}

/* 封面编辑器 */
.cover-editor {
  display: flex;
  gap: 16px;
  align-items: flex-start;
}

.cover-preview {
  flex-shrink: 0;
}

.cover-placeholder {
  width: 160px;
  height: 90px;
  background: #f5f7fa;
  border: 1px dashed #dcdfe6;
  border-radius: 4px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #909399;
  gap: 4px;
}

.cover-placeholder span {
  font-size: 12px;
}

.cover-actions {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.cover-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}

/* 视频库 */
.video-library-toolbar {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
}

.video-library-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  max-height: 500px;
  overflow-y: auto;
}

.video-library-item {
  border: 2px solid #ebeef5;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.2s;
  position: relative;
}

.video-library-item:hover {
  border-color: #409eff;
  box-shadow: 0 2px 12px rgba(64, 158, 255, 0.2);
}

.video-library-item.selected {
  border-color: #67c23a;
  background: #f0f9eb;
}

.video-thumbnail {
  position: relative;
  height: 100px;
  background: #f5f7fa;
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
  color: #c0c4cc;
}

.video-duration {
  position: absolute;
  bottom: 4px;
  right: 4px;
  background: rgba(0, 0, 0, 0.7);
  color: #fff;
  padding: 2px 6px;
  border-radius: 4px;
  font-size: 12px;
}

.video-info {
  padding: 8px;
}

.video-title {
  font-size: 13px;
  font-weight: 500;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.video-meta {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}

.check-icon {
  position: absolute;
  top: 8px;
  right: 8px;
  color: #67c23a;
  font-size: 20px;
  background: #fff;
  border-radius: 50%;
}

/* 视频库入口 */
.video-library-tip {
  margin-top: 16px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
}

.video-library-tip .or-text {
  color: #c0c4cc;
  font-size: 12px;
}

/* 封面预览（只读） */
.cover-preview-only {
  display: flex;
  align-items: center;
  gap: 12px;
}

.cover-placeholder-small {
  width: 160px;
  height: 90px;
  background: #f5f7fa;
  border: 1px dashed #dcdfe6;
  border-radius: 4px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #909399;
  gap: 4px;
  font-size: 12px;
}

.cover-tip-text {
  color: #909399;
  font-size: 12px;
}

.change-video-tip {
  margin-left: 12px;
  color: #909399;
  font-size: 12px;
}
</style>
