<template>
  <div class="table-box p-5">
    <!-- 统计卡片 -->
    <el-row :gutter="16" class="mb-4">
      <el-col :span="5">
        <div class="stat-card">
          <div class="stat-label">秒杀活动数</div>
          <div class="stat-value text-blue-500">{{ stats.activityCount || 0 }}</div>
        </div>
      </el-col>
      <el-col :span="5">
        <div class="stat-card">
          <div class="stat-label">进行中</div>
          <div class="stat-value text-orange-500">{{ stats.ongoingCount || 0 }}</div>
        </div>
      </el-col>
      <el-col :span="5">
        <div class="stat-card">
          <div class="stat-label">订单总数</div>
          <div class="stat-value">{{ stats.orderCount || 0 }}</div>
        </div>
      </el-col>
      <el-col :span="5">
        <div class="stat-card">
          <div class="stat-label">已支付订单</div>
          <div class="stat-value text-green-500">{{ stats.paidOrderCount || 0 }}</div>
        </div>
      </el-col>
      <el-col :span="4">
        <div class="stat-card">
          <div class="stat-label">销售额</div>
          <div class="stat-value text-red-500">¥{{ Number(stats.totalSales || 0).toFixed(2) }}</div>
        </div>
      </el-col>
    </el-row>

    <vxe-grid ref="xGrid" v-bind="gridOptions">
      <template #toolbar_buttons>
        <el-button :icon="Refresh" @click="handleRefresh">刷新</el-button>
      </template>
      <template #seckillPrice="{ row }">
        <span class="text-red-500 font-medium">¥{{ Number(row.seckillPrice || 0).toFixed(2) }}</span>
      </template>
      <template #status="{ row }">
        <el-tag :type="statusMap[row.status]?.type || 'info'">
          {{ statusMap[row.status]?.label ?? row.status }}
        </el-tag>
      </template>
    </vxe-grid>
  </div>
</template>

<script setup name="seckillOrder">
import { ref, reactive, onMounted } from "vue";
import { Refresh } from "@element-plus/icons-vue";
import { getSeckillOrderPageApi, getSeckillStatsApi } from "@/api/modules/mall-ext";

const xGrid = ref();
const stats = ref({});

const statusMap = {
  0: { label: "待支付", type: "warning" },
  1: { label: "已支付", type: "success" },
  2: { label: "已取消", type: "info" }
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
      { field: "activityId", title: "活动ID", span: 6, itemRender: { name: "$input", props: { placeholder: "请输入活动ID" } } },
      {
        field: "status",
        title: "订单状态",
        span: 6,
        itemRender: {
          name: "$select",
          options: [
            { label: "全部", value: "" },
            { label: "待支付", value: 0 },
            { label: "已支付", value: 1 },
            { label: "已取消", value: 2 }
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
          const res = await getSeckillOrderPageApi(params);
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
    { field: "orderNo", title: "订单号", minWidth: 180 },
    { field: "userName", title: "用户", width: 120 },
    { field: "activityName", title: "活动名称", minWidth: 160 },
    { field: "skuId", title: "SKU ID", width: 90 },
    { field: "seckillPrice", title: "秒杀价", width: 110, slots: { default: "seckillPrice" } },
    { field: "quantity", title: "数量", width: 80 },
    { field: "status", title: "状态", width: 100, slots: { default: "status" } },
    { field: "createTime", title: "下单时间", width: 170 },
    { field: "payTime", title: "支付时间", width: 170 }
  ]
});

const loadStats = async () => {
  try {
    const res = await getSeckillStatsApi();
    stats.value = res.data || {};
  } catch (e) {
    stats.value = {};
  }
};

const handleRefresh = () => {
  xGrid.value?.commitProxy("query");
  loadStats();
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
