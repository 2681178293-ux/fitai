<template>
  <div class="table-box p-5">
    <vxe-grid ref="xGrid" v-bind="gridOptions">
      <template #toolbar_buttons>
        <el-button :icon="Refresh" @click="handleRefresh">刷新</el-button>
        <el-button type="primary" :icon="DataAnalysis" @click="openStats">完成率统计</el-button>
      </template>

      <template #completed="{ row }">
        <el-tag v-if="row.isCompleted" type="success" size="small">已完成</el-tag>
        <el-tag v-else type="info" size="small">学习中</el-tag>
      </template>

      <template #progress="{ row }">
        {{ formatSeconds(row.progressSeconds) }}
      </template>

      <template #action="{ row }">
        <el-button type="primary" link :icon="View" @click="viewStudents(row)">学员列表</el-button>
        <el-button type="warning" link :icon="RefreshLeft" @click="handleReset(row)">重置进度</el-button>
      </template>
    </vxe-grid>

    <!-- 完成率统计 -->
    <el-dialog v-model="statsVisible" title="完成率统计" width="480px">
      <el-form label-width="90px" @submit.prevent>
        <el-form-item label="课程ID">
          <el-input v-model="statCourseId" placeholder="留空统计全部课程" clearable>
            <template #append>
              <el-button :icon="Search" @click="loadStats" />
            </template>
          </el-input>
        </el-form-item>
      </el-form>
      <el-descriptions v-if="stats" :column="1" border v-loading="statsLoading">
        <el-descriptions-item label="课程">
          {{ stats.courseTitle || (stats.courseId ? `#${stats.courseId}` : "全部课程") }}
        </el-descriptions-item>
        <el-descriptions-item label="学习人数">{{ stats.totalLearners }}</el-descriptions-item>
        <el-descriptions-item label="已完成人数">{{ stats.completedCount }}</el-descriptions-item>
        <el-descriptions-item label="完成率">{{ stats.completionRate ?? 0 }}%</el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <!-- 课程学员列表 -->
    <el-dialog v-model="studentsVisible" title="课程学员列表" width="720px">
      <vxe-grid
        border
        :loading="studentsLoading"
        :data="students"
        :columns="studentColumns"
        max-height="480"
      >
        <template #s_completed="{ row }">
          <el-tag v-if="row.isCompleted" type="success" size="small">已完成</el-tag>
          <el-tag v-else type="info" size="small">学习中</el-tag>
        </template>
      </vxe-grid>
    </el-dialog>
  </div>
</template>

<script setup name="courseProgress">
import { ref, reactive } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { Refresh, View, RefreshLeft, DataAnalysis, Search } from "@element-plus/icons-vue";
import {
  getLearnProgressPageApi,
  getLearnProgressStatsApi,
  getCourseStudentsApi,
  resetLearnProgressApi
} from "@/api/modules/biz-ext";

const xGrid = ref();

const statsVisible = ref(false);
const statsLoading = ref(false);
const stats = ref(null);
const statCourseId = ref("");

const studentsVisible = ref(false);
const studentsLoading = ref(false);
const students = ref([]);

const formatSeconds = s => {
  const sec = Number(s || 0);
  const m = Math.floor(sec / 60);
  const r = sec % 60;
  return `${m}分${r}秒`;
};

const studentColumns = [
  { type: "seq", width: 60 },
  { field: "userId", title: "用户ID", width: 100 },
  { field: "userNickname", title: "昵称", minWidth: 120 },
  { field: "progressSeconds", title: "进度(秒)", width: 100 },
  { field: "isCompleted", title: "是否完成", width: 100, slots: { default: "s_completed" } },
  { field: "completeTime", title: "完成时间", width: 170 }
];

const gridOptions = reactive({
  border: true,
  showOverflow: true,
  height: 600,
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
          const res = await getLearnProgressPageApi(params);
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
      { field: "courseId", title: "课程ID", span: 5, itemRender: { name: "$input", props: { placeholder: "课程ID" } } },
      { field: "userId", title: "用户ID", span: 5, itemRender: { name: "$input", props: { placeholder: "用户ID" } } },
      { field: "username", title: "昵称", span: 5, itemRender: { name: "$input", props: { placeholder: "用户昵称" } } },
      {
        field: "isCompleted",
        title: "是否完成",
        span: 5,
        itemRender: {
          name: "$select",
          options: [
            { label: "全部", value: "" },
            { label: "已完成", value: true },
            { label: "学习中", value: false }
          ],
          props: { placeholder: "请选择" }
        }
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
    { field: "userNickname", title: "学员", minWidth: 120 },
    { field: "userId", title: "用户ID", width: 100 },
    { field: "courseTitle", title: "课程", minWidth: 160 },
    { field: "progressSeconds", title: "进度", width: 120, slots: { default: "progress" } },
    { field: "isCompleted", title: "完成", width: 90, slots: { default: "completed" } },
    { field: "completeTime", title: "完成时间", width: 170 },
    { field: "createTime", title: "创建时间", width: 170 },
    { title: "操作", width: 190, fixed: "right", slots: { default: "action" } }
  ]
});

const handleRefresh = () => xGrid.value?.commitProxy("query");

const openStats = () => {
  statsVisible.value = true;
  loadStats();
};

const loadStats = async () => {
  statsLoading.value = true;
  try {
    const res = await getLearnProgressStatsApi(statCourseId.value || undefined);
    stats.value = res.data || {};
  } catch (e) {
    stats.value = null;
  } finally {
    statsLoading.value = false;
  }
};

const viewStudents = async row => {
  studentsVisible.value = true;
  studentsLoading.value = true;
  students.value = [];
  try {
    const res = await getCourseStudentsApi(row.courseId);
    students.value = res.data || [];
  } catch (e) {
    students.value = [];
  } finally {
    studentsLoading.value = false;
  }
};

const handleReset = row => {
  ElMessageBox.confirm("确定重置该学员的学习进度？进度将归零且标记为未完成。", "提示", {
    type: "warning"
  }).then(async () => {
    try {
      await resetLearnProgressApi(row.id);
      ElMessage.success("重置成功");
      handleRefresh();
    } catch (e) {
      ElMessage.error("重置失败");
    }
  });
};
</script>

<style scoped>
.table-box {
  background: #fff;
  border-radius: 8px;
}
</style>
