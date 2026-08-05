<template>
  <div class="table-box p-5">
    <vxe-grid ref="xGrid" v-bind="gridOptions">
      <template #toolbar_buttons>
        <el-button type="primary" :icon="Plus" @click="handleAdd">新增动作</el-button>
        <el-button :icon="Refresh" @click="handleRefresh">刷新</el-button>
      </template>

      <template #cover="{ row }">
        <div v-if="row.coverImage" class="table-thumb-cell">
          <el-image :src="row.coverImage" :preview-src-list="[row.coverImage]" fit="contain" class="table-thumb-cover" />
        </div>
        <span v-else class="text-gray-400 text-sm">暂无</span>
      </template>

      <template #difficulty="{ row }">
        <el-tag v-if="row.difficulty === 1" type="success">入门</el-tag>
        <el-tag v-else-if="row.difficulty === 2" type="warning">进阶</el-tag>
        <el-tag v-else-if="row.difficulty === 3" type="danger">高级</el-tag>
        <span v-else class="text-gray-400">-</span>
      </template>

      <template #status="{ row }">
        <el-tag v-if="row.status === 1" type="success">已上架</el-tag>
        <el-tag v-else type="info">已下架</el-tag>
      </template>

      <template #action="{ row }">
        <el-button type="primary" link :icon="Edit" @click="handleEdit(row)">编辑</el-button>
        <el-button type="danger" link :icon="Delete" @click="handleDelete(row)">删除</el-button>
      </template>
    </vxe-grid>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑动作' : '新增动作'" width="720px" destroy-on-close>
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="120px">
        <el-form-item label="动作名称" prop="name">
          <el-input v-model="formData.name" placeholder="如：俯卧撑、深蹲" />
        </el-form-item>
        <el-form-item label="目标肌群">
          <el-select
            v-model="formData.muscleGroup"
            placeholder="请选择或留空填自定义"
            filterable
            allow-create
            clearable
            style="width: 100%"
          >
            <el-option label="胸部" value="chest" />
            <el-option label="背部" value="back" />
            <el-option label="肩部" value="shoulder" />
            <el-option label="腿部" value="leg" />
            <el-option label="手臂" value="arm" />
            <el-option label="核心/腹部" value="core" />
            <el-option label="全身" value="full" />
          </el-select>
        </el-form-item>
        <el-form-item label="所需器材">
          <el-select v-model="formData.equipment" placeholder="请选择" clearable style="width: 100%">
            <el-option label="徒手" value="none" />
            <el-option label="哑铃" value="dumbbell" />
            <el-option label="杠铃" value="barbell" />
            <el-option label="器械" value="machine" />
            <el-option label="弹力带" value="band" />
          </el-select>
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="难度" prop="difficulty">
              <el-select v-model="formData.difficulty" placeholder="请选择" style="width: 100%">
                <el-option label="入门" :value="1" />
                <el-option label="进阶" :value="2" />
                <el-option label="高级" :value="3" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="卡路里/分钟">
              <el-input-number v-model="formData.caloriesPerMin" :min="0" :max="100" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="封面图片">
          <ImageLibrary
            v-model="formData.coverImage"
            folder="exercise"
            upload-action="/file/upload/exercise/cover"
          />
        </el-form-item>
        <el-form-item label="关联视频">
          <div class="video-select-area">
            <div v-if="selectedVideo || formData.videoUrl" class="selected-video">
              <img :src="selectedVideo?.coverUrl || '/video-placeholder.png'" class="video-cover" alt="" />
              <span class="video-title">{{ selectedVideo?.title || "已关联视频" }}</span>
              <el-button type="danger" link @click="clearVideo">移除</el-button>
            </div>
            <el-button v-else type="primary" @click="openVideoLibrary">视频库</el-button>
          </div>
          <VideoLibrary ref="videoLibraryRef" @select="handleVideoSelect" />
        </el-form-item>
        <el-form-item label="动作描述">
          <div class="editor-wrapper">
            <Toolbar
              class="editor-toolbar"
              :editor="editorRefDesc"
              :defaultConfig="editorToolbarConfig"
              :mode="editorMode"
            />
            <Editor
              class="editor-content"
              v-model="descriptionHtml"
              :defaultConfig="editorConfig"
              :mode="editorMode"
              @onCreated="handleEditorDescCreated"
            />
          </div>
        </el-form-item>
        <el-form-item label="动作要点">
          <div class="editor-wrapper">
            <Toolbar
              class="editor-toolbar"
              :editor="editorRefTips"
              :defaultConfig="editorToolbarConfig"
              :mode="editorMode"
            />
            <Editor
              class="editor-content"
              v-model="tipsHtml"
              :defaultConfig="editorConfig"
              :mode="editorMode"
              @onCreated="handleEditorTipsCreated"
            />
          </div>
        </el-form-item>
        <el-form-item label="常见错误">
          <div class="editor-wrapper">
            <Toolbar
              class="editor-toolbar"
              :editor="editorRefMistakes"
              :defaultConfig="editorToolbarConfig"
              :mode="editorMode"
            />
            <Editor
              class="editor-content"
              v-model="commonMistakesHtml"
              :defaultConfig="editorConfig"
              :mode="editorMode"
              @onCreated="handleEditorMistakesCreated"
            />
          </div>
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="状态">
              <el-radio-group v-model="formData.status">
                <el-radio :value="1">上架</el-radio>
                <el-radio :value="0">下架</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="排序">
              <el-input-number v-model="formData.sortOrder" :min="0" style="width: 120px" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="trainingExercise">
import { ref, reactive, shallowRef, watch, onBeforeUnmount } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { Plus, Refresh, Edit, Delete } from "@element-plus/icons-vue";
import {
  getExercisePageApi,
  getExerciseByIdApi,
  addExerciseApi,
  updateExerciseApi,
  deleteExerciseApi
} from "@/api/modules/exercise";
import { getPlayUrlApi } from "@/api/modules/video";
import ImageLibrary from "@/components/ImageLibrary/index.vue";
import VideoLibrary from "@/components/VideoLibrary/index.vue";
import { useUserStore } from "@/stores/modules/user";
import "@wangeditor/editor/dist/css/style.css";
import { Editor, Toolbar } from "@wangeditor/editor-for-vue";

const userStore = useUserStore();
const editorRefDesc = shallowRef();
const editorRefTips = shallowRef();
const editorRefMistakes = shallowRef();
const descriptionHtml = ref("");
const tipsHtml = ref("");
const commonMistakesHtml = ref("");
const editorMode = "default";
const editorToolbarConfig = {};
const editorConfig = {
  placeholder: "请输入内容...",
  MENU_CONF: {
    uploadImage: {
      server: import.meta.env.VITE_API_URL + "/file/upload/exercise/cover",
      fieldName: "file",
      headers: () => ({ Authorization: userStore.token }),
      customInsert(res, insertFn) {
        if (res.code === 200 && res.data) insertFn(res.data, "", "");
      }
    }
  }
};

const handleEditorDescCreated = editor => {
  editorRefDesc.value = editor;
};
const handleEditorTipsCreated = editor => {
  editorRefTips.value = editor;
};
const handleEditorMistakesCreated = editor => {
  editorRefMistakes.value = editor;
};

onBeforeUnmount(() => {
  editorRefDesc.value?.destroy();
  editorRefTips.value?.destroy();
  editorRefMistakes.value?.destroy();
});

const xGrid = ref();
const formRef = ref();
const dialogVisible = ref(false);
const isEdit = ref(false);
const submitLoading = ref(false);
const videoLibraryRef = ref();
const selectedVideo = ref(null);

watch(dialogVisible, val => {
  if (!val) {
    descriptionHtml.value = "";
    tipsHtml.value = "";
    commonMistakesHtml.value = "";
    editorRefDesc.value = null;
    editorRefTips.value = null;
    editorRefMistakes.value = null;
  }
});

const formData = reactive({
  id: null,
  name: "",
  description: "",
  videoUrl: "",
  coverImage: "",
  muscleGroup: "",
  equipment: "none",
  difficulty: 1,
  caloriesPerMin: null,
  tips: "",
  commonMistakes: "",
  status: 1,
  sortOrder: 0
});

const formRules = {
  name: [{ required: true, message: "请输入动作名称", trigger: "blur" }],
  difficulty: [{ required: true, message: "请选择难度", trigger: "change" }]
};

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
    custom: true
  },
  formConfig: {
    items: [
      { field: "keyword", title: "动作名称", span: 6, itemRender: { name: "$input", props: { placeholder: "请输入名称" } } },
      {
        field: "difficulty",
        title: "难度",
        span: 6,
        itemRender: {
          name: "$select",
          options: [
            { label: "全部", value: "" },
            { label: "入门", value: 1 },
            { label: "进阶", value: 2 },
            { label: "高级", value: 3 }
          ],
          props: { placeholder: "请选择" }
        }
      },
      {
        field: "status",
        title: "状态",
        span: 6,
        itemRender: {
          name: "$select",
          options: [
            { label: "全部", value: "" },
            { label: "已上架", value: 1 },
            { label: "已下架", value: 0 }
          ],
          props: { placeholder: "请选择" }
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
          ...form
        };
        try {
          const res = await getExercisePageApi(params);
          return { list: res.data?.records || [], total: res.data?.total || 0 };
        } catch (error) {
          console.error("获取动作列表失败:", error);
          return { list: [], total: 0 };
        }
      }
    }
  },
  columns: [
    { type: "seq", width: 60, title: "序号" },
    { field: "coverImage", title: "封面", width: 140, slots: { default: "cover" } },
    { field: "name", title: "动作名称", minWidth: 120 },
    { field: "muscleGroupName", title: "肌群", width: 100 },
    { field: "difficulty", title: "难度", width: 80, slots: { default: "difficulty" } },
    { field: "equipmentName", title: "器材", width: 80 },
    { field: "caloriesPerMin", title: "卡路里/分钟", width: 100 },
    { field: "sortOrder", title: "排序", width: 70 },
    { field: "status", title: "状态", width: 80, slots: { default: "status" } },
    { title: "操作", width: 150, fixed: "right", slots: { default: "action" } }
  ]
});

const handleRefresh = () => {
  xGrid.value?.commitProxy("query");
};

const resetForm = () => {
  Object.assign(formData, {
    id: null,
    name: "",
    description: "",
    videoUrl: "",
    coverImage: "",
    muscleGroup: "",
    equipment: "none",
    difficulty: 1,
    caloriesPerMin: null,
    tips: "",
    commonMistakes: "",
    status: 1,
    sortOrder: 0
  });
  selectedVideo.value = null;
  descriptionHtml.value = "";
  tipsHtml.value = "";
  commonMistakesHtml.value = "";
};

const handleAdd = () => {
  isEdit.value = false;
  resetForm();
  dialogVisible.value = true;
};

const handleEdit = async row => {
  isEdit.value = true;
  try {
    const res = await getExerciseByIdApi(row.id);
    const d = res.data || {};
    Object.assign(formData, {
      id: d.id,
      name: d.name,
      description: d.description,
      videoUrl: d.videoUrl,
      coverImage: d.coverImage,
      muscleGroup: d.muscleGroup,
      equipment: d.equipment,
      difficulty: d.difficulty,
      caloriesPerMin: d.caloriesPerMin,
      tips: d.tips,
      commonMistakes: d.commonMistakes,
      status: d.status,
      sortOrder: d.sortOrder ?? 0
    });
    descriptionHtml.value = d.description || "";
    tipsHtml.value = d.tips || "";
    commonMistakesHtml.value = d.commonMistakes || "";
    selectedVideo.value = d.videoUrl ? { id: null, title: "已关联视频", coverUrl: null } : null;
  } catch (e) {
    ElMessage.error("获取详情失败");
    return;
  }
  dialogVisible.value = true;
};

const openVideoLibrary = () => {
  videoLibraryRef.value?.open();
};

const handleVideoSelect = async video => {
  if (!video) return;
  try {
    const res = await getPlayUrlApi(video.id);
    formData.videoUrl = res.data || "";
    selectedVideo.value = video;
    ElMessage.success("已选择视频");
  } catch (e) {
    ElMessage.error("获取播放地址失败");
  }
};

const clearVideo = () => {
  selectedVideo.value = null;
  formData.videoUrl = "";
};

const handleSubmit = async () => {
  try {
    await formRef.value.validate();
    submitLoading.value = true;
    formData.description = descriptionHtml.value;
    formData.tips = tipsHtml.value;
    formData.commonMistakes = commonMistakesHtml.value;
    const payload = {
      name: formData.name,
      description: formData.description,
      videoUrl: formData.videoUrl,
      coverImage: formData.coverImage,
      muscleGroup: formData.muscleGroup || null,
      equipment: formData.equipment || null,
      difficulty: formData.difficulty,
      caloriesPerMin: formData.caloriesPerMin,
      tips: formData.tips || null,
      commonMistakes: formData.commonMistakes || null,
      status: formData.status,
      sortOrder: formData.sortOrder ?? 0
    };
    if (isEdit.value) {
      await updateExerciseApi(formData.id, payload);
    } else {
      await addExerciseApi(payload);
    }
    ElMessage.success(isEdit.value ? "编辑成功" : "新增成功");
    dialogVisible.value = false;
    handleRefresh();
  } catch (error) {
    if (error !== false) {
      console.error("提交失败:", error);
      ElMessage.error("操作失败，请重试");
    }
  } finally {
    submitLoading.value = false;
  }
};

const handleDelete = row => {
  ElMessageBox.confirm("确定要删除该动作吗？", "提示", {
    type: "warning"
  }).then(async () => {
    try {
      await deleteExerciseApi(row.id);
      ElMessage.success("删除成功");
      handleRefresh();
    } catch (error) {
      console.error("删除失败:", error);
      ElMessage.error("删除失败，请重试");
    }
  });
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
  object-fit: cover;
  border-radius: 4px;
}

.selected-video .video-title {
  flex: 1;
  font-size: 14px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.editor-wrapper {
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  overflow: hidden;
}

.editor-toolbar {
  border-bottom: 1px solid #dcdfe6;
}

.editor-content {
  min-height: 150px;
}
</style>
