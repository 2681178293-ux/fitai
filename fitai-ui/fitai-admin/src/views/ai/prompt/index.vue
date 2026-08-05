<template>
  <div class="table-box p-5">
    <vxe-grid ref="xGrid" v-bind="gridOptions">
      <template #toolbar_buttons>
        <el-select
          v-model="filterScope"
          placeholder="作用域筛选"
          clearable
          style="width: 150px; margin-right: 8px"
          @change="loadList"
        >
          <el-option label="全部作用域" value="" />
          <el-option label="全局" value="global" />
          <el-option label="训练计划" value="training" />
          <el-option label="饮食方案" value="diet" />
          <el-option label="闲聊" value="chat" />
          <el-option label="训练点评" value="review" />
          <el-option label="知识库问答(RAG)" value="knowledge" />
        </el-select>
        <el-button type="primary" :icon="Plus" @click="handleAdd">新增提示词</el-button>
        <el-button :icon="Refresh" @click="loadList">刷新</el-button>
      </template>
      <template #scope="{ row }">
        <el-tag size="small">{{ scopeMap[row.scope] || row.scope }}</el-tag>
      </template>
      <template #enabled="{ row }">
        <el-tag v-if="row.enabled === 1" type="success">启用</el-tag>
        <el-tag v-else type="info">禁用</el-tag>
      </template>
      <template #action="{ row }">
        <el-button type="primary" link :icon="Edit" @click="handleEdit(row)">编辑</el-button>
        <el-button type="danger" link :icon="Delete" @click="handleDelete(row)">删除</el-button>
      </template>
    </vxe-grid>
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑提示词' : '新增提示词'" width="640px" destroy-on-close>
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
        <el-form-item label="作用域" prop="scope">
          <el-select v-model="formData.scope" placeholder="请选择" style="width: 100%">
            <el-option label="全局" value="global" />
            <el-option label="训练计划" value="training" />
            <el-option label="饮食方案" value="diet" />
            <el-option label="闲聊" value="chat" />
            <el-option label="训练点评" value="review" />
            <el-option label="知识库问答(RAG)" value="knowledge" />
          </el-select>
        </el-form-item>
        <el-form-item label="名称" prop="name">
          <el-input v-model="formData.name" placeholder="如：训练计划系统提示" />
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input v-model="formData.content" type="textarea" :rows="10" placeholder="提示词内容，支持变量" />
        </el-form-item>
        <el-form-item label="启用">
          <el-radio-group v-model="formData.enabled">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="formData.sortOrder" :min="0" style="width: 120px" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="aiPrompt">
import { ref, reactive, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { Plus, Refresh, Edit, Delete } from "@element-plus/icons-vue";
import { getPromptListApi, getPromptByIdApi, addPromptApi, updatePromptApi, deletePromptApi } from "@/api/modules/ai";

const scopeMap = {
  global: "全局",
  training: "训练计划",
  diet: "饮食方案",
  chat: "闲聊",
  review: "训练点评",
  knowledge: "知识库问答(RAG)"
};

const filterScope = ref("");

const xGrid = ref();
const formRef = ref();
const dialogVisible = ref(false);
const isEdit = ref(false);
const submitLoading = ref(false);
const list = ref([]);

const formData = reactive({
  id: null,
  scope: "global",
  name: "",
  content: "",
  enabled: 1,
  sortOrder: 0
});

const formRules = {
  scope: [{ required: true, message: "请选择作用域", trigger: "change" }],
  name: [{ required: true, message: "请输入名称", trigger: "blur" }],
  content: [{ required: true, message: "请输入内容", trigger: "blur" }]
};

const gridOptions = reactive({
  border: true,
  showOverflow: true,
  height: 600,
  loading: false,
  data: list,
  rowConfig: { keyField: "id" },
  toolbarConfig: { slots: { buttons: "toolbar_buttons" }, zoom: false },
  columns: [
    { type: "seq", width: 60 },
    { field: "scope", title: "作用域", width: 100, slots: { default: "scope" } },
    { field: "name", title: "名称", minWidth: 160 },
    { field: "content", title: "内容摘要", minWidth: 200, showOverflow: true },
    { field: "enabled", title: "状态", width: 80, slots: { default: "enabled" } },
    { field: "sortOrder", title: "排序", width: 70 },
    { title: "操作", width: 150, fixed: "right", slots: { default: "action" } }
  ]
});

async function loadList() {
  gridOptions.loading = true;
  try {
    const params = filterScope.value ? { scope: filterScope.value } : {};
    const res = await getPromptListApi(params);
    list.value = res.data || [];
  } catch (e) {
    list.value = [];
    ElMessage.error(e?.msg || e?.message || "加载提示词列表失败");
  } finally {
    gridOptions.loading = false;
  }
}

onMounted(loadList);

const handleAdd = () => {
  isEdit.value = false;
  Object.assign(formData, { id: null, scope: "global", name: "", content: "", enabled: 1, sortOrder: 0 });
  dialogVisible.value = true;
};

const handleEdit = async row => {
  isEdit.value = true;
  try {
    const res = await getPromptByIdApi(row.id);
    const d = res.data || {};
    Object.assign(formData, { id: d.id, scope: d.scope, name: d.name, content: d.content, enabled: d.enabled ?? 1, sortOrder: d.sortOrder ?? 0 });
  } catch (e) {
    ElMessage.error("获取详情失败");
    return;
  }
  dialogVisible.value = true;
};

const handleSubmit = async () => {
  try {
    await formRef.value.validate();
    submitLoading.value = true;
    const payload = { scope: formData.scope, name: formData.name, content: formData.content, enabled: formData.enabled, sortOrder: formData.sortOrder };
    if (isEdit.value) await updatePromptApi(formData.id, payload);
    else await addPromptApi(payload);
    ElMessage.success(isEdit.value ? "编辑成功" : "新增成功");
    dialogVisible.value = false;
    loadList();
  } catch (e) {
    if (e !== false) ElMessage.error("操作失败");
  } finally {
    submitLoading.value = false;
  }
};

const handleDelete = row => {
  ElMessageBox.confirm("确定删除该提示词？", "提示", { type: "warning" }).then(async () => {
    try {
      await deletePromptApi(row.id);
      ElMessage.success("删除成功");
      loadList();
    } catch (e) {
      ElMessage.error("删除失败");
    }
  });
};
</script>

<style scoped>
.table-box {
  background: #fff;
  border-radius: 8px;
}
</style>
