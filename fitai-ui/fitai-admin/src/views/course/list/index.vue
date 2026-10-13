<template>
  <div class="table-box p-5">
    <vxe-grid ref="xGrid" v-bind="gridOptions">
      <!-- 工具栏按钮 -->
      <template #toolbar_buttons>
        <el-button type="primary" :icon="Plus" @click="handleAdd">新增课程</el-button>
        <el-button :icon="Refresh" @click="handleRefresh">刷新</el-button>
      </template>

      <!-- 封面列 -->
      <template #cover="{ row }">
        <div class="table-thumb-cell">
          <el-image
            v-if="row.coverImage"
            :src="row.coverImage"
            :preview-src-list="[row.coverImage]"
            fit="contain"
            class="table-thumb-cover"
          />
          <span v-else class="text-gray-400">-</span>
        </div>
      </template>

      <!-- 难度列（后端 difficulty 为字符串 "1"/"2"/"3"） -->
      <template #difficulty="{ row }">
        <el-tag v-if="Number(row.difficulty) === 1" type="success">入门</el-tag>
        <el-tag v-else-if="Number(row.difficulty) === 2" type="warning">进阶</el-tag>
        <el-tag v-else-if="Number(row.difficulty) === 3" type="danger">高级</el-tag>
        <span v-else>{{ row.difficulty || "-" }}</span>
      </template>

      <!-- 免费列（后端 isFree：0否 1是） -->
      <template #isFree="{ row }">
        <el-tag v-if="row.isFree === true || row.isFree === '1' || row.isFree === 1" type="success">免费</el-tag>
        <el-tag v-else type="warning">¥{{ row.price ?? 0 }}</el-tag>
      </template>

      <!-- 状态列（后端 status：0上架 1下架 2草稿） -->
      <template #status="{ row }">
        <el-tag v-if="row.status === '0' || row.status === 0" type="success">已上架</el-tag>
        <el-tag v-else-if="row.status === '2' || row.status === 2" type="warning">草稿</el-tag>
        <el-tag v-else type="info">已下架</el-tag>
      </template>

      <!-- 操作列 -->
      <template #action="{ row }">
        <el-button type="primary" link :icon="Edit" @click="handleEdit(row)">编辑</el-button>
        <el-button type="danger" link :icon="Delete" @click="handleDelete(row)">删除</el-button>
      </template>
    </vxe-grid>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑课程' : '新增课程'" width="700px" destroy-on-close>
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
        <el-form-item label="课程标题" prop="title">
          <el-input v-model="formData.title" placeholder="请输入课程标题" />
        </el-form-item>
        <el-form-item label="课程分类" prop="categoryId">
          <el-cascader
            v-model="formData.categoryId"
            :options="categoryOptions"
            :props="{
              value: 'categoryId',
              label: 'categoryName',
              children: 'children',
              checkStrictly: true,
              emitPath: false
            }"
            placeholder="请选择分类"
            clearable
            style="width: 100%"
          />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="难度等级" prop="difficulty">
              <el-select v-model="formData.difficulty" placeholder="请选择难度" style="width: 100%">
                <el-option label="入门" :value="1" />
                <el-option label="进阶" :value="2" />
                <el-option label="高级" :value="3" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="课程时长" prop="duration">
              <el-input-number v-model="formData.duration" :min="1" :max="300" placeholder="分钟" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="消耗卡路里" prop="calories">
              <el-input-number v-model="formData.calories" :min="0" :max="2000" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="所需器材">
              <el-input v-model="formData.equipment" placeholder="如：哑铃、瑜伽垫" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="是否免费">
          <el-switch v-model="formData.isFree" />
        </el-form-item>
        <el-form-item v-if="!formData.isFree" label="课程价格" prop="price">
          <el-input-number v-model="formData.price" :min="0" :precision="2" style="width: 200px" />
        </el-form-item>
        <el-form-item label="关联视频">
          <div class="video-select-area">
            <div v-if="selectedVideo" class="selected-video">
              <el-image
                v-if="selectedVideo.coverUrl"
                :src="selectedVideo.coverUrl"
                class="video-cover"
                fit="cover"
              />
              <div v-else class="video-cover video-cover-placeholder">
                <el-icon :size="22"><VideoCamera /></el-icon>
              </div>
              <span class="video-title">{{ selectedVideo.title }}</span>
              <el-button type="danger" link @click="clearVideo">移除</el-button>
            </div>
            <el-button v-else type="primary" @click="openVideoLibrary">选择视频</el-button>
          </div>
          <VideoLibrary ref="videoLibraryRef" @select="handleVideoSelect" />
        </el-form-item>
        <el-form-item label="封面图片" prop="coverImage">
          <ImageLibrary
            v-model="formData.coverImage"
            folder="course"
            upload-action="/file/upload/course/cover"
          />
        </el-form-item>
        <el-form-item label="课程描述">
          <div class="editor-wrapper">
            <Toolbar
              class="editor-toolbar"
              :editor="editorRef"
              :defaultConfig="editorToolbarConfig"
              :mode="editorMode"
            />
            <Editor
              class="editor-content"
              v-model="descriptionHtml"
              :defaultConfig="editorConfig"
              :mode="editorMode"
              @onCreated="handleEditorCreated"
            />
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="courseList">
import { ref, reactive, shallowRef, watch, onBeforeUnmount } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { Plus, Refresh, Edit, Delete, VideoCamera } from "@element-plus/icons-vue";
import { getCoursePageApi, addCourseApi, updateCourseApi, deleteCourseApi, getCourseCategoryTreeApi } from "@/api/modules/course";
import ImageLibrary from "@/components/ImageLibrary/index.vue";
import VideoLibrary from "@/components/VideoLibrary/index.vue";
import { useUserStore } from "@/stores/modules/user";
import "@wangeditor/editor/dist/css/style.css";
import { Editor, Toolbar } from "@wangeditor/editor-for-vue";

// 富文本编辑器
const userStore = useUserStore();
const editorRef = shallowRef();
const descriptionHtml = ref("");
const editorMode = "default";
const editorToolbarConfig = {};
const editorConfig = {
  placeholder: "请输入课程描述内容...",
  MENU_CONF: {
    uploadImage: {
      server: import.meta.env.VITE_API_URL + "/file/upload/course/image",
      fieldName: "file",
      headers: () => ({
        Authorization: userStore.token
      }),
      customInsert(res, insertFn) {
        if (res.code === 200 && res.data) {
          insertFn(res.data, "", "");
        }
      }
    }
  }
};

// 编辑器创建完成
const handleEditorCreated = editor => {
  editorRef.value = editor;
};

onBeforeUnmount(() => {
  if (editorRef.value) {
    editorRef.value.destroy();
  }
});

const xGrid = ref();
const formRef = ref();
const dialogVisible = ref(false);
const isEdit = ref(false);
const submitLoading = ref(false);
const categoryOptions = ref([]);
const videoLibraryRef = ref();
const selectedVideo = ref(null);

/** 在分类树中按 categoryId 查找分类名 */
const findCategoryName = (nodes, categoryId) => {
  if (!categoryId || !Array.isArray(nodes)) return "";
  for (const node of nodes) {
    if (node.categoryId === categoryId) return node.categoryName || "";
    const childName = findCategoryName(node.children, categoryId);
    if (childName) return childName;
  }
  return "";
};

/**
 * 后端 CrsCourseVO → 前端列表/表单字段
 * courseTitle/coverUrl/durationMin/summary/courseId ↔ title/coverImage/duration/description/id
 */
const normalizeCourseRow = row => {
  if (!row) return row;
  return {
    ...row,
    id: row.courseId ?? row.id,
    title: row.courseTitle ?? row.title ?? "",
    coverImage: row.coverUrl ?? row.coverImage ?? "",
    duration: row.durationMin ?? row.duration ?? 0,
    description: row.summary ?? row.description ?? "",
    // 表单里 isFree 用布尔；列表展示同时兼容字符串
    isFree: row.isFree === true || row.isFree === "1" || row.isFree === 1,
    difficulty: row.difficulty != null && row.difficulty !== "" ? Number(row.difficulty) : 1
  };
};

/**
 * 前端表单 → 后端 AddDTO / UpdateDTO 字段名与类型
 */
const buildCoursePayload = (form, { isEdit = false } = {}) => {
  const payload = {
    categoryId: form.categoryId,
    categoryName: findCategoryName(categoryOptions.value, form.categoryId) || form.categoryName || undefined,
    courseTitle: form.title,
    coverUrl: form.coverImage || undefined,
    coverOssId: form.coverOssId || undefined,
    difficulty: form.difficulty != null ? String(form.difficulty) : undefined,
    durationMin: form.duration,
    calories: form.calories,
    equipment: form.equipment || undefined,
    isFree: form.isFree ? "1" : "0",
    price: form.isFree ? 0 : form.price ?? 0,
    videoId: form.videoId || undefined,
    summary: form.description || undefined
  };
  if (isEdit) {
    payload.courseId = form.id ?? form.courseId;
  }
  return payload;
};

// 监听弹窗关闭时清理状态
watch(dialogVisible, val => {
  if (!val) {
    descriptionHtml.value = "";
    editorRef.value = null;
  }
});

// 表单数据（页面内部仍用友好字段名，提交时再映射）
const formData = reactive({
  id: null,
  title: "",
  categoryId: null,
  categoryName: "",
  difficulty: 1,
  duration: 30,
  calories: 200,
  equipment: "",
  isFree: true,
  price: 0,
  videoId: null,
  coverImage: "",
  coverOssId: null,
  description: ""
});

const formRules = {
  title: [{ required: true, message: "请输入课程标题", trigger: "blur" }],
  categoryId: [{ required: true, message: "请选择课程分类", trigger: "change" }],
  difficulty: [{ required: true, message: "请选择难度等级", trigger: "change" }],
  duration: [{ required: true, message: "请输入课程时长", trigger: "blur" }],
  coverImage: [{ required: true, message: "请上传封面图片", trigger: "change" }]
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
  exportConfig: {
    types: ["xlsx", "csv", "pdf", "html", "xml", "txt"]
  },
  formConfig: {
    items: [
      { field: "keyword", title: "课程标题", span: 6, itemRender: { name: "$input", props: { placeholder: "请输入标题" } } },
      {
        field: "status",
        title: "状态",
        span: 6,
        itemRender: {
          name: "$select",
          options: [
            { label: "全部", value: "" },
            // 与后端一致：0上架 1下架
            { label: "已上架", value: "0" },
            { label: "已下架", value: "1" }
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
    response: { result: "list", total: "total" },
    ajax: {
      query: async ({ page, form }) => {
        const params = {
          pageNum: page.currentPage,
          pageSize: page.pageSize,
          keyword: form.keyword || undefined,
          status: form.status !== "" && form.status != null ? String(form.status) : undefined
        };
        try {
          const res = await getCoursePageApi(params);
          const list = (res.data?.records || []).map(normalizeCourseRow);
          return { list, total: res.data?.total || 0 };
        } catch (error) {
          console.error("获取课程列表失败:", error);
          return { list: [], total: 0 };
        }
      }
    }
  },
  columns: [
    { type: "seq", width: 60, title: "序号" },
    { field: "coverImage", title: "封面", width: 140, slots: { default: "cover" } },
    { field: "title", title: "课程标题", minWidth: 200 },
    { field: "categoryName", title: "分类", width: 120 },
    { field: "difficulty", title: "难度", width: 80, slots: { default: "difficulty" } },
    { field: "duration", title: "时长(分钟)", width: 100 },
    { field: "calories", title: "卡路里", width: 80 },
    { field: "isFree", title: "价格", width: 100, slots: { default: "isFree" } },
    { field: "viewCount", title: "观看数", width: 80 },
    { field: "likeCount", title: "点赞数", width: 80 },
    { field: "status", title: "状态", width: 80, slots: { default: "status" } },
    { title: "操作", width: 150, fixed: "right", slots: { default: "action" } }
  ]
});

// 加载分类
const loadCategories = async () => {
  try {
    const res = await getCourseCategoryTreeApi();
    categoryOptions.value = res.data || [];
  } catch (error) {
    console.error("获取课程分类失败:", error);
    categoryOptions.value = [];
  }
};

loadCategories();

// 刷新
const handleRefresh = () => {
  xGrid.value.commitProxy("query");
};

// 新增
const handleAdd = () => {
  isEdit.value = false;
  selectedVideo.value = null;
  descriptionHtml.value = "";
  Object.assign(formData, {
    id: null,
    title: "",
    categoryId: null,
    categoryName: "",
    difficulty: 1,
    duration: 30,
    calories: 200,
    equipment: "",
    isFree: true,
    price: 0,
    videoId: null,
    coverImage: "",
    coverOssId: null,
    description: ""
  });
  dialogVisible.value = true;
};

// 编辑
const handleEdit = row => {
  isEdit.value = true;
  const normalized = normalizeCourseRow(row);
  Object.assign(formData, {
    id: normalized.id,
    title: normalized.title,
    categoryId: normalized.categoryId,
    categoryName: normalized.categoryName || "",
    difficulty: normalized.difficulty,
    duration: normalized.duration,
    calories: normalized.calories ?? 0,
    equipment: normalized.equipment || "",
    isFree: normalized.isFree,
    price: Number(normalized.price) || 0,
    videoId: normalized.videoId || null,
    coverImage: normalized.coverImage,
    coverOssId: normalized.coverOssId || null,
    description: normalized.description
  });
  descriptionHtml.value = normalized.description || "";
  if (normalized.videoId) {
    selectedVideo.value = {
      id: normalized.videoId,
      title: row.videoTitle || "已关联视频",
      coverUrl: row.videoCoverUrl || null
    };
  } else {
    selectedVideo.value = null;
  }
  dialogVisible.value = true;
};

// 提交
const handleSubmit = async () => {
  try {
    await formRef.value.validate();
    submitLoading.value = true;

    formData.description = descriptionHtml.value;
    const payload = buildCoursePayload(formData, { isEdit: isEdit.value });

    if (isEdit.value) {
      await updateCourseApi(payload);
    } else {
      await addCourseApi(payload);
    }

    ElMessage.success(isEdit.value ? "编辑成功" : "新增成功");
    dialogVisible.value = false;
    handleRefresh();
  } catch (error) {
    console.error("提交失败:", error);
    if (error !== false) ElMessage.error(error?.msg || "操作失败，请重试");
  } finally {
    submitLoading.value = false;
  }
};

// 删除
const handleDelete = row => {
  ElMessageBox.confirm("确定要删除该课程吗？", "提示", {
    type: "warning"
  }).then(async () => {
    try {
      await deleteCourseApi(row.id ?? row.courseId);
      ElMessage.success("删除成功");
      handleRefresh();
    } catch (error) {
      console.error("删除失败:", error);
      ElMessage.error("删除失败，请重试");
    }
  });
};

// 视频选择
const openVideoLibrary = () => {
  videoLibraryRef.value?.open();
};

const handleVideoSelect = video => {
  if (video) {
    selectedVideo.value = video;
    formData.videoId = video.id;
  }
};

const clearVideo = () => {
  selectedVideo.value = null;
  formData.videoId = null;
};

</script>

<style scoped>
.table-box {
  background: #fff;
  border-radius: 8px;
}

.video-select-area {
  display: flex;
  align-items: center;
}

.selected-video {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 12px;
  background: #f5f7fa;
  border-radius: 6px;
}

.selected-video .video-cover {
  width: 60px;
  height: 40px;
  border-radius: 4px;
  background: #e0e0e0;
  flex-shrink: 0;
}

.selected-video .video-cover-placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--el-text-color-secondary);
}

.selected-video .video-title {
  color: #333;
  font-size: 14px;
}

/* 富文本编辑器 */
.editor-wrapper {
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  width: 100%;
}

.editor-toolbar {
  border-bottom: 1px solid #dcdfe6;
}

.editor-content {
  height: 350px;
  overflow-y: auto;
}
</style>
