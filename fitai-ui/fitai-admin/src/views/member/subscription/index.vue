<template>
  <div class="table-box p-5">
    <!-- KPI 卡片 -->
    <div class="grid grid-cols-1 md:grid-cols-3 gap-4 mb-4">
      <div class="kpi-card">
        <div class="kpi-label">生效中会员</div>
        <div class="kpi-value text-green-500">{{ kpi.activeCount ?? 0 }}</div>
      </div>
      <div class="kpi-card">
        <div class="kpi-label">本月新增</div>
        <div class="kpi-value text-blue-500">{{ kpi.monthNewCount ?? 0 }}</div>
      </div>
      <div class="kpi-card">
        <div class="kpi-label">7日内到期</div>
        <div class="kpi-value text-orange-500">{{ kpi.expiringCount ?? 0 }}</div>
      </div>
    </div>

    <vxe-grid ref="xGrid" v-bind="gridOptions">
      <template #toolbar_buttons>
        <el-button :icon="Refresh" @click="handleRefresh">刷新</el-button>
      </template>

      <template #price="{ row }">
        <span class="text-red-500 font-medium">¥{{ row.price != null ? Number(row.price).toFixed(2) : "-" }}</span>
      </template>

      <template #startTime="{ row }">
        <span>{{ row.startTime ? dayjs(row.startTime).format("YYYY-MM-DD HH:mm") : "-" }}</span>
      </template>

      <template #expireTime="{ row }">
        <span>{{ row.expireTime ? dayjs(row.expireTime).format("YYYY-MM-DD HH:mm") : "-" }}</span>
      </template>

      <template #status="{ row }">
        <el-tag :type="statusMap[row.status]?.type || 'info'">
          {{ statusMap[row.status]?.label ?? row.status ?? "-" }}
        </el-tag>
      </template>

      <template #action="{ row }">
        <el-button
          type="danger"
          link
          :icon="CircleClose"
          :disabled="String(row.status) !== '0'"
          @click="handleCancel(row)"
        >
          取消订阅
        </el-button>
      </template>
    </vxe-grid>
  </div>
</template>

<script setup name="memberSubscription">
import { ref, reactive, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { Refresh, CircleClose } from "@element-plus/icons-vue";
import dayjs from "dayjs";
import {
  getSubscriptionPageApi,
  getSubscriptionKpiApi,
  cancelSubscriptionApi,
  getMemberPlansApi
} from "@/api/modules/member";

const xGrid = ref();

const kpi = reactive({
  activeCount: 0,
  monthNewCount: 0,
  expiringCount: 0
});

// 与后端 MemSubscriptionVO 一致：0有效 1已过期 2已取消
const statusMap = {
  0: { label: "生效中", type: "success" },
  "0": { label: "生效中", type: "success" },
  1: { label: "已过期", type: "info" },
  "1": { label: "已过期", type: "info" },
  2: { label: "已取消", type: "danger" },
  "2": { label: "已取消", type: "danger" }
};

const gridOptions = reactive({
  border: true,
  showOverflow: true,
  height: 560,
  loading: false,
  rowConfig: { keyField: "subscriptionId", isHover: true },
  columnConfig: { resizable: true },
  toolbarConfig: {
    slots: { buttons: "toolbar_buttons" },
    refresh: true,
    zoom: false,
    export: true,
    custom: true
  },
  exportConfig: {
    types: ["xlsx", "csv", "pdf", "html", "xml", "txt"]
  },
  formConfig: {
    items: [
      { field: "customerId", title: "用户ID", span: 6, itemRender: { name: "$input", props: { placeholder: "可选" } } },
      {
        field: "packageName",
        title: "套餐",
        span: 6,
        itemRender: {
          name: "$select",
          options: [{ label: "全部", value: "" }],
          props: { placeholder: "全部" }
        }
      },
      {
        field: "status",
        title: "订阅状态",
        span: 6,
        itemRender: {
          name: "$select",
          options: [
            { label: "全部", value: "" },
            { label: "生效中", value: "0" },
            { label: "已过期", value: "1" },
            { label: "已取消", value: "2" }
          ],
          props: { placeholder: "全部" }
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
  pagerConfig: {
    enabled: true,
    pageSize: 10,
    pageSizes: [10, 20, 50]
  },
  proxyConfig: {
    seq: true,
    form: true,
    response: { result: "list", total: "total" },
    ajax: {
      query: async ({ page, form }) => {
        try {
          const res = await getSubscriptionPageApi({
            pageNum: page.currentPage,
            pageSize: page.pageSize,
            customerId: form.customerId ? Number(form.customerId) : undefined,
            packageName: form.packageName || undefined,
            status: form.status !== "" && form.status !== undefined && form.status !== null ? form.status : undefined
          });
          const pageData = res.data || {};
          return { list: pageData.records || [], total: pageData.total || 0 };
        } catch (e) {
          console.error(e);
          return { list: [], total: 0 };
        }
      }
    }
  },
  columns: [
    { type: "seq", title: "序号", width: 60 },
    { field: "memberOrderId", title: "订单ID", minWidth: 120 },
    { field: "customerId", title: "用户ID", width: 100 },
    { field: "nickname", title: "用户昵称", minWidth: 130 },
    { field: "packageName", title: "套餐名称", minWidth: 130 },
    { field: "price", title: "金额", width: 110, slots: { default: "price" } },
    { field: "startTime", title: "开始时间", minWidth: 160, slots: { default: "startTime" } },
    { field: "expireTime", title: "到期时间", minWidth: 160, slots: { default: "expireTime" } },
    { field: "status", title: "订阅状态", width: 110, slots: { default: "status" } },
    { title: "操作", width: 130, fixed: "right", slots: { default: "action" } }
  ]
});

const handleRefresh = () => {
  xGrid.value?.commitProxy("query");
  loadKpi();
};

const loadKpi = async () => {
  try {
    const res = await getSubscriptionKpiApi();
    const data = res?.data || {};
    kpi.activeCount = data.activeCount ?? 0;
    kpi.monthNewCount = data.monthNewCount ?? 0;
    kpi.expiringCount = data.expiringCount ?? 0;
  } catch (e) {
    console.error(e);
  }
};

const loadPlanOptions = async () => {
  try {
    const res = await getMemberPlansApi();
    const list = Array.isArray(res?.data) ? res.data : [];
    const planItem = gridOptions.formConfig.items.find(i => i.field === "packageName");
    if (planItem) {
      planItem.itemRender.options = [
        { label: "全部", value: "" },
        ...list.map(p => ({ label: p.name, value: p.name }))
      ];
    }
  } catch (e) {
    console.error(e);
  }
};

const handleCancel = row => {
  ElMessageBox.confirm(`确定取消用户「${row.nickname || row.customerId}」的会员订阅吗？`, "提示", { type: "warning" })
    .then(async () => {
      await cancelSubscriptionApi(row.subscriptionId);
      ElMessage.success("已取消订阅");
      handleRefresh();
    })
    .catch(() => {});
};

onMounted(() => {
  loadKpi();
  loadPlanOptions();
});
</script>

<style scoped>
.table-box {
  background: #fff;
  border-radius: 8px;
}

.kpi-card {
  padding: 16px 20px;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.kpi-label {
  font-size: 13px;
  color: #909399;
  margin-bottom: 8px;
}

.kpi-value {
  font-size: 26px;
  font-weight: 600;
}
</style>
