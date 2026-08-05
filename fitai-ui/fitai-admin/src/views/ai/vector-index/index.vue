<template>
  <div class="table-box p-5">
    <vxe-grid ref="xGrid" v-bind="gridOptions">
      <template #toolbar_buttons>
        <el-button type="primary" :icon="Plus" @click="handleAdd">新增索引</el-button>
        <el-button :icon="Refresh" @click="handleRefresh">刷新</el-button>
      </template>
      <template #indexStatus="{ row }">
        <el-tag v-if="row.indexStatus === '0'" type="success">正常</el-tag>
        <el-tag v-else-if="row.indexStatus === '1'" type="warning">重建中</el-tag>
        <el-tag v-else-if="row.indexStatus === '2'" type="danger">异常</el-tag>
        <el-tag v-else type="info">-</el-tag>
      </template>
      <template #action="{ row }">
        <el-button type="primary" link :icon="View" @click="handleView(row)">状态</el-button>
        <el-button type="success" link :icon="RefreshRight" :disabled="row.indexStatus === '1'" @click="handleRebuild(row)">重建</el-button>
        <el-button type="warning" link :icon="Connection" @click="handleSync(row)">同步</el-button>
        <el-button type="primary" link :icon="Edit" @click="handleEdit(row)">编辑</el-button>
        <el-button type="danger" link :icon="Delete" @click="handleDelete(row)">删除</el-button>
      </template>
    </vxe-grid>

    <!-- 新增/编辑 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑向量索引' : '新增向量索引'" width="640px" destroy-on-close>
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="110px">
        <el-form-item label="索引名" prop="indexName">
          <el-input v-model="formData.indexName" placeholder="索引名" />
        </el-form-item>
        <el-form-item label="键前缀" prop="indexPrefix">
          <el-input v-model="formData.indexPrefix" placeholder="如 fitai:vector:" />
        </el-form-item>
        <el-form-item label="知识库ID">
          <el-input v-model="formData.knowledgeId" placeholder="选填" />
        </el-form-item>
        <el-form-item label="向量维度">
          <el-input-number v-model="formData.vectorDims" :min="0" style="width: 160px" />
        </el-form-item>
        <el-form-item label="距离度量">
          <el-select v-model="formData.distanceMetric" placeholder="请选择" clearable style="width: 100%">
            <el-option label="COSINE" value="COSINE" />
            <el-option label="L2" value="L2" />
            <el-option label="IP" value="IP" />
          </el-select>
        </el-form-item>
        <el-form-item label="索引算法">
          <el-select v-model="formData.algorithm" placeholder="请选择" clearable style="width: 100%">
            <el-option label="HNSW" value="HNSW" />
            <el-option label="FLAT" value="FLAT" />
          </el-select>
        </el-form-item>
        <el-form-item label="嵌入模型">
          <el-input v-model="formData.embeddingModel" placeholder="嵌入模型编码，选填" />
        </el-form-item>
        <el-form-item label="连接地址">
          <el-input v-model="formData.endpoint" placeholder="选填" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="formData.indexStatus" placeholder="请选择" style="width: 100%">
            <el-option label="正常" value="0" />
            <el-option label="重建中" value="1" />
            <el-option label="异常" value="2" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 状态详情 -->
    <el-dialog v-model="viewVisible" title="索引状态" width="560px" destroy-on-close>
      <el-descriptions v-if="viewData" :column="1" border size="small">
        <el-descriptions-item label="索引名">{{ viewData.indexName }}</el-descriptions-item>
        <el-descriptions-item label="引擎">{{ viewData.vectorEngine }}</el-descriptions-item>
        <el-descriptions-item label="键前缀">{{ viewData.indexPrefix }}</el-descriptions-item>
        <el-descriptions-item label="文档数（登记）">{{ viewData.docCount ?? "-" }}</el-descriptions-item>
        <el-descriptions-item label="Redis 实际键数">{{ viewData.redisKeyCount ?? "查询不可用" }}</el-descriptions-item>
        <el-descriptions-item label="向量维度">{{ viewData.vectorDims ?? "-" }}</el-descriptions-item>
        <el-descriptions-item label="距离度量 / 算法">{{ viewData.distanceMetric || "-" }} / {{ viewData.algorithm || "-" }}</el-descriptions-item>
        <el-descriptions-item label="最后重建时间">{{ viewData.lastRebuildTime || "-" }}</el-descriptions-item>
        <el-descriptions-item label="状态">{{ statusLabel(viewData.indexStatus) }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup name="aiVectorIndex">
import { ref, reactive } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { Plus, Refresh, Edit, Delete, View, RefreshRight, Connection } from "@element-plus/icons-vue";
import {
  getVectorIndexPageApi,
  getVectorIndexDetailApi,
  addVectorIndexApi,
  updateVectorIndexApi,
  rebuildVectorIndexApi,
  syncVectorIndexApi,
  deleteVectorIndexApi
} from "@/api/modules/ai-agent";

const xGrid = ref();
const formRef = ref();
const dialogVisible = ref(false);
const isEdit = ref(false);
const submitLoading = ref(false);

const STATUS_LABELS = { "0": "正常", "1": "重建中", "2": "异常" };
const statusLabel = s => STATUS_LABELS[s] || "-";

const formData = reactive({
  indexId: null,
  indexName: "",
  indexPrefix: "",
  knowledgeId: "",
  vectorDims: null,
  distanceMetric: "",
  algorithm: "",
  embeddingModel: "",
  endpoint: "",
  indexStatus: "0"
});

const formRules = {
  indexName: [{ required: true, message: "请输入索引名", trigger: "blur" }],
  indexPrefix: [{ required: true, message: "请输入键前缀", trigger: "blur" }]
};

const gridOptions = reactive({
  border: true,
  showOverflow: true,
  height: 600,
  loading: false,
  rowConfig: { keyField: "indexId" },
  toolbarConfig: { slots: { buttons: "toolbar_buttons" }, refresh: true, zoom: false },
  proxyConfig: {
    form: true,
    response: { result: "list", total: "total" },
    ajax: {
      query: async ({ page, form }) => {
        const params = { pageNum: page.currentPage, pageSize: page.pageSize, ...form };
        try {
          const res = await getVectorIndexPageApi(params);
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
      { field: "keyword", title: "关键词", span: 6, itemRender: { name: "$input", props: { placeholder: "索引名" } } },
      {
        field: "indexStatus",
        title: "状态",
        span: 6,
        itemRender: {
          name: "$select",
          options: [
            { label: "全部", value: "" },
            { label: "正常", value: "0" },
            { label: "重建中", value: "1" },
            { label: "异常", value: "2" }
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
    { field: "indexName", title: "索引名", minWidth: 140 },
    { field: "vectorEngine", title: "引擎", width: 90 },
    { field: "indexPrefix", title: "键前缀", minWidth: 150 },
    { field: "docCount", title: "文档数", width: 90 },
    { field: "algorithm", title: "算法", width: 90 },
    { field: "indexStatus", title: "状态", width: 100, slots: { default: "indexStatus" } },
    { field: "lastRebuildTime", title: "最后重建", width: 170 },
    { title: "操作", width: 300, fixed: "right", slots: { default: "action" } }
  ]
});

const handleRefresh = () => xGrid.value?.commitProxy("query");

const resetForm = () => {
  Object.assign(formData, {
    indexId: null,
    indexName: "",
    indexPrefix: "",
    knowledgeId: "",
    vectorDims: null,
    distanceMetric: "",
    algorithm: "",
    embeddingModel: "",
    endpoint: "",
    indexStatus: "0"
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
    const res = await getVectorIndexDetailApi(row.indexId);
    const d = res.data || {};
    Object.assign(formData, {
      indexId: d.indexId,
      indexName: d.indexName || "",
      indexPrefix: d.indexPrefix || "",
      knowledgeId: d.knowledgeId ?? "",
      vectorDims: d.vectorDims ?? null,
      distanceMetric: d.distanceMetric || "",
      algorithm: d.algorithm || "",
      embeddingModel: d.embeddingModel || "",
      endpoint: d.endpoint || "",
      indexStatus: d.indexStatus || "0"
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
      indexName: formData.indexName,
      indexPrefix: formData.indexPrefix,
      knowledgeId: formData.knowledgeId === "" ? null : Number(formData.knowledgeId),
      vectorDims: formData.vectorDims ?? null,
      distanceMetric: formData.distanceMetric || null,
      algorithm: formData.algorithm || null,
      embeddingModel: formData.embeddingModel || null,
      endpoint: formData.endpoint || null,
      indexStatus: formData.indexStatus
    };
    if (isEdit.value) {
      payload.indexId = formData.indexId;
      await updateVectorIndexApi(payload);
    } else {
      await addVectorIndexApi(payload);
    }
    ElMessage.success(isEdit.value ? "编辑成功" : "新增成功");
    dialogVisible.value = false;
    handleRefresh();
  } catch (e) {
    if (e !== false) ElMessage.error("操作失败");
  } finally {
    submitLoading.value = false;
  }
};

const viewVisible = ref(false);
const viewData = ref(null);
const handleView = async row => {
  try {
    const res = await getVectorIndexDetailApi(row.indexId);
    viewData.value = res.data || row;
  } catch (e) {
    viewData.value = row;
  }
  viewVisible.value = true;
};

const handleRebuild = row => {
  ElMessageBox.confirm(`确定重建索引「${row.indexName}」？将异步全量重建向量。`, "提示", { type: "warning" }).then(async () => {
    try {
      await rebuildVectorIndexApi(row.indexId);
      ElMessage.success("重建任务已提交");
      handleRefresh();
    } catch (e) {
      ElMessage.error(e?.msg || e?.message || "提交失败");
    }
  });
};

const handleSync = row => {
  syncVectorIndexApi(row.indexId)
    .then(() => {
      ElMessage.success("同步成功");
      handleRefresh();
    })
    .catch(e => ElMessage.error(e?.msg || e?.message || "同步失败"));
};

const handleDelete = row => {
  ElMessageBox.confirm(`确定删除索引「${row.indexName}」（仅删元数据）？`, "提示", { type: "warning" }).then(async () => {
    try {
      await deleteVectorIndexApi(row.indexId);
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
