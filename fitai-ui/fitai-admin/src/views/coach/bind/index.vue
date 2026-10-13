<template>
  <div class="table-box p-5">
    <!-- 搜索区域 -->
    <div class="search-form mb-4">
      <el-form :model="searchForm" inline>
        <el-form-item label="教练">
          <el-select v-model="searchForm.coachName" placeholder="请选择" filterable clearable style="width: 160px">
            <el-option v-for="item in coachOptions" :key="item.coachId" :label="item.coachName" :value="item.coachName" />
          </el-select>
        </el-form-item>
        <el-form-item label="用户ID">
          <el-input v-model="searchForm.customerId" placeholder="请输入用户ID" clearable class="w-40" />
        </el-form-item>
        <el-form-item label="绑定类型">
          <el-select v-model="searchForm.bindType" placeholder="请选择" clearable style="width: 120px">
            <el-option label="跟练" value="1" />
            <el-option label="私教" value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择" clearable style="width: 120px">
            <el-option label="有效" value="0" />
            <el-option label="已解绑" value="1" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 表格 -->
    <vxe-grid ref="xGrid" v-bind="gridOptions">
      <template #toolbar_buttons>
        <el-button type="primary" :icon="Plus" @click="handleAdd">新增绑定</el-button>
        <el-button :icon="Refresh" @click="loadData">刷新</el-button>
      </template>

      <template #bindType="{ row }">
        <el-tag v-if="String(row.bindType) === '1'" type="primary">跟练</el-tag>
        <el-tag v-else-if="String(row.bindType) === '2'" type="warning">私教</el-tag>
        <el-tag v-else type="info">未知</el-tag>
      </template>

      <template #status="{ row }">
        <el-tag v-if="String(row.status) === '0'" type="success">有效</el-tag>
        <el-tag v-else type="info">已解绑</el-tag>
      </template>

      <template #startTime="{ row }">
        <span>{{ row.startTime ? dayjs(row.startTime).format("YYYY-MM-DD HH:mm") : "-" }}</span>
      </template>

      <template #endTime="{ row }">
        <span v-if="row.endTime">{{ dayjs(row.endTime).format("YYYY-MM-DD HH:mm") }}</span>
        <span v-else class="text-gray-400">-</span>
      </template>

      <template #action="{ row }">
        <el-button v-if="String(row.status) === '0'" type="danger" link :icon="Close" @click="handleUnbind(row)">解绑</el-button>
        <span v-else class="text-gray-400">-</span>
      </template>
    </vxe-grid>

    <div class="pagination-container">
      <el-pagination
        v-model:current-page="pageConfig.currentPage"
        v-model:page-size="pageConfig.pageSize"
        :page-sizes="[10, 20, 50, 100]"
        :total="pageConfig.total"
        background
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>

    <el-dialog v-model="dialogVisible" title="新增学员绑定" width="500px" destroy-on-close>
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
        <el-form-item label="用户ID" prop="customerId">
          <el-input v-model="formData.customerId" placeholder="请输入用户ID" />
        </el-form-item>
        <el-form-item label="教练" prop="coachId">
          <el-select v-model="formData.coachId" placeholder="请选择教练" filterable style="width: 100%">
            <el-option v-for="item in coachOptions" :key="item.coachId" :label="item.coachName" :value="item.coachId" />
          </el-select>
        </el-form-item>
        <el-form-item label="绑定类型" prop="bindType">
          <el-radio-group v-model="formData.bindType">
            <el-radio value="1">跟练</el-radio>
            <el-radio value="2">私教</el-radio>
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

<script setup name="coachBind">
import { ref, reactive, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { Search, Refresh, Plus, Close } from "@element-plus/icons-vue";
import dayjs from "dayjs";
import { getCoachListApi, getCoachBindPageApi, createCoachBindApi, unbindCoachBindApi } from "@/api/modules/coach";

const xGrid = ref();
const formRef = ref();
const dialogVisible = ref(false);
const submitLoading = ref(false);
const coachOptions = ref([]);

const searchForm = ref({
  coachName: null,
  customerId: "",
  bindType: null,
  status: null
});

const formData = reactive({
  customerId: "",
  coachId: null,
  bindType: "1"
});

const formRules = {
  customerId: [{ required: true, message: "请输入用户ID", trigger: "blur" }],
  coachId: [{ required: true, message: "请选择教练", trigger: "change" }],
  bindType: [{ required: true, message: "请选择绑定类型", trigger: "change" }]
};

const pageConfig = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
});

const tableData = ref([]);

const loadData = async () => {
  gridOptions.loading = true;
  try {
    const params = {
      pageNum: pageConfig.currentPage,
      pageSize: pageConfig.pageSize,
      coachName: searchForm.value.coachName || undefined,
      customerId: searchForm.value.customerId ? Number(searchForm.value.customerId) : undefined,
      bindType: searchForm.value.bindType || undefined,
      status: searchForm.value.status !== null && searchForm.value.status !== "" ? searchForm.value.status : undefined
    };
    const res = await getCoachBindPageApi(params);
    tableData.value = res.data?.records || [];
    pageConfig.total = Number(res.data?.total) || 0;
  } catch (error) {
    console.error("加载学员绑定列表失败:", error);
    tableData.value = [];
    pageConfig.total = 0;
  } finally {
    gridOptions.loading = false;
  }
};

const gridOptions = reactive({
  border: true,
  showOverflow: true,
  height: 560,
  loading: false,
  rowConfig: { keyField: "bindId", isHover: true },
  columnConfig: { resizable: true },
  toolbarConfig: {
    slots: { buttons: "toolbar_buttons" },
    refresh: true,
    refreshOptions: { queryMethod: loadData },
    zoom: false,
    export: true,
    custom: true
  },
  exportConfig: {
    types: ["xlsx", "csv", "pdf", "html", "xml", "txt"]
  },
  columns: [
    { type: "seq", title: "序号", width: 60 },
    { field: "bindId", title: "绑定ID", width: 90 },
    { field: "customerId", title: "用户ID", width: 100 },
    { field: "coachId", title: "教练ID", width: 100 },
    { field: "coachName", title: "教练姓名", minWidth: 120 },
    { field: "bindType", title: "绑定类型", width: 100, slots: { default: "bindType" } },
    { field: "status", title: "状态", width: 100, slots: { default: "status" } },
    { field: "startTime", title: "开始时间", width: 170, slots: { default: "startTime" } },
    { field: "endTime", title: "结束时间", width: 170, slots: { default: "endTime" } },
    { field: "createTime", title: "创建时间", width: 170 },
    { title: "操作", width: 120, fixed: "right", slots: { default: "action" } }
  ],
  data: tableData
});

const loadCoachOptions = async () => {
  try {
    const res = await getCoachListApi();
    coachOptions.value = res.data || [];
  } catch (error) {
    console.error("获取教练列表失败:", error);
    coachOptions.value = [];
  }
};

const handleSearch = () => {
  pageConfig.currentPage = 1;
  loadData();
};

const handleReset = () => {
  searchForm.value = { coachName: null, customerId: "", bindType: null, status: null };
  pageConfig.currentPage = 1;
  loadData();
};

const handleSizeChange = size => {
  pageConfig.pageSize = size;
  pageConfig.currentPage = 1;
  loadData();
};

const handleCurrentChange = page => {
  pageConfig.currentPage = page;
  loadData();
};

const handleAdd = () => {
  Object.assign(formData, {
    customerId: "",
    coachId: null,
    bindType: "1"
  });
  dialogVisible.value = true;
};

const handleSubmit = async () => {
  try {
    await formRef.value.validate();
    submitLoading.value = true;
    const coach = coachOptions.value.find(c => c.coachId === formData.coachId);
    await createCoachBindApi({
      customerId: Number(formData.customerId),
      coachId: formData.coachId,
      coachName: coach?.coachName,
      bindType: formData.bindType,
      status: "0"
    });
    ElMessage.success("绑定成功");
    dialogVisible.value = false;
    loadData();
  } finally {
    submitLoading.value = false;
  }
};

const handleUnbind = row => {
  ElMessageBox.confirm(`确定解绑用户「${row.customerId}」与教练「${row.coachName || row.coachId}」的关系吗？`, "提示", {
    type: "warning"
  })
    .then(async () => {
      await unbindCoachBindApi(row.bindId);
      ElMessage.success("已解绑");
      loadData();
    })
    .catch(() => {});
};

onMounted(() => {
  loadCoachOptions();
  loadData();
});
</script>

<style scoped>
.table-box {
  background: #fff;
  border-radius: 8px;
}

.pagination-container {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}
</style>
