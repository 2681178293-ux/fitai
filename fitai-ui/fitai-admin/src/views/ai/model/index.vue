<template>
  <div class="table-box p-5">
    <vxe-grid ref="xGrid" v-bind="gridOptions">
      <template #toolbar_buttons>
        <el-button type="primary" :icon="Plus" @click="handleAdd">新增模型</el-button>
        <el-button :icon="Refresh" @click="loadList">刷新</el-button>
      </template>
      <template #isDefault="{ row }">
        <el-tag v-if="row.isDefault === 1" type="success">默认</el-tag>
        <span v-else>-</span>
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
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑模型' : '新增模型'" width="520px" destroy-on-close>
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
        <el-form-item label="显示名称" prop="name">
          <el-input v-model="formData.name" placeholder="如：通义千问 Plus" />
        </el-form-item>
        <el-form-item label="模型标识" prop="modelKey">
          <el-input v-model="formData.modelKey" placeholder="如：qwen-plus、qwen-max" />
        </el-form-item>
        <el-form-item label="Base URL">
          <el-input v-model="formData.baseUrl" placeholder="留空使用默认" />
        </el-form-item>
        <el-form-item label="API Key">
          <el-input v-model="formData.apiKey" type="password" placeholder="编辑时不修改请留空" show-password />
        </el-form-item>
        <el-form-item label="温度">
          <el-input-number v-model="formData.temperature" :min="0" :max="2" :step="0.1" :precision="2" style="width: 120px" />
        </el-form-item>
        <el-form-item label="默认模型">
          <el-radio-group v-model="formData.isDefault">
            <el-radio :value="1">是</el-radio>
            <el-radio :value="0">否</el-radio>
          </el-radio-group>
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

<script setup name="aiModel">
import { ref, reactive, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { Plus, Refresh, Edit, Delete } from "@element-plus/icons-vue";
import { getModelListApi, getModelByIdApi, addModelApi, updateModelApi, deleteModelApi } from "@/api/modules/ai";

const xGrid = ref();
const formRef = ref();
const dialogVisible = ref(false);
const isEdit = ref(false);
const submitLoading = ref(false);
const list = ref([]);

const formData = reactive({
  id: null,
  name: "",
  modelKey: "",
  baseUrl: "",
  apiKey: "",
  temperature: 0.7,
  isDefault: 0,
  enabled: 1,
  sortOrder: 0
});

const formRules = {
  name: [{ required: true, message: "请输入显示名称", trigger: "blur" }],
  modelKey: [{ required: true, message: "请输入模型标识", trigger: "blur" }]
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
    { field: "name", title: "显示名称", minWidth: 120 },
    { field: "modelKey", title: "模型标识", width: 120 },
    { field: "temperature", title: "温度", width: 80 },
    { field: "isDefault", title: "默认", width: 80, slots: { default: "isDefault" } },
    { field: "enabled", title: "状态", width: 80, slots: { default: "enabled" } },
    { field: "sortOrder", title: "排序", width: 70 },
    { title: "操作", width: 150, fixed: "right", slots: { default: "action" } }
  ]
});

async function loadList() {
  gridOptions.loading = true;
  try {
    const res = await getModelListApi();
    list.value = res.data || [];
  } catch (e) {
    list.value = [];
    ElMessage.error(e?.msg || e?.message || "加载模型配置失败");
  } finally {
    gridOptions.loading = false;
  }
}

onMounted(loadList);

const handleAdd = () => {
  isEdit.value = false;
  Object.assign(formData, { id: null, name: "", modelKey: "", baseUrl: "", apiKey: "", temperature: 0.7, isDefault: 0, enabled: 1, sortOrder: 0 });
  dialogVisible.value = true;
};

const handleEdit = async row => {
  isEdit.value = true;
  try {
    const res = await getModelByIdApi(row.id);
    const d = res.data || {};
    Object.assign(formData, { id: d.id, name: d.name, modelKey: d.modelKey, baseUrl: d.baseUrl || "", apiKey: d.apiKey || "", temperature: d.temperature ?? 0.7, isDefault: d.isDefault ?? 0, enabled: d.enabled ?? 1, sortOrder: d.sortOrder ?? 0 });
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
    const payload = { name: formData.name, modelKey: formData.modelKey, baseUrl: formData.baseUrl || null, apiKey: formData.apiKey || null, temperature: formData.temperature, isDefault: formData.isDefault, enabled: formData.enabled, sortOrder: formData.sortOrder };
    if (isEdit.value) await updateModelApi(formData.id, payload);
    else await addModelApi(payload);
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
  ElMessageBox.confirm("确定删除该模型配置？", "提示", { type: "warning" }).then(async () => {
    try {
      await deleteModelApi(row.id);
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
