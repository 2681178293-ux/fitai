<template>
  <div class="table-box p-5">
    <!-- 搜索区域 -->
    <div class="search-form mb-4">
      <el-form :model="searchForm" inline>
        <el-form-item label="教练">
          <el-select v-model="searchForm.coachId" placeholder="请选择" filterable clearable style="width: 160px">
            <el-option v-for="item in coachOptions" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="用户ID">
          <el-input v-model="searchForm.userId" placeholder="请输入用户ID" clearable class="w-40" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择" clearable style="width: 120px">
            <el-option label="待确认" :value="0" />
            <el-option label="已确认" :value="1" />
            <el-option label="已完成" :value="2" />
            <el-option label="已取消" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="预约日期">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
            style="width: 260px"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 表格 -->
    <vxe-grid ref="xGrid" v-bind="gridOptions">
      <!-- 工具栏按钮 -->
      <template #toolbar_buttons>
        <el-button :icon="Refresh" @click="loadData">刷新</el-button>
      </template>

      <!-- 状态列 -->
      <template #status="{ row }">
        <el-tag v-if="row.status === 0" type="warning">待确认</el-tag>
        <el-tag v-else-if="row.status === 1" type="primary">已确认</el-tag>
        <el-tag v-else-if="row.status === 2" type="success">已完成</el-tag>
        <el-tag v-else-if="row.status === 3" type="info">已取消</el-tag>
        <el-tag v-else type="info">未知</el-tag>
      </template>

      <!-- 预约日期列 -->
      <template #bookingDate="{ row }">
        <span>{{ row.bookingDate ? dayjs(row.bookingDate).format("YYYY-MM-DD") : "-" }}</span>
      </template>

      <!-- 取消原因列 -->
      <template #cancelReason="{ row }">
        <span v-if="row.cancelReason">{{ row.cancelReason }}</span>
        <span v-else class="text-gray-400">-</span>
      </template>

      <!-- 操作列 -->
      <template #action="{ row }">
        <el-button v-if="row.status === 0" type="primary" link :icon="Check" @click="handleConfirm(row)">确认</el-button>
        <el-button v-if="row.status === 1" type="success" link :icon="Finished" @click="handleComplete(row)">完成</el-button>
        <el-button v-if="row.status === 0 || row.status === 1" type="danger" link :icon="Close" @click="handleCancel(row)">
          取消
        </el-button>
        <span v-if="row.status === 2 || row.status === 3" class="text-gray-400">-</span>
      </template>
    </vxe-grid>

    <!-- 分页 -->
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

    <!-- 取消预约弹窗 -->
    <el-dialog v-model="cancelVisible" title="取消预约" width="500px" destroy-on-close>
      <el-form ref="cancelFormRef" :model="cancelForm" :rules="cancelRules" label-width="100px">
        <el-form-item label="预约单号">
          <span>{{ cancelForm.bookingNo }}</span>
        </el-form-item>
        <el-form-item label="取消原因" prop="cancelReason">
          <el-input v-model="cancelForm.cancelReason" type="textarea" :rows="3" placeholder="请输入取消原因" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="cancelVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleCancelSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="coachBooking">
import { ref, reactive, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { Search, Refresh, Check, Close, Finished } from "@element-plus/icons-vue";
import dayjs from "dayjs";
import {
  getCoachListApi,
  getCoachBookingPageApi,
  confirmCoachBookingApi,
  completeCoachBookingApi,
  cancelCoachBookingApi
} from "@/api/modules/coach";

const xGrid = ref();
const cancelFormRef = ref();
const cancelVisible = ref(false);
const submitLoading = ref(false);
const coachOptions = ref([]);
const dateRange = ref([]);

const searchForm = ref({
  coachId: null,
  userId: "",
  status: null
});

const cancelForm = reactive({
  id: null,
  bookingNo: "",
  cancelReason: ""
});

const cancelRules = {
  cancelReason: [{ required: true, message: "请输入取消原因", trigger: "blur" }]
};

const pageConfig = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
});

const tableData = ref([]);

// 加载数据（必须在 gridOptions 之前定义，因为 toolbarConfig.refresh.queryMethod 引用它）
const loadData = async () => {
  gridOptions.loading = true;
  try {
    const params = {
      pageNum: pageConfig.currentPage,
      pageSize: pageConfig.pageSize,
      ...searchForm.value,
      startDate: dateRange.value?.[0] || null,
      endDate: dateRange.value?.[1] || null
    };
    const res = await getCoachBookingPageApi(params);
    tableData.value = res.data?.records || [];
    pageConfig.total = Number(res.data?.total) || 0;
  } catch (error) {
    console.error("加载预约列表失败:", error);
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
  rowConfig: { keyField: "id", isHover: true },
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
    { field: "bookingNo", title: "预约单号", width: 190 },
    { field: "coachName", title: "教练", width: 110 },
    { field: "coachId", title: "教练ID", width: 90 },
    { field: "userName", title: "用户昵称", width: 120 },
    { field: "userId", title: "用户ID", width: 90 },
    { field: "bookingDate", title: "预约日期", width: 120, slots: { default: "bookingDate" } },
    { field: "timeSlot", title: "时段", width: 120 },
    { field: "remark", title: "备注", minWidth: 140 },
    { field: "status", title: "状态", width: 90, slots: { default: "status" } },
    { field: "cancelReason", title: "取消原因", minWidth: 140, slots: { default: "cancelReason" } },
    { field: "createTime", title: "创建时间", width: 170 },
    { title: "操作", width: 180, fixed: "right", slots: { default: "action" } }
  ],
  data: tableData
});

// 加载教练下拉
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
  searchForm.value = { coachId: null, userId: "", status: null };
  dateRange.value = [];
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

// 确认预约
const handleConfirm = row => {
  ElMessageBox.confirm(`确定确认预约单「${row.bookingNo}」吗？`, "提示", {
    type: "warning"
  })
    .then(async () => {
      await confirmCoachBookingApi(row.id);
      ElMessage.success("已确认");
      loadData();
    })
    .catch(() => {});
};

// 完成预约
const handleComplete = row => {
  ElMessageBox.confirm(`确定将预约单「${row.bookingNo}」标记为已完成吗？`, "提示", {
    type: "warning"
  })
    .then(async () => {
      await completeCoachBookingApi(row.id);
      ElMessage.success("已完成");
      loadData();
    })
    .catch(() => {});
};

// 打开取消弹窗
const handleCancel = row => {
  Object.assign(cancelForm, {
    id: row.id,
    bookingNo: row.bookingNo,
    cancelReason: ""
  });
  cancelVisible.value = true;
};

// 提交取消
const handleCancelSubmit = async () => {
  try {
    await cancelFormRef.value.validate();
    submitLoading.value = true;
    await cancelCoachBookingApi(cancelForm.id, { cancelReason: cancelForm.cancelReason });
    ElMessage.success("已取消预约");
    cancelVisible.value = false;
    loadData();
  } finally {
    submitLoading.value = false;
  }
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
