<template>
  <div class="table-box p-5">
    <vxe-grid ref="xGrid" v-bind="gridOptions">
      <template #toolbar_buttons>
        <el-button type="primary" :icon="Plus" @click="handleAdd">新增数据集</el-button>
        <el-button :icon="Refresh" @click="handleRefresh">刷新</el-button>
      </template>
      <template #datasetType="{ row }">
        <el-tag size="small" :type="row.datasetType === 'eval' ? 'primary' : 'warning'">
          {{ row.datasetType === "eval" ? "模型评测" : row.datasetType === "rag" ? "RAG评估" : row.datasetType }}
        </el-tag>
      </template>
      <template #status="{ row }">
        <el-tag v-if="row.status === '0'" type="success">正常</el-tag>
        <el-tag v-else type="info">停用</el-tag>
      </template>
      <template #action="{ row }">
        <el-button type="success" link :icon="List" @click="openRows(row)">样本行</el-button>
        <el-button type="primary" link :icon="Edit" @click="handleEdit(row)">编辑</el-button>
        <el-button type="danger" link :icon="Delete" @click="handleDelete(row)">删除</el-button>
      </template>
    </vxe-grid>

    <!-- 数据集 新增/编辑 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑数据集' : '新增数据集'" width="520px" destroy-on-close>
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
        <el-form-item label="名称" prop="datasetName">
          <el-input v-model="formData.datasetName" placeholder="数据集名称" />
        </el-form-item>
        <el-form-item label="编码">
          <el-input v-model="formData.datasetCode" placeholder="不填自动生成 ds_时间戳" />
        </el-form-item>
        <el-form-item label="类型" prop="datasetType">
          <el-select v-model="formData.datasetType" placeholder="请选择" style="width: 100%" :disabled="isEdit">
            <el-option label="模型评测（eval）" value="eval" />
            <el-option label="RAG质量评估（rag）" value="rag" />
          </el-select>
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

    <!-- 样本行管理 抽屉 -->
    <el-drawer v-model="rowDrawer" :title="`样本行管理 - ${currentDataset.datasetName || ''}`" size="60%">
      <div class="row-toolbar">
        <el-button type="primary" :icon="Plus" @click="handleAddRow">新增样本行</el-button>
        <el-button :icon="Refresh" @click="loadRows">刷新</el-button>
      </div>
      <el-table :data="rowList" border v-loading="rowLoading" size="small" style="width: 100%">
        <el-table-column type="index" width="55" />
        <el-table-column prop="inputText" label="输入（问题）" show-overflow-tooltip min-width="200" />
        <el-table-column prop="outputText" label="期望输出" show-overflow-tooltip min-width="200" />
        <el-table-column prop="createTime" label="创建时间" width="170" />
        <el-table-column label="操作" width="130" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link :icon="Edit" @click="handleEditRow(row)">编辑</el-button>
            <el-button type="danger" link :icon="Delete" @click="handleDeleteRow(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="row-pager">
        <el-pagination
          layout="total, prev, pager, next"
          :total="rowTotal"
          :current-page="rowPage.pageNum"
          :page-size="rowPage.pageSize"
          @current-change="onRowPageChange"
        />
      </div>
    </el-drawer>

    <!-- 样本行 新增/编辑 -->
    <el-dialog v-model="rowDialog" :title="rowIsEdit ? '编辑样本行' : '新增样本行'" width="600px" destroy-on-close append-to-body>
      <el-form ref="rowFormRef" :model="rowForm" :rules="rowRules" label-width="100px">
        <el-form-item label="输入" prop="inputText">
          <el-input v-model="rowForm.inputText" type="textarea" :rows="3" placeholder="问题/输入" />
        </el-form-item>
        <el-form-item label="期望输出">
          <el-input v-model="rowForm.outputText" type="textarea" :rows="3" placeholder="标准答案（选填）" />
        </el-form-item>
        <el-form-item label="元数据">
          <el-input v-model="rowForm.metadataJson" type="textarea" :rows="2" placeholder="JSON，选填" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="rowDialog = false">取消</el-button>
        <el-button type="primary" :loading="rowSubmitLoading" @click="handleSubmitRow">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="aiDataset">
import { ref, reactive } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { Plus, Refresh, Edit, Delete, List } from "@element-plus/icons-vue";
import {
  getDatasetPageApi,
  addDatasetApi,
  updateDatasetApi,
  deleteDatasetApi,
  getDatasetRowPageApi,
  addDatasetRowApi,
  updateDatasetRowApi,
  deleteDatasetRowApi
} from "@/api/modules/ai-agent";

const xGrid = ref();
const formRef = ref();
const dialogVisible = ref(false);
const isEdit = ref(false);
const submitLoading = ref(false);

const formData = reactive({
  datasetId: null,
  datasetName: "",
  datasetCode: "",
  datasetType: "eval",
  status: "0"
});

const formRules = {
  datasetName: [{ required: true, message: "请输入名称", trigger: "blur" }],
  datasetType: [{ required: true, message: "请选择类型", trigger: "change" }]
};

const gridOptions = reactive({
  border: true,
  showOverflow: true,
  height: 600,
  loading: false,
  rowConfig: { keyField: "datasetId" },
  toolbarConfig: { slots: { buttons: "toolbar_buttons" }, refresh: true, zoom: false },
  proxyConfig: {
    form: true,
    response: { result: "list", total: "total" },
    ajax: {
      query: async ({ page, form }) => {
        const params = { pageNum: page.currentPage, pageSize: page.pageSize, ...form };
        try {
          const res = await getDatasetPageApi(params);
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
      { field: "keyword", title: "关键词", span: 6, itemRender: { name: "$input", props: { placeholder: "名称" } } },
      {
        field: "datasetType",
        title: "类型",
        span: 6,
        itemRender: {
          name: "$select",
          options: [
            { label: "全部", value: "" },
            { label: "模型评测", value: "eval" },
            { label: "RAG评估", value: "rag" }
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
    { field: "datasetName", title: "名称", minWidth: 150 },
    { field: "datasetCode", title: "编码", width: 160 },
    { field: "datasetType", title: "类型", width: 110, slots: { default: "datasetType" } },
    { field: "rowCount", title: "行数", width: 80 },
    { field: "status", title: "状态", width: 90, slots: { default: "status" } },
    { field: "createTime", title: "创建时间", width: 170 },
    { title: "操作", width: 220, fixed: "right", slots: { default: "action" } }
  ]
});

const handleRefresh = () => xGrid.value?.commitProxy("query");

const handleAdd = () => {
  isEdit.value = false;
  Object.assign(formData, { datasetId: null, datasetName: "", datasetCode: "", datasetType: "eval", status: "0" });
  dialogVisible.value = true;
};

const handleEdit = row => {
  isEdit.value = true;
  Object.assign(formData, {
    datasetId: row.datasetId,
    datasetName: row.datasetName || "",
    datasetCode: row.datasetCode || "",
    datasetType: row.datasetType || "eval",
    status: row.status || "0"
  });
  dialogVisible.value = true;
};

const handleSubmit = async () => {
  try {
    await formRef.value.validate();
    submitLoading.value = true;
    const payload = {
      datasetId: isEdit.value ? formData.datasetId : null,
      datasetName: formData.datasetName,
      datasetCode: formData.datasetCode || null,
      datasetType: formData.datasetType,
      status: formData.status
    };
    if (isEdit.value) await updateDatasetApi(payload);
    else await addDatasetApi(payload);
    ElMessage.success(isEdit.value ? "编辑成功" : "新增成功");
    dialogVisible.value = false;
    handleRefresh();
  } catch (e) {
    if (e !== false) ElMessage.error("操作失败");
  } finally {
    submitLoading.value = false;
  }
};

const handleDelete = row => {
  ElMessageBox.confirm(`确定删除数据集「${row.datasetName}」及其样本行？`, "提示", { type: "warning" }).then(async () => {
    try {
      await deleteDatasetApi(row.datasetId);
      ElMessage.success("删除成功");
      handleRefresh();
    } catch (e) {
      ElMessage.error("删除失败");
    }
  });
};

// ===== 样本行管理 =====
const rowDrawer = ref(false);
const currentDataset = reactive({ datasetId: null, datasetName: "" });
const rowList = ref([]);
const rowTotal = ref(0);
const rowLoading = ref(false);
const rowPage = reactive({ pageNum: 1, pageSize: 10 });

const openRows = row => {
  currentDataset.datasetId = row.datasetId;
  currentDataset.datasetName = row.datasetName;
  rowPage.pageNum = 1;
  rowDrawer.value = true;
  loadRows();
};

const loadRows = async () => {
  rowLoading.value = true;
  try {
    const res = await getDatasetRowPageApi({
      datasetId: currentDataset.datasetId,
      pageNum: rowPage.pageNum,
      pageSize: rowPage.pageSize
    });
    const data = res.data || {};
    rowList.value = data.records || [];
    rowTotal.value = data.total || 0;
  } catch (e) {
    rowList.value = [];
    rowTotal.value = 0;
  } finally {
    rowLoading.value = false;
  }
};

const onRowPageChange = p => {
  rowPage.pageNum = p;
  loadRows();
};

// 样本行 新增/编辑
const rowDialog = ref(false);
const rowFormRef = ref();
const rowIsEdit = ref(false);
const rowSubmitLoading = ref(false);
const rowForm = reactive({ rowId: null, datasetId: null, inputText: "", outputText: "", metadataJson: "" });
const rowRules = {
  inputText: [{ required: true, message: "请输入内容", trigger: "blur" }]
};

const handleAddRow = () => {
  rowIsEdit.value = false;
  Object.assign(rowForm, { rowId: null, datasetId: currentDataset.datasetId, inputText: "", outputText: "", metadataJson: "" });
  rowDialog.value = true;
};

const handleEditRow = row => {
  rowIsEdit.value = true;
  Object.assign(rowForm, {
    rowId: row.rowId,
    datasetId: currentDataset.datasetId,
    inputText: row.inputText || "",
    outputText: row.outputText || "",
    metadataJson: row.metadataJson || ""
  });
  rowDialog.value = true;
};

const handleSubmitRow = async () => {
  try {
    await rowFormRef.value.validate();
    rowSubmitLoading.value = true;
    const payload = {
      rowId: rowIsEdit.value ? rowForm.rowId : null,
      datasetId: currentDataset.datasetId,
      inputText: rowForm.inputText,
      outputText: rowForm.outputText || null,
      metadataJson: rowForm.metadataJson || null
    };
    if (rowIsEdit.value) await updateDatasetRowApi(payload);
    else await addDatasetRowApi(payload);
    ElMessage.success(rowIsEdit.value ? "编辑成功" : "新增成功");
    rowDialog.value = false;
    loadRows();
    handleRefresh();
  } catch (e) {
    if (e !== false) ElMessage.error("操作失败");
  } finally {
    rowSubmitLoading.value = false;
  }
};

const handleDeleteRow = row => {
  ElMessageBox.confirm("确定删除该样本行？", "提示", { type: "warning" }).then(async () => {
    try {
      await deleteDatasetRowApi(row.rowId, currentDataset.datasetId);
      ElMessage.success("删除成功");
      loadRows();
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
.row-toolbar {
  margin-bottom: 12px;
}
.row-pager {
  margin-top: 12px;
  display: flex;
  justify-content: flex-end;
}
</style>
