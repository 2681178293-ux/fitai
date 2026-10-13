<template>
  <div class="table-box p-5">
    <vxe-grid ref="xGrid" v-bind="gridOptions">
      <template #toolbar_buttons>
        <el-button :icon="Refresh" @click="handleRefresh">刷新</el-button>
        <el-button type="primary" :icon="DataAnalysis" @click="openStats">完成率统计</el-button>
      </template>

      <!-- 学员：头像 + 昵称 + 手机号 -->
      <template #student="{ row }">
        <div class="student-cell">
          <el-avatar :size="28" :src="row.avatar || undefined">
            {{ (row.userNickname || "?").charAt(0) }}
          </el-avatar>
          <div class="student-info">
            <div class="student-name">{{ row.userNickname || "-" }}</div>
            <div v-if="row.mobile" class="student-mobile">{{ row.mobile }}</div>
          </div>
        </div>
      </template>

      <!-- 进度：进度条 + 百分比 + 已学/总时长 -->
      <template #progress="{ row }">
        <div class="progress-cell">
          <div class="progress-top">
            <el-progress
              :percentage="row.progressPct"
              :stroke-width="10"
              :show-text="false"
              color="var(--el-color-primary)"
              class="progress-bar"
            />
            <span class="progress-pct">{{ row.progressPct }}%</span>
          </div>
          <div class="progress-time">
            {{ formatSeconds(row.learnedSec) }} / {{ formatSeconds(row.totalSec) }}
          </div>
        </div>
      </template>

      <template #completed="{ row }">
        <el-tag v-if="row.isCompleted" type="success" size="small">已完成</el-tag>
        <el-tag v-else type="info" size="small">学习中</el-tag>
      </template>

      <template #action="{ row }">
        <el-button type="primary" link :icon="View" @click="viewStudents(row)">学员列表</el-button>
      </template>
    </vxe-grid>

    <!-- 完成率统计 -->
    <el-dialog v-model="statsVisible" title="完成率统计" width="480px" destroy-on-close>
      <el-form label-width="90px" @submit.prevent>
        <el-form-item label="课程" required>
          <el-select
            v-model="statCourseId"
            filterable
            clearable
            placeholder="请选择课程"
            style="width: 100%"
          >
            <el-option
              v-for="c in courseOptions"
              :key="c.courseId"
              :label="c.courseTitle"
              :value="String(c.courseId)"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" :loading="statsLoading" @click="loadStats">查询</el-button>
        </el-form-item>
      </el-form>
      <el-descriptions v-if="stats" :column="1" border v-loading="statsLoading">
        <el-descriptions-item label="课程">
          {{ stats.courseTitle || (stats.courseId ? `#${stats.courseId}` : "-") }}
        </el-descriptions-item>
        <el-descriptions-item label="学习人数">{{ stats.totalLearners ?? 0 }}</el-descriptions-item>
        <el-descriptions-item label="已完成人数">{{ stats.completedCount ?? 0 }}</el-descriptions-item>
        <el-descriptions-item label="完成率">{{ stats.completionRate ?? 0 }}%</el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <!-- 课程学员列表 -->
    <el-dialog v-model="studentsVisible" :title="studentsTitle" width="780px" destroy-on-close>
      <vxe-grid
        border
        :loading="studentsLoading"
        :data="students"
        :columns="studentColumns"
        max-height="480"
      >
        <template #s_student="{ row }">
          <div class="student-cell">
            <el-avatar :size="28" :src="row.avatar || undefined">
              {{ (row.userNickname || "?").charAt(0) }}
            </el-avatar>
            <div class="student-info">
              <div class="student-name">{{ row.userNickname || "-" }}</div>
              <div v-if="row.mobile" class="student-mobile">{{ row.mobile }}</div>
            </div>
          </div>
        </template>
        <template #s_progress="{ row }">
          <div class="progress-cell">
            <div class="progress-top">
              <el-progress
                :percentage="row.progressPct"
                :stroke-width="8"
                :show-text="false"
                color="var(--el-color-primary)"
                class="progress-bar"
              />
              <span class="progress-pct">{{ row.progressPct }}%</span>
            </div>
            <div class="progress-time">
              {{ formatSeconds(row.learnedSec) }} / {{ formatSeconds(row.totalSec) }}
            </div>
          </div>
        </template>
        <template #s_completed="{ row }">
          <el-tag v-if="row.isCompleted" type="success" size="small">已完成</el-tag>
          <el-tag v-else type="info" size="small">学习中</el-tag>
        </template>
      </vxe-grid>
    </el-dialog>
  </div>
</template>

<script setup name="courseProgress">
import { ref, reactive, onMounted } from "vue";
import { ElMessage } from "element-plus";
import { Refresh, View, DataAnalysis, Search } from "@element-plus/icons-vue";
import {
  getLearnProgressPageApi,
  getLearnProgressStatsApi,
  getCourseStudentsApi
} from "@/api/modules/biz-ext";
import { getCoursePageApi } from "@/api/modules/course";

const xGrid = ref();
const courseOptions = ref([]);

const statsVisible = ref(false);
const statsLoading = ref(false);
const stats = ref(null);
const statCourseId = ref("");

const studentsVisible = ref(false);
const studentsLoading = ref(false);
const students = ref([]);
const studentsTitle = ref("课程学员列表");

const formatSeconds = s => {
  const sec = Number(s || 0);
  const m = Math.floor(sec / 60);
  const r = sec % 60;
  return `${m}分${r}秒`;
};

const formatTime = t => {
  if (!t) return "";
  return String(t).replace("T", " ").slice(0, 19);
};

const normalizeRow = row => {
  if (!row) return row;
  const learnedSec = Number(row.learnedSec ?? 0);
  const totalSec = Number(row.totalSec ?? 0);
  let progressPct = Number(row.progressPct ?? 0);
  if (!progressPct && totalSec > 0) {
    progressPct = Math.min(100, Math.round((learnedSec / totalSec) * 100));
  }
  const isFinished = row.isFinished === "1" || row.isFinished === 1 || row.isCompleted === true;
  return {
    ...row,
    id: row.progressId ?? row.id,
    userId: row.customerId ?? row.userId,
    customerId: row.customerId ?? row.userId,
    userNickname: row.nickName || row.userNickname || row.customerName || "",
    avatar: row.avatar || row.userAvatar || "",
    mobile: row.mobile || row.phone || "",
    memberLevelName: row.memberLevelName || row.memberLevel || "-",
    learnedSec,
    totalSec,
    progressPct: Math.min(100, Math.max(0, progressPct)),
    isCompleted: isFinished,
    lastLearnTime: formatTime(row.lastLearnTime),
    createTime: formatTime(row.createTime)
  };
};

const matchFilter = (row, form = {}) => {
  if (form.courseId !== "" && form.courseId != null && String(row.courseId) !== String(form.courseId)) {
    return false;
  }
  if (form.username) {
    const name = (row.userNickname || "").toLowerCase();
    if (!name.includes(String(form.username).toLowerCase())) return false;
  }
  if (form.isCompleted !== "" && form.isCompleted != null) {
    const wantDone =
      form.isCompleted === true || form.isCompleted === "true" || form.isCompleted === 1 || form.isCompleted === "1";
    if (Boolean(row.isCompleted) !== wantDone) return false;
  }
  return true;
};

const fetchProgressList = async () => {
  const res = await getLearnProgressPageApi({ pageNum: 1, pageSize: 500 });
  return (res.data?.records || []).map(normalizeRow);
};

const loadCourses = async () => {
  try {
    const res = await getCoursePageApi({ pageNum: 1, pageSize: 200 });
    courseOptions.value = (res.data?.records || []).map(c => ({
      courseId: c.courseId ?? c.id,
      courseTitle: c.courseTitle ?? c.title ?? ""
    }));
  } catch (e) {
    courseOptions.value = [];
  }
};

const studentColumns = [
  { type: "seq", width: 60, title: "序号" },
  { field: "userNickname", title: "学员", minWidth: 140, slots: { default: "s_student" } },
  { field: "memberLevelName", title: "会员等级", width: 100 },
  { field: "progressPct", title: "进度", minWidth: 200, slots: { default: "s_progress" } },
  { field: "isCompleted", title: "完成", width: 90, slots: { default: "s_completed" } },
  { field: "lastLearnTime", title: "最近学习", width: 170 }
];

const gridOptions = reactive({
  border: true,
  showOverflow: true,
  height: 600,
  loading: false,
  rowConfig: { keyField: "id", isHover: true, height: 64 },
  columnConfig: { resizable: true },
  toolbarConfig: { slots: { buttons: "toolbar_buttons" }, refresh: true, zoom: false },
  proxyConfig: {
    form: true,
    response: { result: "list", total: "total" },
    ajax: {
      query: async ({ page, form }) => {
        try {
          const hasFilter =
            (form.courseId != null && form.courseId !== "") ||
            (form.username != null && form.username !== "") ||
            (form.isCompleted !== "" && form.isCompleted != null);

          if (hasFilter) {
            const all = await fetchProgressList();
            const filtered = all.filter(r => matchFilter(r, form));
            const start = (page.currentPage - 1) * page.pageSize;
            return { list: filtered.slice(start, start + page.pageSize), total: filtered.length };
          }

          const res = await getLearnProgressPageApi({
            pageNum: page.currentPage,
            pageSize: page.pageSize
          });
          const data = res.data || {};
          return { list: (data.records || []).map(normalizeRow), total: data.total || 0 };
        } catch (e) {
          console.error("获取学习进度失败:", e);
          return { list: [], total: 0 };
        }
      }
    }
  },
  formConfig: {
    titleWidth: 80,
    items: [
      {
        field: "courseId",
        title: "课程",
        span: 6,
        itemRender: {
          name: "$select",
          options: [],
          props: { placeholder: "请选择", clearable: true, filterable: true }
        }
      },
      {
        field: "username",
        title: "学员",
        span: 6,
        itemRender: { name: "$input", props: { placeholder: "昵称", clearable: true } }
      },
      {
        field: "isCompleted",
        title: "是否完成",
        span: 6,
        itemRender: {
          name: "$select",
          options: [
            { label: "全部", value: "" },
            { label: "已完成", value: true },
            { label: "学习中", value: false }
          ],
          props: { placeholder: "请选择", clearable: true }
        }
      },
      {
        span: 6,
        align: "center",
        itemRender: {
          name: "$buttons",
          children: [
            { props: { type: "submit", content: "搜索", status: "primary", icon: "vxe-icon-search" } },
            { props: { type: "reset", content: "重置", icon: "vxe-icon-refresh" } }
          ]
        }
      }
    ]
  },
  pagerConfig: { enabled: true, pageSize: 10, pageSizes: [10, 20, 50] },
  columns: [
    { type: "seq", width: 60, title: "序号" },
    { field: "userNickname", title: "学员", minWidth: 140, slots: { default: "student" } },
    { field: "memberLevelName", title: "会员等级", width: 110 },
    { field: "courseTitle", title: "课程", minWidth: 160 },
    { field: "progressPct", title: "进度", minWidth: 220, slots: { default: "progress" } },
    { field: "isCompleted", title: "完成", width: 90, slots: { default: "completed" } },
    { field: "lastLearnTime", title: "最近学习", width: 170 },
    { title: "操作", width: 120, fixed: "right", slots: { default: "action" } }
  ]
});

/** 课程下拉选项同步到表格搜索表单 */
const syncCourseSelectOptions = () => {
  const item = gridOptions.formConfig.items.find(i => i.field === "courseId");
  if (item?.itemRender) {
    item.itemRender.options = courseOptions.value.map(c => ({
      label: c.courseTitle,
      value: String(c.courseId)
    }));
  }
};

onMounted(async () => {
  await loadCourses();
  syncCourseSelectOptions();
});

const handleRefresh = () => xGrid.value?.commitProxy("query");

const openStats = () => {
  statsVisible.value = true;
  stats.value = null;
  if (statCourseId.value) loadStats();
};

const loadStats = async () => {
  const courseId = String(statCourseId.value || "").trim();
  if (!courseId) {
    ElMessage.warning("请选择课程");
    return;
  }
  statsLoading.value = true;
  try {
    const res = await getLearnProgressStatsApi(courseId);
    stats.value = res.data || null;
    if (!res.data) ElMessage.info("该课程暂无学习数据");
  } catch (e) {
    stats.value = null;
    ElMessage.error(e?.msg || e?.message || "统计失败");
  } finally {
    statsLoading.value = false;
  }
};

const viewStudents = async row => {
  studentsVisible.value = true;
  studentsLoading.value = true;
  students.value = [];
  studentsTitle.value = row.courseTitle ? `课程学员列表 - ${row.courseTitle}` : "课程学员列表";
  try {
    const res = await getCourseStudentsApi(row.courseId);
    const data = res.data;
    const rawList = Array.isArray(data) ? data : data?.records || data?.list || [];
    students.value = rawList.map(normalizeRow);
  } catch (e) {
    students.value = [];
    ElMessage.error(e?.msg || "加载学员失败");
  } finally {
    studentsLoading.value = false;
  }
};
</script>

<style scoped>
.table-box {
  background: #fff;
  border-radius: 8px;
}

.student-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}

.student-info {
  min-width: 0;
  display: flex;
  flex-direction: column;
  line-height: 1.3;
}

.student-name {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  color: var(--el-text-color-primary);
}

.student-mobile {
  margin-top: 2px;
  font-size: 12px;
  color: var(--el-text-color-secondary);
}

.progress-cell {
  padding: 4px 0;
  min-width: 160px;
}

.progress-top {
  display: flex;
  align-items: center;
  gap: 8px;
}

.progress-bar {
  flex: 1;
  min-width: 0;
}

.progress-pct {
  flex-shrink: 0;
  width: 42px;
  text-align: right;
  font-size: 13px;
  color: var(--el-text-color-regular);
}

.progress-time {
  margin-top: 2px;
  font-size: 12px;
  color: var(--el-text-color-secondary);
}
</style>
