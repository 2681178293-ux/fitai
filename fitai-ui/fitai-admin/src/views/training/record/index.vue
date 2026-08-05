<template>
  <div class="table-box p-5">
    <!-- KPI 卡片 -->
    <el-row :gutter="16" class="mb-4">
      <el-col :span="6">
        <el-card shadow="never" class="kpi-card">
          <div class="kpi-label">今日训练</div>
          <div class="kpi-value">{{ kpi.todayCount ?? 0 }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="never" class="kpi-card">
          <div class="kpi-label">本周训练</div>
          <div class="kpi-value">{{ kpi.weekCount ?? 0 }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="never" class="kpi-card">
          <div class="kpi-label">近7天平均时长(分)</div>
          <div class="kpi-value">{{ kpi.avgDurationMin ?? 0 }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="never" class="kpi-card">
          <div class="kpi-label">近7天趋势</div>
          <div class="kpi-trend">
            <span v-for="(t, i) in kpi.weekTrend || []" :key="i" class="trend-item">
              {{ t.label }}:{{ t.count }}
            </span>
            <span v-if="!(kpi.weekTrend || []).length" class="text-gray-400">-</span>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <vxe-grid ref="xGrid" v-bind="gridOptions">
      <template #toolbar_buttons>
        <el-button :icon="Refresh" @click="handleRefresh">刷新</el-button>
        <el-button type="primary" :icon="DataAnalysis" @click="openUserStats">用户统计</el-button>
      </template>

      <template #action="{ row }">
        <el-button type="primary" link :icon="View" @click="handleView(row)">详情</el-button>
      </template>
    </vxe-grid>

    <!-- 详情 -->
    <el-dialog v-model="detailVisible" title="训练记录详情" width="680px">
      <el-descriptions v-if="detail.id" :column="2" border>
        <el-descriptions-item label="记录ID">{{ detail.id }}</el-descriptions-item>
        <el-descriptions-item label="用户">{{ detail.userNickname || detail.userId }}</el-descriptions-item>
        <el-descriptions-item label="关联计划">{{ detail.planId || "自由训练" }}</el-descriptions-item>
        <el-descriptions-item label="训练日期">{{ detail.trainingDate }}</el-descriptions-item>
        <el-descriptions-item label="时长(分)">{{ detail.duration ?? "-" }}</el-descriptions-item>
        <el-descriptions-item label="消耗卡路里">{{ detail.calories ?? "-" }}</el-descriptions-item>
        <el-descriptions-item label="训练感受">{{ detail.feeling ? `${detail.feeling}/5` : "-" }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ detail.createTime }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ detail.remark || "-" }}</el-descriptions-item>
        <el-descriptions-item label="训练数据(exercisesData)" :span="2">
          <pre class="rec-json">{{ prettyJson(detail.exercisesData) }}</pre>
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <!-- 用户统计 -->
    <el-dialog v-model="userStatsVisible" title="按用户训练统计(Top20)" width="640px">
      <vxe-grid border :loading="userStatsLoading" :data="userStats" :columns="userStatColumns" max-height="480" />
    </el-dialog>
  </div>
</template>

<script setup name="trainingRecord">
import { ref, reactive, onMounted } from "vue";
import { Refresh, View, DataAnalysis } from "@element-plus/icons-vue";
import {
  getTrainRecordPageApi,
  getTrainRecordKpiApi,
  getTrainRecordUserStatsApi,
  getTrainRecordApi
} from "@/api/modules/biz-ext";

const xGrid = ref();
const kpi = ref({});
const detailVisible = ref(false);
const detail = ref({});
const userStatsVisible = ref(false);
const userStatsLoading = ref(false);
const userStats = ref([]);

const prettyJson = data => {
  if (data == null) return "-";
  try {
    return JSON.stringify(typeof data === "string" ? JSON.parse(data) : data, null, 2);
  } catch (e) {
    return String(data);
  }
};

const userStatColumns = [
  { type: "seq", width: 60 },
  { field: "userId", title: "用户ID", width: 100 },
  { field: "userNickname", title: "昵称", minWidth: 140 },
  { field: "recordCount", title: "训练次数", width: 100 },
  { field: "totalDuration", title: "累计时长(分)", width: 130 }
];

const gridOptions = reactive({
  border: true,
  showOverflow: true,
  height: 520,
  loading: false,
  rowConfig: { keyField: "id" },
  toolbarConfig: { slots: { buttons: "toolbar_buttons" }, refresh: true, zoom: false },
  proxyConfig: {
    form: true,
    response: { result: "list", total: "total" },
    ajax: {
      query: async ({ page, form }) => {
        const params = { pageNum: page.currentPage, pageSize: page.pageSize, ...form };
        try {
          const res = await getTrainRecordPageApi(params);
          const data = res.data || {};
          return { list: data.records || [], total: data.total || 0 };
        } catch (e) {
          return { list: [], total: 0 };
        }
      }
    }
  },
  formConfig: {
    items: [
      { field: "userId", title: "用户ID", span: 5, itemRender: { name: "$input", props: { placeholder: "用户ID" } } },
      { field: "username", title: "昵称", span: 5, itemRender: { name: "$input", props: { placeholder: "用户昵称" } } },
      { field: "planId", title: "计划ID", span: 5, itemRender: { name: "$input", props: { placeholder: "计划ID" } } },
      {
        field: "beginDate",
        title: "开始日期",
        span: 5,
        itemRender: { name: "$input", props: { type: "date", placeholder: "yyyy-MM-dd" } }
      },
      {
        field: "endDate",
        title: "结束日期",
        span: 5,
        itemRender: { name: "$input", props: { type: "date", placeholder: "yyyy-MM-dd" } }
      },
      {
        span: 4,
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
  pagerConfig: { enabled: true },
  columns: [
    { type: "seq", width: 60 },
    { field: "id", title: "ID", width: 90 },
    { field: "userNickname", title: "用户", minWidth: 120 },
    { field: "planId", title: "计划ID", width: 100 },
    { field: "trainingDate", title: "训练日期", width: 130 },
    { field: "duration", title: "时长(分)", width: 100 },
    { field: "calories", title: "卡路里", width: 100 },
    { field: "feeling", title: "感受", width: 80 },
    { field: "createTime", title: "创建时间", width: 170 },
    { title: "操作", width: 100, fixed: "right", slots: { default: "action" } }
  ]
});

const handleRefresh = () => xGrid.value?.commitProxy("query");

const loadKpi = async () => {
  try {
    const res = await getTrainRecordKpiApi();
    kpi.value = res.data || {};
  } catch (e) {
    kpi.value = {};
  }
};

const handleView = async row => {
  detail.value = {};
  detailVisible.value = true;
  try {
    const res = await getTrainRecordApi(row.id);
    detail.value = res.data || {};
  } catch (e) {
    detail.value = { ...row };
  }
};

const openUserStats = async () => {
  userStatsVisible.value = true;
  userStatsLoading.value = true;
  userStats.value = [];
  try {
    const res = await getTrainRecordUserStatsApi();
    userStats.value = res.data || [];
  } catch (e) {
    userStats.value = [];
  } finally {
    userStatsLoading.value = false;
  }
};

onMounted(loadKpi);
</script>

<style scoped>
.table-box {
  background: #fff;
  border-radius: 8px;
}
.kpi-card {
  border-radius: 8px;
}
.kpi-label {
  font-size: 13px;
  color: #909399;
}
.kpi-value {
  font-size: 26px;
  font-weight: 600;
  margin-top: 6px;
}
.kpi-trend {
  margin-top: 6px;
  font-size: 12px;
  line-height: 1.6;
}
.trend-item {
  margin-right: 8px;
  color: #606266;
}
.rec-json {
  max-height: 220px;
  overflow: auto;
  margin: 0;
  white-space: pre-wrap;
  word-break: break-all;
  font-size: 12px;
}
</style>
