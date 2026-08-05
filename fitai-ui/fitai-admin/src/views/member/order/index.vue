<template>
  <div class="table-box p-5">
    <vxe-grid ref="xGrid" v-bind="gridOptions">
      <template #toolbar_buttons>
        <el-button :icon="Refresh" @click="handleRefresh">刷新</el-button>
      </template>

      <template #status="{ row }">
        <el-tag :type="orderStatusMap[row.status]?.type || 'info'">
          {{ orderStatusMap[row.status]?.label ?? row.status }}
        </el-tag>
      </template>

      <template #amount="{ row }">
        <span class="text-red-500 font-medium">¥{{ row.amount != null ? Number(row.amount).toFixed(2) : "-" }}</span>
      </template>

      <template #action="{ row }">
        <el-button type="primary" link :icon="View" @click="handleView(row)">详情</el-button>
      </template>
    </vxe-grid>

    <el-dialog v-model="detailVisible" title="会员订单详情" width="640px">
      <el-descriptions v-if="current" :column="2" border>
        <el-descriptions-item label="订单号" :span="2">{{ current.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="用户ID">{{ current.userId }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="orderStatusMap[current.status]?.type">
            {{ orderStatusMap[current.status]?.label }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="套餐">{{ current.planName }}</el-descriptions-item>
        <el-descriptions-item label="套餐ID">{{ current.planId }}</el-descriptions-item>
        <el-descriptions-item label="实付金额">
          <span class="text-red-500 font-semibold">¥{{ current.amount != null ? Number(current.amount).toFixed(2) : "-" }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="支付方式">{{ current.payType || "-" }}</el-descriptions-item>
        <el-descriptions-item label="支付时间">{{ current.payTime || "-" }}</el-descriptions-item>
        <el-descriptions-item label="会员开始">{{ current.startTime || "-" }}</el-descriptions-item>
        <el-descriptions-item label="会员结束">{{ current.endTime || "-" }}</el-descriptions-item>
        <el-descriptions-item label="创建时间" :span="2">{{ current.createTime || "-" }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup name="memberOrder">
import { ref, reactive } from "vue";
import { Refresh, View } from "@element-plus/icons-vue";
import { getMemberOrderPageApi, getMemberOrderDetailApi } from "@/api/modules/member";

const xGrid = ref();
const detailVisible = ref(false);
const current = ref(null);

const orderStatusMap = {
  0: { label: "待支付", type: "warning" },
  1: { label: "已支付", type: "success" },
  2: { label: "已取消", type: "info" },
  3: { label: "已退款", type: "danger" }
};

const gridOptions = reactive({
  border: true,
  showOverflow: true,
  height: 600,
  loading: false,
  rowConfig: { keyField: "id", isHover: true },
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
      {
        field: "userId",
        title: "用户ID",
        span: 6,
        itemRender: { name: "$input", props: { placeholder: "可选" } }
      },
      {
        field: "status",
        title: "状态",
        span: 6,
        itemRender: {
          name: "$select",
          options: [
            { label: "全部", value: "" },
            { label: "待支付", value: 0 },
            { label: "已支付", value: 1 },
            { label: "已取消", value: 2 },
            { label: "已退款", value: 3 }
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
          const q = {
            pageNum: page.currentPage,
            pageSize: page.pageSize,
            userId: form.userId ? Number(form.userId) : undefined,
            status: form.status !== "" && form.status !== undefined && form.status !== null ? form.status : undefined
          };
          const res = await getMemberOrderPageApi(q);
          const pageData = res.data || {};
          return {
            list: pageData.records || [],
            total: pageData.total || 0
          };
        } catch (e) {
          console.error(e);
          return { list: [], total: 0 };
        }
      }
    }
  },
  columns: [
    { type: "seq", title: "序号", width: 60 },
    { field: "orderNo", title: "订单号", minWidth: 180 },
    { field: "userId", title: "用户ID", width: 100 },
    { field: "planName", title: "套餐", minWidth: 120 },
    { field: "amount", title: "金额", width: 100, slots: { default: "amount" } },
    { field: "status", title: "状态", width: 100, slots: { default: "status" } },
    { field: "payTime", title: "支付时间", minWidth: 160 },
    { field: "createTime", title: "创建时间", minWidth: 160 },
    { title: "操作", width: 90, fixed: "right", slots: { default: "action" } }
  ]
});

const handleRefresh = () => {
  xGrid.value?.commitProxy("query");
};

const handleView = async row => {
  const res = await getMemberOrderDetailApi(row.id);
  current.value = res.data;
  detailVisible.value = true;
};
</script>
