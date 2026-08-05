<template>
  <div class="table-box p-5">
    <!-- 统计卡片 -->
    <el-row :gutter="16" class="mb-4">
      <el-col :span="6">
        <div class="stat-card">
          <div class="stat-label">领取总数</div>
          <div class="stat-value text-blue-500">{{ stats.totalCount || 0 }}</div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card">
          <div class="stat-label">未使用</div>
          <div class="stat-value text-orange-500">{{ stats.unusedCount || 0 }}</div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card">
          <div class="stat-label">已使用</div>
          <div class="stat-value text-green-500">{{ stats.usedCount || 0 }}</div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card">
          <div class="stat-label">已过期/作废</div>
          <div class="stat-value text-gray-500">{{ stats.expiredCount || 0 }}</div>
        </div>
      </el-col>
    </el-row>

    <vxe-grid ref="xGrid" v-bind="gridOptions">
      <template #toolbar_buttons>
        <el-button :icon="Refresh" @click="handleRefresh">刷新</el-button>
      </template>
      <template #status="{ row }">
        <el-tag :type="statusMap[row.status]?.type || 'info'">
          {{ statusMap[row.status]?.label ?? row.status }}
        </el-tag>
      </template>
      <template #action="{ row }">
        <el-button v-if="row.status === 0" type="danger" link @click="handleVoid(row)">作废</el-button>
        <span v-else class="text-gray-400">-</span>
      </template>
    </vxe-grid>
  </div>
</template>

<script setup name="couponRecv">
import { ref, reactive, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { Refresh } from "@element-plus/icons-vue";
import { getCouponRecvPageApi, voidCouponRecvApi, getCouponRecvStatsApi } from "@/api/modules/mall-ext";

const xGrid = ref();
const stats = ref({});

const statusMap = {
  0: { label: "未使用", type: "warning" },
  1: { label: "已使用", type: "success" },
  2: { label: "已过期/作废", type: "info" }
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
      { field: "couponName", title: "优惠券名称", span: 6, itemRender: { name: "$input", props: { placeholder: "优惠券名称" } } },
      { field: "couponId", title: "优惠券ID", span: 6, itemRender: { name: "$input", props: { placeholder: "优惠券ID" } } },
      {
        field: "status",
        title: "状态",
        span: 6,
        itemRender: {
          name: "$select",
          options: [
            { label: "全部", value: "" },
            { label: "未使用", value: 0 },
            { label: "已使用", value: 1 },
            { label: "已过期/作废", value: 2 }
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
          const res = await getCouponRecvPageApi(params);
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
    { field: "couponName", title: "优惠券名称", minWidth: 160 },
    { field: "userName", title: "用户", width: 120 },
    { field: "status", title: "状态", width: 120, slots: { default: "status" } },
    { field: "orderNo", title: "使用订单号", minWidth: 180 },
    { field: "createTime", title: "领取时间", width: 170 },
    { field: "useTime", title: "使用时间", width: 170 },
    { field: "expireTime", title: "过期时间", width: 170 },
    { title: "操作", width: 100, fixed: "right", slots: { default: "action" } }
  ]
});

const loadStats = async () => {
  try {
    const res = await getCouponRecvStatsApi();
    stats.value = res.data || {};
  } catch (e) {
    stats.value = {};
  }
};

const handleRefresh = () => {
  xGrid.value?.commitProxy("query");
  loadStats();
};

const handleVoid = row => {
  ElMessageBox.confirm(`确定作废用户「${row.userName || row.userId}」领取的优惠券「${row.couponName}」？`, "作废确认", {
    type: "warning"
  }).then(async () => {
    try {
      await voidCouponRecvApi(row.id);
      ElMessage.success("已作废");
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
