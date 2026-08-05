<template>
  <div class="table-box p-5">
    <!-- 统计卡片 -->
    <el-row :gutter="16" class="mb-4">
      <el-col :span="5">
        <div class="stat-card">
          <div class="stat-label">待审核</div>
          <div class="stat-value text-orange-500">{{ stats.pendingCount || 0 }}</div>
        </div>
      </el-col>
      <el-col :span="5">
        <div class="stat-card">
          <div class="stat-label">已同意</div>
          <div class="stat-value text-blue-500">{{ stats.approvedCount || 0 }}</div>
        </div>
      </el-col>
      <el-col :span="5">
        <div class="stat-card">
          <div class="stat-label">已拒绝</div>
          <div class="stat-value text-gray-500">{{ stats.rejectedCount || 0 }}</div>
        </div>
      </el-col>
      <el-col :span="5">
        <div class="stat-card">
          <div class="stat-label">已退款</div>
          <div class="stat-value text-green-500">{{ stats.refundedCount || 0 }}</div>
        </div>
      </el-col>
      <el-col :span="4">
        <div class="stat-card">
          <div class="stat-label">今日退款额</div>
          <div class="stat-value text-red-500">¥{{ Number(stats.todayAmount || 0).toFixed(2) }}</div>
        </div>
      </el-col>
    </el-row>

    <vxe-grid ref="xGrid" v-bind="gridOptions">
      <template #toolbar_buttons>
        <el-button :icon="Refresh" @click="handleRefresh">刷新</el-button>
      </template>
      <template #amount="{ row }">
        <span class="text-red-500 font-medium">¥{{ Number(row.amount || 0).toFixed(2) }}</span>
      </template>
      <template #auditStatus="{ row }">
        <el-tag :type="auditStatusMap[row.auditStatus]?.type || 'info'">
          {{ auditStatusMap[row.auditStatus]?.label ?? row.auditStatus }}
        </el-tag>
      </template>
      <template #action="{ row }">
        <el-button type="primary" link :icon="View" @click="handleView(row)">详情</el-button>
        <el-button v-if="row.auditStatus === 0" type="success" link @click="handleApprove(row)">同意</el-button>
        <el-button v-if="row.auditStatus === 0" type="danger" link @click="handleReject(row)">拒绝</el-button>
        <el-button v-if="row.auditStatus === 1" type="warning" link @click="handleComplete(row)">退款执行</el-button>
      </template>
    </vxe-grid>

    <!-- 详情弹窗 -->
    <el-dialog v-model="detailVisible" title="退款申请详情" width="680px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="退款单号">{{ current.refundNo }}</el-descriptions-item>
        <el-descriptions-item label="订单号">{{ current.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="交易流水号">{{ current.transactionNo || '-' }}</el-descriptions-item>
        <el-descriptions-item label="用户ID">{{ current.userId }}</el-descriptions-item>
        <el-descriptions-item label="退款金额">
          <span class="text-red-500 font-bold">¥{{ Number(current.amount || 0).toFixed(2) }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="审核状态">
          <el-tag :type="auditStatusMap[current.auditStatus]?.type">
            {{ auditStatusMap[current.auditStatus]?.label ?? current.auditStatus }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="退款原因" :span="2">{{ current.reason || '-' }}</el-descriptions-item>
        <el-descriptions-item label="审核备注" :span="2">{{ current.auditRemark || '-' }}</el-descriptions-item>
        <el-descriptions-item label="审核时间">{{ current.auditTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="退款时间">{{ current.refundTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="申请时间" :span="2">{{ current.createTime || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup name="orderRefund">
import { ref, reactive, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { Refresh, View } from "@element-plus/icons-vue";
import {
  getRefundPageApi,
  getRefundDetailApi,
  approveRefundApi,
  rejectRefundApi,
  completeRefundApi,
  getRefundStatsApi
} from "@/api/modules/mall-ext";

const xGrid = ref();
const detailVisible = ref(false);
const current = ref({});
const stats = ref({});

const auditStatusMap = {
  0: { label: "待审核", type: "warning" },
  1: { label: "已同意", type: "primary" },
  2: { label: "已拒绝", type: "info" },
  3: { label: "已退款", type: "success" }
};

const gridOptions = reactive({
  border: true,
  showOverflow: true,
  height: 560,
  loading: false,
  rowConfig: { keyField: "id" },
  toolbarConfig: { slots: { buttons: "toolbar_buttons" }, refresh: true, zoom: false },
  pagerConfig: { enabled: true, pageSize: 10, pageSizes: [10, 20, 50, 100] },
  formConfig: {
    items: [
      { field: "orderNo", title: "订单号", span: 6, itemRender: { name: "$input", props: { placeholder: "请输入订单号" } } },
      { field: "refundNo", title: "退款单号", span: 6, itemRender: { name: "$input", props: { placeholder: "请输入退款单号" } } },
      {
        field: "auditStatus",
        title: "审核状态",
        span: 6,
        itemRender: {
          name: "$select",
          options: [
            { label: "全部", value: "" },
            { label: "待审核", value: 0 },
            { label: "已同意", value: 1 },
            { label: "已拒绝", value: 2 },
            { label: "已退款", value: 3 }
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
    form: true,
    response: { result: "list", total: "total" },
    ajax: {
      query: async ({ page, form }) => {
        const params = { pageNum: page.currentPage, pageSize: page.pageSize, ...form };
        try {
          const res = await getRefundPageApi(params);
          const data = res.data || {};
          return { list: data.records || [], total: data.total || 0 };
        } catch (e) {
          return { list: [], total: 0 };
        }
      }
    }
  },
  columns: [
    { type: "seq", width: 60, title: "序号" },
    { field: "refundNo", title: "退款单号", minWidth: 180 },
    { field: "orderNo", title: "订单号", minWidth: 180 },
    { field: "userId", title: "用户ID", width: 100 },
    { field: "amount", title: "退款金额", width: 120, slots: { default: "amount" } },
    { field: "reason", title: "退款原因", minWidth: 150 },
    { field: "auditStatus", title: "审核状态", width: 100, slots: { default: "auditStatus" } },
    { field: "createTime", title: "申请时间", width: 170 },
    { title: "操作", width: 200, fixed: "right", slots: { default: "action" } }
  ]
});

const loadStats = async () => {
  try {
    const res = await getRefundStatsApi();
    stats.value = res.data || {};
  } catch (e) {
    stats.value = {};
  }
};

const handleRefresh = () => {
  xGrid.value?.commitProxy("query");
  loadStats();
};

const handleView = async row => {
  try {
    const res = await getRefundDetailApi(row.id);
    current.value = res.data || row;
  } catch (e) {
    current.value = row;
  }
  detailVisible.value = true;
};

const handleApprove = row => {
  ElMessageBox.confirm(`确定同意退款单「${row.refundNo}」的申请？`, "同意退款", { type: "warning" }).then(async () => {
    try {
      await approveRefundApi(row.id);
      ElMessage.success("已同意");
      handleRefresh();
    } catch (e) {
      /* 拦截器已提示 */
    }
  });
};

const handleReject = row => {
  ElMessageBox.prompt("请输入拒绝原因", "拒绝退款", {
    inputPlaceholder: "拒绝原因（选填）",
    type: "warning"
  }).then(async ({ value }) => {
    try {
      await rejectRefundApi(row.id, value || "");
      ElMessage.success("已拒绝，订单恢复原状态");
      handleRefresh();
    } catch (e) {
      /* 拦截器已提示 */
    }
  });
};

const handleComplete = row => {
  ElMessageBox.confirm(
    `确定对退款单「${row.refundNo}」执行退款？执行后订单状态将置为【已退款】，不可撤销。`,
    "退款执行确认",
    { type: "warning" }
  ).then(async () => {
    try {
      await completeRefundApi(row.id);
      ElMessage.success("退款已执行");
      handleRefresh();
    } catch (e) {
      /* 拦截器已提示 */
    }
  });
};

onMounted(loadStats);
</script>

<style scoped>
.table-box {
  background: #fff;
  border-radius: 8px;
}
.stat-card {
  background: #f7f8fa;
  border-radius: 8px;
  padding: 14px 18px;
}
.stat-label {
  font-size: 13px;
  color: #909399;
}
.stat-value {
  font-size: 24px;
  font-weight: 600;
  margin-top: 6px;
}
</style>
