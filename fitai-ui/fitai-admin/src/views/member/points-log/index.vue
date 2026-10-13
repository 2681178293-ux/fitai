<template>
  <div class="table-box p-5">
    <div class="grid grid-cols-1 md:grid-cols-3 gap-4 mb-4">
      <div class="kpi-card">
        <div class="kpi-label">今日发放</div>
        <div class="kpi-value text-green-500">{{ kpi.todayEarn ?? 0 }}</div>
      </div>
      <div class="kpi-card">
        <div class="kpi-label">今日消耗</div>
        <div class="kpi-value text-red-500">{{ kpi.todaySpend ?? 0 }}</div>
      </div>
      <div class="kpi-card">
        <div class="kpi-label">累计发放</div>
        <div class="kpi-value text-blue-500">{{ kpi.totalEarn ?? 0 }}</div>
      </div>
    </div>

    <vxe-grid ref="xGrid" v-bind="gridOptions">
      <template #toolbar_buttons>
        <el-button type="primary" :icon="Edit" @click="openAdjust">手动调整</el-button>
        <el-button type="success" :icon="Download" :loading="exportLoading" @click="handleExport">导出CSV</el-button>
        <el-button :icon="Refresh" @click="handleRefresh">刷新</el-button>
      </template>

      <template #changePoints="{ row }">
        <span :class="Number(row.changePoints) >= 0 ? 'text-green-500' : 'text-red-500'" class="font-medium">
          {{ Number(row.changePoints) >= 0 ? "+" : "" }}{{ row.changePoints ?? 0 }}
        </span>
      </template>

      <template #bizType="{ row }">
        <el-tag :type="typeMap[row.bizType]?.type || 'info'">{{ typeMap[row.bizType]?.label ?? row.bizType ?? "-" }}</el-tag>
      </template>

      <template #createTime="{ row }">
        <span>{{ row.createTime ? dayjs(row.createTime).format("YYYY-MM-DD HH:mm:ss") : "-" }}</span>
      </template>
    </vxe-grid>

    <el-dialog v-model="adjustVisible" title="手动调整积分" width="520px" destroy-on-close>
      <el-form ref="adjustFormRef" :model="adjustForm" :rules="adjustRules" label-width="110px">
        <el-form-item label="用户ID" prop="customerId">
          <el-input v-model="adjustForm.customerId" placeholder="请输入用户ID" />
        </el-form-item>
        <el-form-item label="积分变动值" prop="changePoints">
          <el-input-number v-model="adjustForm.changePoints" :min="-100000" :max="100000" style="width: 100%" />
          <div class="text-xs text-gray-400 mt-1">正数为发放，负数为扣减</div>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="adjustForm.remark" type="textarea" :rows="3" placeholder="调整原因" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="adjustVisible = false">取消</el-button>
        <el-button type="primary" :loading="adjustLoading" @click="handleAdjustSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="memberPointsLog">
import { ref, reactive, onMounted } from "vue";
import { ElMessage } from "element-plus";
import { Refresh, Edit, Download } from "@element-plus/icons-vue";
import dayjs from "dayjs";
import {
  getPointsLogPageApi,
  getPointsLogKpiApi,
  adjustPointsApi,
  exportPointsLogApi
} from "@/api/modules/member";

const xGrid = ref();
const adjustFormRef = ref();
const adjustVisible = ref(false);
const adjustLoading = ref(false);
const exportLoading = ref(false);

const kpi = reactive({
  todayEarn: 0,
  todaySpend: 0,
  totalEarn: 0
});

const typeMap = {
  sign: { label: "签到", type: "success" },
  checkin: { label: "签到", type: "success" },
  train: { label: "训练", type: "primary" },
  order: { label: "订单", type: "danger" },
  review: { label: "评价", type: "warning" },
  adjust: { label: "调整", type: "info" }
};

const lastQuery = ref({});

const adjustForm = reactive({
  customerId: "",
  changePoints: 0,
  remark: ""
});

const adjustRules = {
  customerId: [{ required: true, message: "请输入用户ID", trigger: "blur" }],
  changePoints: [{ required: true, message: "请输入积分变动值", trigger: "blur" }],
  remark: [{ required: true, message: "请输入备注", trigger: "blur" }]
};

const formatTime = (val, suffix) => (val ? `${dayjs(val).format("YYYY-MM-DD")} ${suffix}` : undefined);

const buildQuery = form => ({
  customerId: form.customerId ? Number(form.customerId) : undefined,
  bizType: form.bizType || undefined,
  registerSource: form.registerSource || undefined,
  pointsStartTime: formatTime(form.pointsStartTime, "00:00:00"),
  pointsEndTime: formatTime(form.pointsEndTime, "23:59:59")
});

const gridOptions = reactive({
  border: true,
  showOverflow: true,
  height: 560,
  loading: false,
  rowConfig: { keyField: "logId", isHover: true },
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
        field: "bizType",
        title: "类型",
        span: 6,
        itemRender: {
          name: "$select",
          options: [
            { label: "全部", value: "" },
            { label: "签到", value: "sign" },
            { label: "训练", value: "train" },
            { label: "订单", value: "order" },
            { label: "评价", value: "review" }
          ],
          props: { placeholder: "全部" }
        }
      },
      { field: "registerSource", title: "来源", span: 6, itemRender: { name: "$input", props: { placeholder: "可选" } } },
      {
        field: "pointsStartTime",
        title: "开始时间",
        span: 6,
        itemRender: { name: "$input", props: { type: "date", placeholder: "开始日期", clearable: true } }
      },
      {
        field: "pointsEndTime",
        title: "结束时间",
        span: 6,
        itemRender: { name: "$input", props: { type: "date", placeholder: "结束日期", clearable: true } }
      },
      {
        span: 18,
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
          const q = buildQuery(form);
          lastQuery.value = q;
          const res = await getPointsLogPageApi({
            pageNum: page.currentPage,
            pageSize: page.pageSize,
            ...q
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
    { field: "customerId", title: "用户ID", width: 100 },
    { field: "nickname", title: "用户昵称", minWidth: 130 },
    { field: "changePoints", title: "变动值", width: 110, slots: { default: "changePoints" } },
    { field: "balanceAfter", title: "变动后余额", width: 120 },
    { field: "bizType", title: "类型", width: 100, slots: { default: "bizType" } },
    { field: "registerSource", title: "来源", width: 130 },
    { field: "bizId", title: "业务ID", width: 110 },
    { field: "remark", title: "备注", minWidth: 180 },
    { field: "createTime", title: "时间", minWidth: 170, slots: { default: "createTime" } }
  ]
});

const handleRefresh = () => {
  xGrid.value?.commitProxy("query");
  loadKpi();
};

const loadKpi = async () => {
  try {
    const res = await getPointsLogKpiApi();
    const data = res?.data || {};
    kpi.todayEarn = data.todayEarn ?? 0;
    kpi.todaySpend = data.todaySpend ?? 0;
    kpi.totalEarn = data.totalEarn ?? 0;
  } catch (e) {
    console.error(e);
  }
};

const openAdjust = () => {
  Object.assign(adjustForm, { customerId: "", changePoints: 0, remark: "" });
  adjustVisible.value = true;
};

const handleAdjustSubmit = async () => {
  try {
    await adjustFormRef.value.validate();
    if (!adjustForm.changePoints) {
      ElMessage.warning("积分变动值不能为 0");
      return;
    }
    adjustLoading.value = true;
    await adjustPointsApi({
      customerId: Number(adjustForm.customerId),
      changePoints: adjustForm.changePoints,
      remark: adjustForm.remark
    });
    ElMessage.success("调整成功");
    adjustVisible.value = false;
    handleRefresh();
  } finally {
    adjustLoading.value = false;
  }
};

const handleExport = async () => {
  exportLoading.value = true;
  let url = "";
  let link = null;
  try {
    const blob = await exportPointsLogApi({ ...lastQuery.value });
    const file = blob instanceof Blob ? blob : new Blob([blob], { type: "text/csv;charset=utf-8" });
    url = URL.createObjectURL(file);
    link = document.createElement("a");
    link.href = url;
    link.download = `积分流水_${dayjs().format("YYYYMMDDHHmmss")}.csv`;
    document.body.appendChild(link);
    link.click();
    ElMessage.success("导出成功");
  } catch (e) {
    console.error(e);
    ElMessage.error("导出失败");
  } finally {
    if (link) document.body.removeChild(link);
    if (url) URL.revokeObjectURL(url);
    exportLoading.value = false;
  }
};

onMounted(() => {
  loadKpi();
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
