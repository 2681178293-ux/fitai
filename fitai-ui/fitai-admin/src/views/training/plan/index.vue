<template>
  <div class="table-box p-5">
    <vxe-grid ref="xGrid" v-bind="gridOptions">
      <template #toolbar_buttons>
        <el-button :icon="Refresh" @click="handleRefresh">刷新</el-button>
      </template>

      <template #goal="{ row }">
        <el-tag size="small">{{ goalMap[row.goal] || "-" }}</el-tag>
      </template>

      <template #level="{ row }">
        <span>{{ levelMap[row.level] || "-" }}</span>
      </template>

      <template #ai="{ row }">
        <el-tag v-if="row.isAiGenerated" type="warning" size="small">AI生成</el-tag>
        <el-tag v-else type="info" size="small">手动</el-tag>
      </template>

      <template #status="{ row }">
        <el-tag :type="statusMap[row.status]?.type || 'info'" size="small">
          {{ statusMap[row.status]?.label || row.status }}
        </el-tag>
      </template>

      <template #action="{ row }">
        <el-button type="primary" link :icon="View" @click="handleView(row)">详情</el-button>
        <el-button type="warning" link :icon="Switch" @click="openStatus(row)">改状态</el-button>
        <el-button type="danger" link :icon="Delete" @click="handleDelete(row)">删除</el-button>
      </template>
    </vxe-grid>

    <!-- 详情 -->
    <el-dialog v-model="detailVisible" title="训练计划详情" width="720px">
      <el-descriptions v-if="detail.id" :column="2" border>
        <el-descriptions-item label="计划ID">{{ detail.id }}</el-descriptions-item>
        <el-descriptions-item label="计划名称">{{ detail.name }}</el-descriptions-item>
        <el-descriptions-item label="用户">{{ detail.userNickname || detail.userId }}</el-descriptions-item>
        <el-descriptions-item label="目标">{{ goalMap[detail.goal] || "-" }}</el-descriptions-item>
        <el-descriptions-item label="难度">{{ levelMap[detail.level] || "-" }}</el-descriptions-item>
        <el-descriptions-item label="是否AI生成">{{ detail.isAiGenerated ? "是" : "否" }}</el-descriptions-item>
        <el-descriptions-item label="开始日期">{{ detail.startDate || "-" }}</el-descriptions-item>
        <el-descriptions-item label="结束日期">{{ detail.endDate || "-" }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="statusMap[detail.status]?.type || 'info'" size="small">
            {{ statusMap[detail.status]?.label || detail.status }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ detail.createTime }}</el-descriptions-item>
        <el-descriptions-item label="计划明细(planData)" :span="2">
          <pre class="plan-json">{{ prettyPlan(detail.planData) }}</pre>
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <!-- 改状态 -->
    <el-dialog v-model="statusVisible" title="调整计划状态" width="420px">
      <el-form label-width="80px">
        <el-form-item label="状态">
          <el-select v-model="statusForm.status" style="width: 100%">
            <el-option label="草稿" :value="0" />
            <el-option label="进行中" :value="1" />
            <el-option label="已完成" :value="2" />
            <el-option label="已放弃" :value="3" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="statusVisible = false">取消</el-button>
        <el-button type="primary" :loading="statusLoading" @click="submitStatus">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="trainingPlan">
import { ref, reactive } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { Refresh, View, Switch, Delete } from "@element-plus/icons-vue";
import {
  getTrainPlanPageApi,
  getTrainPlanApi,
  updateTrainPlanStatusApi,
  deleteTrainPlanApi
} from "@/api/modules/biz-ext";

const xGrid = ref();
const detailVisible = ref(false);
const detail = ref({});
const statusVisible = ref(false);
const statusLoading = ref(false);
const statusForm = reactive({ id: null, status: 0 });

const goalMap = { 1: "减脂", 2: "增肌", 3: "塑形", 4: "保持健康" };
const levelMap = { 1: "入门", 2: "进阶", 3: "高级" };
const statusMap = {
  0: { label: "草稿", type: "info" },
  1: { label: "进行中", type: "success" },
  2: { label: "已完成", type: "primary" },
  3: { label: "已放弃", type: "danger" }
};

const prettyPlan = data => {
  if (data == null) return "-";
  try {
    return JSON.stringify(typeof data === "string" ? JSON.parse(data) : data, null, 2);
  } catch (e) {
    return String(data);
  }
};

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
          const res = await getTrainPlanPageApi(params);
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
      { field: "name", title: "名称", span: 5, itemRender: { name: "$input", props: { placeholder: "计划名称" } } },
      { field: "userId", title: "用户ID", span: 5, itemRender: { name: "$input", props: { placeholder: "用户ID" } } },
      {
        field: "goal",
        title: "目标",
        span: 5,
        itemRender: {
          name: "$select",
          options: [
            { label: "全部", value: "" },
            { label: "减脂", value: 1 },
            { label: "增肌", value: 2 },
            { label: "塑形", value: 3 },
            { label: "保持健康", value: 4 }
          ],
          props: { placeholder: "请选择" }
        }
      },
      {
        field: "status",
        title: "状态",
        span: 5,
        itemRender: {
          name: "$select",
          options: [
            { label: "全部", value: "" },
            { label: "草稿", value: 0 },
            { label: "进行中", value: 1 },
            { label: "已完成", value: 2 },
            { label: "已放弃", value: 3 }
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
    { field: "name", title: "计划名称", minWidth: 160 },
    { field: "userNickname", title: "用户", width: 120 },
    { field: "goal", title: "目标", width: 100, slots: { default: "goal" } },
    { field: "level", title: "难度", width: 90, slots: { default: "level" } },
    { field: "isAiGenerated", title: "来源", width: 100, slots: { default: "ai" } },
    { field: "status", title: "状态", width: 100, slots: { default: "status" } },
    { field: "createTime", title: "创建时间", width: 170 },
    { title: "操作", width: 200, fixed: "right", slots: { default: "action" } }
  ]
});

const handleRefresh = () => xGrid.value?.commitProxy("query");

const handleView = async row => {
  detail.value = {};
  detailVisible.value = true;
  try {
    const res = await getTrainPlanApi(row.id);
    detail.value = res.data || {};
  } catch (e) {
    detail.value = { ...row };
  }
};

const openStatus = row => {
  statusForm.id = row.id;
  statusForm.status = row.status ?? 0;
  statusVisible.value = true;
};

const submitStatus = async () => {
  statusLoading.value = true;
  try {
    await updateTrainPlanStatusApi(statusForm.id, statusForm.status);
    ElMessage.success("状态已更新");
    statusVisible.value = false;
    handleRefresh();
  } catch (e) {
    ElMessage.error("更新失败");
  } finally {
    statusLoading.value = false;
  }
};

const handleDelete = row => {
  ElMessageBox.confirm("确定删除该训练计划？", "提示", { type: "warning" }).then(async () => {
    try {
      await deleteTrainPlanApi(row.id);
      ElMessage.success("删除成功");
      handleRefresh();
    } catch (e) {
      ElMessage.error("删除失败");
    }
  });
};
</script>

<style scoped>
.table-box {
  background: #fff;
  border-radius: 8px;
}
.plan-json {
  max-height: 260px;
  overflow: auto;
  margin: 0;
  white-space: pre-wrap;
  word-break: break-all;
  font-size: 12px;
}
</style>
