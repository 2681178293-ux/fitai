<template>
  <div class="table-box p-5">
    <vxe-grid ref="xGrid" v-bind="gridOptions">
      <template #toolbar_buttons>
        <el-button type="primary" :icon="Plus" @click="handleAdd">新增工具</el-button>
        <el-button :icon="Refresh" @click="handleRefresh">刷新</el-button>
      </template>
      <template #toolType="{ row }">
        <el-tag size="small" :type="typeTag(row.toolType)">{{ row.toolType || "function" }}</el-tag>
      </template>
      <template #status="{ row }">
        <el-tag v-if="row.status === '0'" type="success">正常</el-tag>
        <el-tag v-else type="info">停用</el-tag>
      </template>
      <template #action="{ row }">
        <el-button type="primary" link :icon="Edit" @click="handleEdit(row)">编辑</el-button>
        <el-button
          link
          :type="row.status === '0' ? 'warning' : 'success'"
          @click="handleToggleStatus(row)"
        >{{ row.status === "0" ? "停用" : "启用" }}</el-button>
        <el-button type="danger" link :icon="Delete" @click="handleDelete(row)">删除</el-button>
      </template>
    </vxe-grid>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑工具' : '新增工具'" width="640px" destroy-on-close>
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="110px">
        <el-form-item label="工具名称" prop="toolName">
          <el-input v-model="formData.toolName" placeholder="工具名称" maxlength="64" />
        </el-form-item>
        <el-form-item label="工具编码" prop="toolCode">
          <el-input v-model="formData.toolCode" placeholder="唯一编码，如 calculateBmi" maxlength="64" />
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="formData.toolType" style="width: 100%">
            <el-option label="function" value="function" />
            <el-option label="mcp" value="mcp" />
            <el-option label="http" value="http" />
          </el-select>
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="formData.description" type="textarea" :rows="2" placeholder="选填" maxlength="256" />
        </el-form-item>
        <el-form-item label="参数 Schema">
          <el-input v-model="formData.schemaJson" type="textarea" :rows="4" placeholder="JSON Schema，留空默认 {}" />
        </el-form-item>
        <el-form-item label="端点">
          <el-input v-model="formData.endpoint" placeholder="HTTP/MCP 端点，选填" maxlength="512" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="formData.status">
            <el-radio value="0">正常</el-radio>
            <el-radio value="1">停用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="aiTool">
import { ref, reactive } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { Plus, Refresh, Edit, Delete } from "@element-plus/icons-vue";
import {
  getToolPageApi,
  getToolDetailApi,
  addToolApi,
  updateToolApi,
  deleteToolApi,
  changeToolStatusApi
} from "@/api/modules/ai-agent";

const xGrid = ref();
const formRef = ref();
const dialogVisible = ref(false);
const isEdit = ref(false);
const submitLoading = ref(false);

const formData = reactive({
  toolId: null,
  toolName: "",
  toolCode: "",
  toolType: "function",
  description: "",
  schemaJson: "",
  endpoint: "",
  status: "0"
});

const formRules = {
  toolName: [{ required: true, message: "请输入工具名称", trigger: "blur" }],
  toolCode: [{ required: true, message: "请输入工具编码", trigger: "blur" }]
};

const typeTag = t => (t === "mcp" ? "warning" : t === "http" ? "success" : "primary");

const gridOptions = reactive({
  border: true,
  showOverflow: true,
  height: 600,
  loading: false,
  rowConfig: { keyField: "toolId" },
  toolbarConfig: { slots: { buttons: "toolbar_buttons" }, refresh: true, zoom: false },
  proxyConfig: {
    form: true,
    response: { result: "list", total: "total" },
    ajax: {
      query: async ({ page, form }) => {
        const params = { pageNum: page.currentPage, pageSize: page.pageSize, ...form };
        try {
          const res = await getToolPageApi(params);
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
      { field: "keyword", title: "关键词", span: 6, itemRender: { name: "$input", props: { placeholder: "名称/编码" } } },
      {
        field: "status",
        title: "状态",
        span: 6,
        itemRender: {
          name: "$select",
          options: [
            { label: "全部", value: "" },
            { label: "正常", value: "0" },
            { label: "停用", value: "1" }
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
    { field: "toolName", title: "名称", minWidth: 140 },
    { field: "toolCode", title: "编码", width: 150 },
    { field: "toolType", title: "类型", width: 110, slots: { default: "toolType" } },
    { field: "description", title: "描述", minWidth: 160 },
    { field: "endpoint", title: "端点", minWidth: 150 },
    { field: "status", title: "状态", width: 90, slots: { default: "status" } },
    { title: "操作", width: 200, fixed: "right", slots: { default: "action" } }
  ]
});

const handleRefresh = () => xGrid.value?.commitProxy("query");

const resetForm = () => {
  Object.assign(formData, {
    toolId: null,
    toolName: "",
    toolCode: "",
    toolType: "function",
    description: "",
    schemaJson: "",
    endpoint: "",
    status: "0"
  });
};

const handleAdd = () => {
  isEdit.value = false;
  resetForm();
  dialogVisible.value = true;
};

const handleEdit = async row => {
  isEdit.value = true;
  resetForm();
  try {
    const res = await getToolDetailApi(row.toolId);
    const d = res.data || {};
    Object.assign(formData, {
      toolId: d.toolId,
      toolName: d.toolName || "",
      toolCode: d.toolCode || "",
      toolType: d.toolType || "function",
      description: d.description || "",
      schemaJson: d.schemaJson || "",
      endpoint: d.endpoint || "",
      status: d.status || "0"
    });
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
    const payload = {
      toolName: formData.toolName,
      toolCode: formData.toolCode,
      toolType: formData.toolType || "function",
      description: formData.description || null,
      schemaJson: formData.schemaJson || null,
      endpoint: formData.endpoint || null,
      status: formData.status
    };
    if (isEdit.value) await updateToolApi(formData.toolId, payload);
    else await addToolApi(payload);
    ElMessage.success(isEdit.value ? "编辑成功" : "新增成功");
    dialogVisible.value = false;
    handleRefresh();
  } catch (e) {
    if (e !== false) ElMessage.error("操作失败");
  } finally {
    submitLoading.value = false;
  }
};

const handleToggleStatus = row => {
  const target = row.status === "0" ? "1" : "0";
  changeToolStatusApi(row.toolId, target)
    .then(() => {
      ElMessage.success("操作成功");
      handleRefresh();
    })
    .catch(() => ElMessage.error("操作失败"));
};

const handleDelete = row => {
  ElMessageBox.confirm(`确定删除工具「${row.toolName}」？`, "提示", { type: "warning" }).then(async () => {
    try {
      await deleteToolApi(row.toolId);
      ElMessage.success("删除成功");
      handleRefresh();
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
