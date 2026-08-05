<template>
  <div class="table-box p-5">
    <!-- KPI 卡片 -->
    <el-row :gutter="16" class="mb-5">
      <el-col :xs="24" :sm="12" :md="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value">{{ kpi.recordTotal ?? 0 }}</div>
          <div class="stat-label">记录总数</div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value">{{ kpi.userCount ?? 0 }}</div>
          <div class="stat-label">覆盖用户数</div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value">{{ kpi.monthNewCount ?? 0 }}</div>
          <div class="stat-label">本月新增</div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value">{{ kpi.avgBodyFat != null ? Number(kpi.avgBodyFat).toFixed(1) + "%" : "-" }}</div>
          <div class="stat-label">平均体脂</div>
        </el-card>
      </el-col>
    </el-row>

    <vxe-grid ref="xGrid" v-bind="gridOptions">
      <template #toolbar_buttons>
        <el-button type="primary" :icon="Plus" @click="handleAdd">新增记录</el-button>
        <el-button type="danger" :icon="Delete" @click="handleBatchDelete">批量删除</el-button>
        <el-button :icon="Refresh" @click="handleRefresh">刷新</el-button>
      </template>

      <!-- 日期区间查询 -->
      <template #form_dateRange="{ data }">
        <el-date-picker
          v-model="data.dateRange"
          type="daterange"
          value-format="YYYY-MM-DD"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          style="width: 100%"
        />
      </template>

      <template #bodyFat="{ row }">
        <span>{{ row.bodyFat != null ? Number(row.bodyFat).toFixed(1) + "%" : "-" }}</span>
      </template>

      <template #recordDate="{ row }">
        <span>{{ formatDate(row.recordDate) }}</span>
      </template>

      <template #action="{ row }">
        <el-button type="primary" link :icon="TrendCharts" @click="handleTrend(row)">趋势</el-button>
        <el-button type="primary" link :icon="Edit" @click="handleEdit(row)">编辑</el-button>
        <el-button type="danger" link :icon="Delete" @click="handleDelete(row)">删除</el-button>
      </template>
    </vxe-grid>

    <!-- 新增/编辑记录 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑身体数据' : '新增身体数据'" width="680px" destroy-on-close>
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="110px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="用户ID" prop="userId">
              <el-input-number v-model="formData.userId" :min="1" :controls="false" style="width: 100%" :disabled="isEdit" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="记录日期" prop="recordDate">
              <el-date-picker v-model="formData.recordDate" type="date" value-format="YYYY-MM-DD" placeholder="选择日期" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="体重(kg)" prop="weight">
              <el-input-number v-model="formData.weight" :min="0" :max="500" :precision="1" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="体脂率(%)">
              <el-input-number v-model="formData.bodyFat" :min="0" :max="100" :precision="1" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="胸围(cm)">
              <el-input-number v-model="formData.chest" :min="0" :max="300" :precision="1" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="腰围(cm)">
              <el-input-number v-model="formData.waist" :min="0" :max="300" :precision="1" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="臀围(cm)">
              <el-input-number v-model="formData.hip" :min="0" :max="300" :precision="1" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="臂围(cm)">
              <el-input-number v-model="formData.arm" :min="0" :max="200" :precision="1" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="腿围(cm)">
              <el-input-number v-model="formData.thigh" :min="0" :max="200" :precision="1" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 趋势图 -->
    <el-dialog
      v-model="trendVisible"
      :title="`体重 / 体脂趋势 - ${trendUser.userNickname || trendUser.userId || ''}`"
      width="900px"
      destroy-on-close
      @opened="initTrendChart"
      @closed="disposeTrendChart"
    >
      <div v-loading="trendLoading">
        <el-empty v-if="!trendLoading && trendData.length === 0" description="暂无趋势数据" />
        <div v-show="trendData.length > 0" ref="trendChartRef" class="trend-chart"></div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup name="userBody">
import { ref, reactive, onMounted, onUnmounted } from "vue";
import dayjs from "dayjs";
import * as echarts from "echarts";
import { ElMessage, ElMessageBox } from "element-plus";
import { Plus, Refresh, Edit, Delete, TrendCharts } from "@element-plus/icons-vue";
import {
  getBodyRecordPageApi,
  getBodyRecordKpiApi,
  getBodyRecordTrendApi,
  createBodyRecordApi,
  updateBodyRecordApi,
  deleteBodyRecordApi
} from "@/api/modules/user";

const xGrid = ref();
const formRef = ref();
const trendChartRef = ref();
const dialogVisible = ref(false);
const trendVisible = ref(false);
const isEdit = ref(false);
const submitLoading = ref(false);
const trendLoading = ref(false);
const trendData = ref([]);
const trendUser = ref({});
const kpi = ref({ recordTotal: 0, userCount: 0, monthNewCount: 0, avgBodyFat: null });

let trendChart = null;

const formData = reactive({
  id: null,
  userId: null,
  weight: null,
  bodyFat: null,
  chest: null,
  waist: null,
  hip: null,
  arm: null,
  thigh: null,
  recordDate: dayjs().format("YYYY-MM-DD")
});

const formRules = {
  userId: [{ required: true, message: "请输入用户ID", trigger: "blur" }],
  weight: [{ required: true, message: "请输入体重", trigger: "blur" }],
  recordDate: [{ required: true, message: "请选择记录日期", trigger: "change" }]
};

const formatDate = val => (val ? dayjs(val).format("YYYY-MM-DD") : "-");

const gridOptions = reactive({
  border: true,
  showOverflow: true,
  height: 560,
  loading: false,
  rowConfig: { keyField: "id", isHover: true },
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
    data: { userId: "", dateRange: [] },
    items: [
      { field: "userId", title: "用户ID", span: 6, itemRender: { name: "$input", props: { placeholder: "可选" } } },
      { field: "dateRange", title: "日期区间", span: 10, slots: { default: "form_dateRange" } },
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
    pageSizes: [10, 20, 50, 100]
  },
  proxyConfig: {
    seq: true,
    form: true,
    response: { result: "list", total: "total" },
    ajax: {
      query: async ({ page, form }) => {
        try {
          const range = Array.isArray(form.dateRange) ? form.dateRange : [];
          const params = {
            pageNum: page.currentPage,
            pageSize: page.pageSize,
            userId: form.userId ? Number(form.userId) : undefined,
            startDate: range[0] || undefined,
            endDate: range[1] || undefined
          };
          const res = await getBodyRecordPageApi(params);
          return { list: res.data?.records || [], total: res.data?.total || 0 };
        } catch (e) {
          console.error("获取身体数据失败:", e);
          return { list: [], total: 0 };
        }
      }
    }
  },
  columns: [
    { type: "checkbox", width: 50 },
    { type: "seq", title: "序号", width: 60 },
    { field: "userId", title: "用户ID", width: 100 },
    { field: "userNickname", title: "用户昵称", minWidth: 130 },
    { field: "weight", title: "体重(kg)", width: 100 },
    { field: "bodyFat", title: "体脂率", width: 100, slots: { default: "bodyFat" } },
    { field: "chest", title: "胸围(cm)", width: 100 },
    { field: "waist", title: "腰围(cm)", width: 100 },
    { field: "hip", title: "臀围(cm)", width: 100 },
    { field: "arm", title: "臂围(cm)", width: 100 },
    { field: "thigh", title: "腿围(cm)", width: 100 },
    { field: "recordDate", title: "记录日期", width: 120, slots: { default: "recordDate" } },
    { title: "操作", width: 210, fixed: "right", slots: { default: "action" } }
  ]
});

const loadKpi = async () => {
  try {
    const res = await getBodyRecordKpiApi();
    kpi.value = res.data || {};
  } catch (e) {
    console.error("获取身体数据KPI失败:", e);
  }
};

const handleRefresh = () => {
  xGrid.value?.commitProxy("query");
  loadKpi();
};

const handleAdd = () => {
  isEdit.value = false;
  Object.assign(formData, {
    id: null,
    userId: null,
    weight: null,
    bodyFat: null,
    chest: null,
    waist: null,
    hip: null,
    arm: null,
    thigh: null,
    recordDate: dayjs().format("YYYY-MM-DD")
  });
  dialogVisible.value = true;
};

const handleEdit = row => {
  isEdit.value = true;
  Object.assign(formData, {
    id: row.id,
    userId: row.userId ?? null,
    weight: row.weight != null ? Number(row.weight) : null,
    bodyFat: row.bodyFat != null ? Number(row.bodyFat) : null,
    chest: row.chest != null ? Number(row.chest) : null,
    waist: row.waist != null ? Number(row.waist) : null,
    hip: row.hip != null ? Number(row.hip) : null,
    arm: row.arm != null ? Number(row.arm) : null,
    thigh: row.thigh != null ? Number(row.thigh) : null,
    recordDate: row.recordDate ? dayjs(row.recordDate).format("YYYY-MM-DD") : ""
  });
  dialogVisible.value = true;
};

const handleSubmit = async () => {
  await formRef.value.validate();
  try {
    submitLoading.value = true;
    const payload = {
      userId: formData.userId,
      weight: formData.weight,
      bodyFat: formData.bodyFat,
      chest: formData.chest,
      waist: formData.waist,
      hip: formData.hip,
      arm: formData.arm,
      thigh: formData.thigh,
      recordDate: formData.recordDate
    };
    if (isEdit.value) {
      await updateBodyRecordApi(formData.id, payload);
      ElMessage.success("更新成功");
    } else {
      await createBodyRecordApi(payload);
      ElMessage.success("创建成功");
    }
    dialogVisible.value = false;
    handleRefresh();
  } finally {
    submitLoading.value = false;
  }
};

const handleDelete = row => {
  ElMessageBox.confirm(`确定删除该条身体数据（${formatDate(row.recordDate)}）吗？`, "提示", { type: "warning" })
    .then(async () => {
      await deleteBodyRecordApi(row.id);
      ElMessage.success("删除成功");
      handleRefresh();
    })
    .catch(() => {});
};

const handleBatchDelete = () => {
  const records = xGrid.value?.getCheckboxRecords() || [];
  if (records.length === 0) {
    ElMessage.warning("请选择要删除的数据");
    return;
  }
  ElMessageBox.confirm(`确定删除选中的 ${records.length} 条记录吗？`, "提示", { type: "warning" })
    .then(async () => {
      await deleteBodyRecordApi(records.map(r => r.id).join(","));
      ElMessage.success("批量删除成功");
      handleRefresh();
    })
    .catch(() => {});
};

const handleTrend = async row => {
  trendUser.value = { userId: row.userId, userNickname: row.userNickname };
  trendData.value = [];
  trendVisible.value = true;
  try {
    trendLoading.value = true;
    const res = await getBodyRecordTrendApi(row.userId, 30);
    trendData.value = Array.isArray(res.data) ? res.data : [];
  } catch (e) {
    console.error("获取趋势数据失败:", e);
    trendData.value = [];
  } finally {
    trendLoading.value = false;
    renderTrendChart();
  }
};

const renderTrendChart = () => {
  if (!trendChart || trendData.value.length === 0) return;
  const list = [...trendData.value].sort((a, b) => String(a.recordDate).localeCompare(String(b.recordDate)));
  const option = {
    tooltip: { trigger: "axis" },
    legend: { data: ["体重(kg)", "体脂率(%)"], bottom: 0 },
    grid: { left: "3%", right: "4%", bottom: "12%", top: "12%", containLabel: true },
    xAxis: {
      type: "category",
      boundaryGap: false,
      data: list.map(item => formatDate(item.recordDate))
    },
    yAxis: [
      { type: "value", name: "体重(kg)", axisLabel: { formatter: "{value}" } },
      { type: "value", name: "体脂率(%)", axisLabel: { formatter: "{value}" } }
    ],
    series: [
      {
        name: "体重(kg)",
        type: "line",
        yAxisIndex: 0,
        smooth: true,
        symbolSize: 6,
        itemStyle: { color: "#409EFF" },
        data: list.map(item => (item.weight != null ? Number(item.weight) : null))
      },
      {
        name: "体脂率(%)",
        type: "line",
        yAxisIndex: 1,
        smooth: true,
        symbolSize: 6,
        itemStyle: { color: "#E6A23C" },
        data: list.map(item => (item.bodyFat != null ? Number(item.bodyFat) : null))
      }
    ]
  };
  trendChart.setOption(option, true);
};

const initTrendChart = () => {
  if (!trendChartRef.value) return;
  if (!trendChart) trendChart = echarts.init(trendChartRef.value);
  trendChart.resize();
  renderTrendChart();
};

const disposeTrendChart = () => {
  if (trendChart) {
    trendChart.dispose();
    trendChart = null;
  }
};

const handleResize = () => {
  trendChart?.resize();
};

onMounted(() => {
  loadKpi();
  window.addEventListener("resize", handleResize);
});

onUnmounted(() => {
  window.removeEventListener("resize", handleResize);
  disposeTrendChart();
});
</script>

<style scoped>
.table-box {
  background: #fff;
  border-radius: 8px;
}

.stat-card {
  text-align: center;
}

.stat-value {
  font-size: 26px;
  font-weight: 700;
  color: #303133;
}

.stat-label {
  margin-top: 6px;
  font-size: 13px;
  color: #909399;
}

.trend-chart {
  width: 100%;
  height: 420px;
}
</style>
