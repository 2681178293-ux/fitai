<template>
  <div class="table-box p-5">

    <vxe-grid ref="xGrid" v-bind="gridOptions">
      <template #toolbar_buttons>
        <el-button type="primary" :icon="Plus" @click="handleAdd">新增文档</el-button>
        <el-button :icon="Refresh" @click="handleRefresh">刷新</el-button>
        <el-button type="success" :loading="rebuildLoading" @click="handleRebuildVector">重建向量索引</el-button>
      </template>
      <template #category="{ row }">
        <el-tag size="small">{{ row.categoryName || row.category || "-" }}</el-tag>
      </template>
      <template #status="{ row }">
        <el-tag v-if="row.status === 1" type="success">启用</el-tag>
        <el-tag v-else type="info">禁用</el-tag>
      </template>
      <template #action="{ row }">
        <el-button type="primary" link :icon="Edit" @click="handleEdit(row)">编辑</el-button>
        <el-button type="danger" link :icon="Delete" @click="handleDelete(row)">删除</el-button>
      </template>
    </vxe-grid>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑文档' : '新增文档'" width="680px" destroy-on-close>
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="formData.title" placeholder="文档标题" />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="formData.category" placeholder="请选择" clearable style="width: 100%">
            <el-option label="动作" value="exercise" />
            <el-option label="营养" value="nutrition" />
            <el-option label="损伤" value="injury" />
            <el-option label="器材" value="equipment" />
          </el-select>
        </el-form-item>
        <el-form-item label="标签">
          <el-input v-model="formData.tags" placeholder="逗号分隔" />
        </el-form-item>
        <el-form-item label="来源">
          <el-input v-model="formData.source" placeholder="选填；上传文件时可自动填为文件名" />
        </el-form-item>
        <el-form-item label="上传文件">
          <div class="dialog-upload-row">
            <el-upload
              ref="dialogUploadRef"
              :limit="1"
              :auto-upload="false"
              :show-file-list="true"
              :on-exceed="onDialogUploadExceed"
              :on-change="onDialogFileChange"
              :on-remove="onDialogFileRemove"
            >
              <el-button type="primary" plain :loading="extractLoading" :icon="UploadFilled">
                选择文件并解析到正文
              </el-button>
            </el-upload>
            <span class="dialog-upload-tip text-gray-500">
              PDF、Markdown、HTML、纯文本、JSON、Office 等；单文件上限见后端
              <code class="rounded bg-gray-100 px-1">fitai.ai.knowledge.import-max-bytes</code>
            </span>
          </div>
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input v-model="formData.content" type="textarea" :rows="10" placeholder="文档内容，或由上传文件自动填入" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="aiKnowledge">
import { ref, reactive } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { Plus, Refresh, Edit, Delete, UploadFilled } from "@element-plus/icons-vue";
import {
  getKnowledgePageApi,
  addKnowledgeApi,
  updateKnowledgeApi,
  deleteKnowledgeApi,
  rebuildKnowledgeVectorApi,
  extractKnowledgeFileApi
} from "@/api/modules/ai";

const xGrid = ref();
const formRef = ref();
const dialogUploadRef = ref();
const dialogVisible = ref(false);
const isEdit = ref(false);
const submitLoading = ref(false);
const rebuildLoading = ref(false);
const extractLoading = ref(false);

const formData = reactive({
  id: null,
  title: "",
  content: "",
  category: "",
  tags: "",
  source: ""
});

const formRules = {
  title: [{ required: true, message: "请输入标题", trigger: "blur" }],
  content: [{ required: true, message: "请输入内容", trigger: "blur" }]
};

const gridOptions = reactive({
  border: true,
  showOverflow: true,
  height: 600,
  loading: false,
  rowConfig: { keyField: "id" },
  toolbarConfig: { slots: { buttons: "toolbar_buttons" }, refresh: true, zoom: false },
  proxyConfig: {
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
          const res = await getKnowledgePageApi(params);
          const data = res.data || {};
          return { list: data.records || [], total: data.total || 0 };
        } catch (e) {
          return { list: [], total: 0 };
        }
      }
    }
  },
  formConfig: {
    items: [
      { field: "keyword", title: "关键词", span: 6, itemRender: { name: "$input", props: { placeholder: "标题/内容" } } },
      {
        field: "category",
        title: "分类",
        span: 6,
        itemRender: {
          name: "$select",
          options: [
            { label: "全部", value: "" },
            { label: "动作", value: "exercise" },
            { label: "营养", value: "nutrition" },
            { label: "损伤", value: "injury" },
            { label: "器材", value: "equipment" }
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
  columns: [
    { type: "seq", width: 60 },
    { field: "title", title: "标题", minWidth: 200 },
    { field: "category", title: "分类", width: 100, slots: { default: "category" } },
    { field: "tags", title: "标签", width: 120 },
    { field: "status", title: "状态", width: 80, slots: { default: "status" } },
    { field: "createTime", title: "创建时间", width: 170 },
    { title: "操作", width: 150, fixed: "right", slots: { default: "action" } }
  ]
});

const handleRefresh = () => xGrid.value?.commitProxy("query");

function stripExtension(fileName) {
  if (!fileName || typeof fileName !== "string") return "";
  const idx = fileName.lastIndexOf(".");
  if (idx <= 0) return fileName.trim();
  return fileName.slice(0, idx).trim();
}

function onDialogUploadExceed() {
  ElMessage.warning("请先移除已选文件，再选择新文件");
}

function onDialogFileRemove() {
  // 仅清空上传列表，不改动已填正文
}

async function onDialogFileChange(uploadFile) {
  const raw = uploadFile.raw;
  if (!raw) return;
  const fd = new FormData();
  fd.append("file", raw);
  extractLoading.value = true;
  try {
    const res = await extractKnowledgeFileApi(fd);
    const payload = res.data || {};
    const text = payload.text ?? "";
    const fileName = payload.fileName || raw.name || "";
    if (!text.trim()) {
      ElMessage.warning("解析结果为空，请检查文件类型与内容");
      return;
    }
    formData.content = text;
    if (!formData.title?.trim()) {
      formData.title = stripExtension(fileName) || formData.title;
    }
    if (!formData.source?.trim()) {
      formData.source = fileName;
    }
    formRef.value?.validateField("content");
    ElMessage.success("已解析并填入正文，请核对后保存");
  } catch (e) {
    ElMessage.error(e?.msg || e?.message || "解析失败");
  } finally {
    extractLoading.value = false;
  }
}

const handleAdd = () => {
  isEdit.value = false;
  Object.assign(formData, { id: null, title: "", content: "", category: "", tags: "", source: "" });
  dialogVisible.value = true;
};

const handleEdit = row => {
  isEdit.value = true;
  Object.assign(formData, {
    id: row.id,
    title: row.title,
    content: row.content,
    category: row.category || "",
    tags: row.tags || "",
    source: row.source || ""
  });
  dialogVisible.value = true;
};

const handleSubmit = async () => {
  try {
    await formRef.value.validate();
    submitLoading.value = true;
    const payload = {
      title: formData.title,
      content: formData.content,
      category: formData.category || null,
      tags: formData.tags || null,
      source: formData.source || null
    };
    if (isEdit.value) await updateKnowledgeApi(formData.id, payload);
    else await addKnowledgeApi(payload);
    ElMessage.success(isEdit.value ? "编辑成功" : "新增成功");
    dialogVisible.value = false;
    dialogUploadRef.value?.clearFiles();
    handleRefresh();
  } catch (e) {
    if (e !== false) ElMessage.error("操作失败");
  } finally {
    submitLoading.value = false;
  }
};

const handleDelete = row => {
  ElMessageBox.confirm("确定删除该文档？", "提示", { type: "warning" }).then(async () => {
    try {
      await deleteKnowledgeApi(row.id);
      ElMessage.success("删除成功");
      handleRefresh();
    } catch (e) {
      ElMessage.error("删除失败");
    }
  });
};

const handleRebuildVector = () => {
  ElMessageBox.confirm("将重新把所有启用的知识文档写入向量索引，耗时取决于文档量，确定继续？", "提示", {
    type: "warning"
  }).then(async () => {
    rebuildLoading.value = true;
    try {
      await rebuildKnowledgeVectorApi();
      ElMessage.success("重建向量索引成功");
    } catch (e) {
      ElMessage.error(e?.msg || e?.message || "重建向量索引失败");
    } finally {
      rebuildLoading.value = false;
    }
  });
};
</script>

<style scoped>
.table-box {
  background: #fff;
  border-radius: 8px;
}

.dialog-upload-row {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 8px;
  width: 100%;
}

.dialog-upload-tip {
  font-size: 12px;
  line-height: 1.5;
}
</style>
