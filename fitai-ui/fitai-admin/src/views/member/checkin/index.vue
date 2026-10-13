<template>
  <div class="table-box p-5">
    <!-- KPI 卡片 -->
    <div class="grid grid-cols-1 md:grid-cols-3 gap-4 mb-4">
      <div class="kpi-card">
        <div class="kpi-label">今日签到</div>
        <div class="kpi-value text-blue-500">{{ kpi.todayCount ?? 0 }}</div>
      </div>
      <div class="kpi-card">
        <div class="kpi-label">本月签到</div>
        <div class="kpi-value text-green-500">{{ kpi.monthCount ?? 0 }}</div>
      </div>
      <div class="kpi-card">
        <div class="kpi-label">最长连续天数</div>
        <div class="kpi-value text-orange-500">{{ kpi.maxContinuousDays ?? 0 }}</div>
      </div>
    </div>

    <el-tabs v-model="activeTab" @tab-change="handleTabChange">
      <el-tab-pane label="签到记录" name="checkin">
        <vxe-grid ref="checkinGrid" v-bind="checkinGridOptions">
          <template #toolbar_buttons>
            <el-button type="danger" :icon="Delete" @click="handleBatchDelete">批量删除</el-button>
            <el-button :icon="Refresh" @click="handleRefreshCheckin">刷新</el-button>
          </template>

          <template #continuousDays="{ row }">
            <el-tag type="warning">{{ row.continuousDays ?? 0 }} 天</el-tag>
          </template>

          <template #pointsEarned="{ row }">
            <span class="text-green-500 font-medium">+{{ row.pointsEarned ?? 0 }}</span>
          </template>

          <template #action="{ row }">
            <el-button type="danger" link :icon="Delete" @click="handleDelete(row)">删除</el-button>
          </template>
        </vxe-grid>
      </el-tab-pane>

      <el-tab-pane label="成长值流水" name="growth">
        <vxe-grid ref="growthGrid" v-bind="growthGridOptions">
          <template #toolbar_buttons>
            <el-button :icon="Refresh" @click="handleRefreshGrowth">刷新</el-button>
          </template>

          <template #changeValue="{ row }">
            <span :class="Number(row.changeValue) >= 0 ? 'text-green-500' : 'text-red-500'" class="font-medium">
              {{ Number(row.changeValue) >= 0 ? "+" : "" }}{{ row.changeValue ?? 0 }}
            </span>
          </template>
        </vxe-grid>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup name="memberCheckin">
import { ref, reactive, onMounted, nextTick } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { Refresh, Delete } from "@element-plus/icons-vue";
import dayjs from "dayjs";
import {
  getCheckinPageApi,
  getCheckinKpiApi,
  getGrowthLogPageApi,
  deleteCheckinApi
} from "@/api/modules/member";

const checkinGrid = ref();
const growthGrid = ref();
const activeTab = ref("checkin");

const kpi = reactive({
  todayCount: 0,
  monthCount: 0,
  maxContinuousDays: 0
});

const formatDate = val => (val ? dayjs(val).format("YYYY-MM-DD") : undefined);

const checkinGridOptions = reactive({
  border: true,
  showOverflow: true,
  height: 520,
  loading: false,
  rowConfig: { keyField: "checkinId", isHover: true },
  columnConfig: { resizable: true },
  checkboxConfig: { highlight: true },
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
        field: "customerId",
        title: "用户ID",
        span: 6,
        itemRender: { name: "$input", props: { placeholder: "customerId", clearable: true } }
      },
      {
        field: "startDate",
        title: "开始日期",
        span: 6,
        itemRender: { name: "$input", props: { type: "date", placeholder: "开始日期", clearable: true } }
      },
      {
        field: "endDate",
        title: "结束日期",
        span: 6,
        itemRender: { name: "$input", props: { type: "date", placeholder: "结束日期", clearable: true } }
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
          const res = await getCheckinPageApi({
            pageNum: page.currentPage,
            pageSize: page.pageSize,
            customerId: form.customerId ? Number(form.customerId) : undefined,
            startDate: formatDate(form.startDate),
            endDate: formatDate(form.endDate)
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
    { type: "checkbox", width: 50 },
    { type: "seq", title: "序号", width: 60 },
    { field: "customerId", title: "用户ID", width: 100 },
    { field: "nickName", title: "昵称", minWidth: 130 },
    { field: "checkinDate", title: "签到日期", width: 140 },
    { field: "continuousDays", title: "连续天数", width: 110, slots: { default: "continuousDays" } },
    { field: "pointsEarned", title: "获得积分", width: 110, slots: { default: "pointsEarned" } },
    { field: "createTime", title: "签到时间", minWidth: 170 },
    { title: "操作", width: 100, fixed: "right", slots: { default: "action" } }
  ]
});

const growthGridOptions = reactive({
  border: true,
  showOverflow: true,
  height: 520,
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
      { field: "userId", title: "用户ID", span: 8, itemRender: { name: "$input", props: { placeholder: "可选" } } },
      {
        field: "bizType",
        title: "业务类型",
        span: 8,
        itemRender: { name: "$input", props: { placeholder: "如：checkin / course" } }
      },
      {
        span: 8,
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
          const res = await getGrowthLogPageApi({
            pageNum: page.currentPage,
            pageSize: page.pageSize,
            userId: form.userId ? Number(form.userId) : undefined,
            bizType: form.bizType || undefined
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
    { field: "userId", title: "用户ID", width: 100 },
    { field: "userNickname", title: "昵称", minWidth: 130 },
    { field: "changeValue", title: "成长值变动", width: 130, slots: { default: "changeValue" } },
    { field: "balanceAfter", title: "变动后成长值", width: 130 },
    { field: "bizType", title: "业务类型", width: 130 },
    { field: "bizId", title: "业务ID", width: 110 },
    { field: "remark", title: "备注", minWidth: 180 },
    { field: "createTime", title: "时间", minWidth: 170 }
  ]
});

const loadKpi = async () => {
  try {
    const res = await getCheckinKpiApi();
    const data = res?.data || {};
    kpi.todayCount = data.todayCount ?? 0;
    kpi.monthCount = data.monthCount ?? 0;
    kpi.maxContinuousDays = data.maxContinuousDays ?? 0;
  } catch (e) {
    console.error(e);
  }
};

const handleRefreshCheckin = () => {
  checkinGrid.value?.commitProxy("query");
  loadKpi();
};

const handleRefreshGrowth = () => {
  growthGrid.value?.commitProxy("query");
};

const handleTabChange = name => {
  nextTick(() => {
    if (name === "growth") {
      growthGrid.value?.commitProxy("query");
    } else {
      checkinGrid.value?.commitProxy("query");
    }
  });
};

const handleDelete = row => {
  ElMessageBox.confirm(`确定删除该条签到记录吗？`, "提示", { type: "warning" })
    .then(async () => {
      await deleteCheckinApi(row.checkinId);
      ElMessage.success("已删除");
      handleRefreshCheckin();
    })
    .catch(() => {});
};

const handleBatchDelete = () => {
  const records = checkinGrid.value?.getCheckboxRecords() || [];
  if (records.length === 0) {
    ElMessage.warning("请选择要删除的数据");
    return;
  }
  const ids = records.map(r => r.checkinId).join(",");
  ElMessageBox.confirm(`确定删除选中的 ${records.length} 条签到记录吗？`, "提示", { type: "warning" })
    .then(async () => {
      await deleteCheckinApi(ids);
      ElMessage.success("已删除");
      handleRefreshCheckin();
    })
    .catch(() => {});
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
