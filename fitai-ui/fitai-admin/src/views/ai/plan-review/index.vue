<template>
  <div class="table-box p-5">
    <vxe-grid ref="xGrid" v-bind="gridOptions">
      <template #toolbar_buttons>
        <el-tag type="warning" effect="plain" class="mr-2">待审：{{ pendingCount }}</el-tag>
        <el-date-picker
          v-model="dateRange"
          type="datetimerange"
          value-format="YYYY-MM-DD HH:mm:ss"
          range-separator="至"
          start-placeholder="开始时间"
          end-placeholder="结束时间"
          class="mr-2"
          @change="handleRefresh"
        />
        <el-button :icon="Refresh" @click="handleRefresh">刷新</el-button>
      </template>
      <template #planType="{ row }">
        <el-tag :type="row.planType === 'train' ? 'primary' : 'success'">{{ planTypeLabel(row.planType) }}</el-tag>
      </template>
      <template #auditStatus="{ row }">
        <el-tag v-if="String(row.auditStatus) === '0'" type="warning">待审</el-tag>
        <el-tag v-else-if="String(row.auditStatus) === '1'" type="success">通过</el-tag>
        <el-tag v-else type="danger">拒绝</el-tag>
      </template>
      <template #action="{ row }">
        <el-button type="primary" link :icon="View" @click="handleDetail(row)">详情</el-button>
        <template v-if="String(row.auditStatus) === '0'">
          <el-button type="success" link :icon="Check" @click="handleAudit(row, '1')">通过</el-button>
          <el-button type="danger" link :icon="Close" @click="handleAudit(row, '2')">驳回</el-button>
        </template>
      </template>
    </vxe-grid>

    <!-- 详情 -->
    <el-dialog v-model="detailVisible" title="方案审核详情" width="720px" destroy-on-close>
      <el-descriptions v-if="detail" :column="2" border size="small">
        <el-descriptions-item label="审核ID">{{ detail.reviewId }}</el-descriptions-item>
        <el-descriptions-item label="方案类型">{{ planTypeLabel(detail.planType) }}</el-descriptions-item>
        <el-descriptions-item label="方案ID">{{ detail.planId ?? "-" }}</el-descriptions-item>
        <el-descriptions-item label="用户ID">{{ detail.customerId ?? "-" }}</el-descriptions-item>
        <el-descriptions-item label="方案标题">{{ detail.planTitle || "-" }}</el-descriptions-item>
        <el-descriptions-item label="方案是否存在">
          <el-tag v-if="detail.planExists" type="success" size="small">存在</el-tag>
          <el-tag v-else type="info" size="small">已删除</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="审核状态">
          <el-tag v-if="String(detail.auditStatus) === '0'" type="warning">待审</el-tag>
          <el-tag v-else-if="String(detail.auditStatus) === '1'" type="success">通过</el-tag>
          <el-tag v-else type="danger">拒绝</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="审核人">{{ detail.auditorId ?? "-" }}</el-descriptions-item>
        <el-descriptions-item label="审核时间">{{ detail.auditTime || "-" }}</el-descriptions-item>
        <el-descriptions-item label="提交时间">{{ detail.createTime || "-" }}</el-descriptions-item>
        <el-descriptions-item label="审核原因" :span="2">{{ detail.auditReason || "-" }}</el-descriptions-item>
        <el-descriptions-item label="方案快照" :span="2">
          <pre class="snapshot-json">{{ formatJson(detail.contentSnapshot) }}</pre>
        </el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
        <template v-if="detail && String(detail.auditStatus) === '0'">
          <el-button type="success" @click="handleAudit(detail, '1')">通过</el-button>
          <el-button type="danger" @click="handleAudit(detail, '2')">驳回</el-button>
        </template>
      </template>
    </el-dialog>

    <!-- 驳回原因 -->
    <el-dialog v-model="rejectVisible" title="驳回方案" width="480px" destroy-on-close>
      <el-form ref="rejectFormRef" :model="rejectForm" :rules="rejectRules" label-width="80px">
        <el-form-item label="驳回原因" prop="auditReason">
          <el-input v-model="rejectForm.auditReason" type="textarea" :rows="3" placeholder="请填写驳回原因" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="rejectVisible = false">取消</el-button>
        <el-button type="danger" :loading="auditLoading" @click="submitReject">确定驳回</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="aiPlanReview">
import { ref, reactive, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { Refresh, View, Check, Close } from "@element-plus/icons-vue";
import {
  getPlanReviewPageApi,
  getPlanReviewByIdApi,
  auditPlanReviewApi,
  getPlanReviewPendingCountApi
} from "@/api/modules/ai-obs";

const xGrid = ref();
const pendingCount = ref(0);
const dateRange = ref([]);

function planTypeLabel(t) {
  const map = { train: "训练计划", diet: "饮食方案" };
  return map[t] || t || "-";
}
function formatJson(raw) {
  if (!raw) return "-";
  try {
    return JSON.stringify(JSON.parse(raw), null, 2);
  } catch (e) {
    return raw;
  }
}

const gridOptions = reactive({
  border: true,
  showOverflow: true,
  height: 580,
  loading: false,
  rowConfig: { keyField: "reviewId" },
  toolbarConfig: { slots: { buttons: "toolbar_buttons" }, refresh: true, zoom: false },
  proxyConfig: {
    form: true,
    response: { result: "list", total: "total" },
    ajax: {
      query: async ({ page, form }) => {
        const params = {
          pageNum: page.currentPage,
          pageSize: page.pageSize,
          ...form,
          beginTime: dateRange.value?.[0] || null,
          endTime: dateRange.value?.[1] || null
        };
        try {
          const res = await getPlanReviewPageApi(params);
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
      {
        field: "planType",
        title: "方案类型",
        span: 6,
        itemRender: {
          name: "$select",
          options: [
            { label: "全部", value: "" },
            { label: "训练计划", value: "train" },
            { label: "饮食方案", value: "diet" }
          ],
          props: { placeholder: "请选择" }
        }
      },
      { field: "customerId", title: "用户ID", span: 5, itemRender: { name: "$input", props: { placeholder: "用户ID" } } },
      {
        field: "auditStatus",
        title: "审核状态",
        span: 6,
        itemRender: {
          name: "$select",
          options: [
            { label: "全部", value: "" },
            { label: "待审", value: "0" },
            { label: "通过", value: "1" },
            { label: "拒绝", value: "2" }
          ],
          props: { placeholder: "请选择" }
        }
      },
      {
        span: 7,
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
  columns: [
    { type: "seq", width: 60 },
    { field: "reviewId", title: "审核ID", width: 90 },
    { field: "planType", title: "类型", width: 110, slots: { default: "planType" } },
    { field: "planId", title: "方案ID", width: 90 },
    { field: "customerId", title: "用户ID", width: 90 },
    { field: "auditStatus", title: "状态", width: 90, slots: { default: "auditStatus" } },
    { field: "auditReason", title: "审核原因", minWidth: 140, showOverflow: true },
    { field: "createTime", title: "提交时间", width: 170 },
    { field: "auditTime", title: "审核时间", width: 170 },
    { title: "操作", width: 200, fixed: "right", slots: { default: "action" } }
  ]
});

const handleRefresh = () => {
  xGrid.value?.commitProxy("query");
  loadPending();
};

async function loadPending() {
  try {
    const res = await getPlanReviewPendingCountApi();
    pendingCount.value = res.data ?? 0;
  } catch (e) {
    pendingCount.value = 0;
  }
}

// 详情
const detailVisible = ref(false);
const detail = ref(null);
const handleDetail = async row => {
  try {
    const res = await getPlanReviewByIdApi(row.reviewId);
    detail.value = res.data || null;
    detailVisible.value = true;
  } catch (e) {
    ElMessage.error("获取详情失败");
  }
};

// 审核
const auditLoading = ref(false);
const rejectVisible = ref(false);
const rejectFormRef = ref();
const rejectForm = reactive({ reviewId: null, auditReason: "" });
const rejectRules = { auditReason: [{ required: true, message: "请填写驳回原因", trigger: "blur" }] };

const handleAudit = (row, status) => {
  if (status === "2") {
    rejectForm.reviewId = row.reviewId;
    rejectForm.auditReason = "";
    rejectVisible.value = true;
    return;
  }
  ElMessageBox.confirm("确定审核通过该方案？", "提示", { type: "warning" }).then(async () => {
    try {
      await auditPlanReviewApi({ reviewId: row.reviewId, auditStatus: "1", auditReason: null });
      ElMessage.success("已通过");
      detailVisible.value = false;
      handleRefresh();
    } catch (e) {
      ElMessage.error(e?.msg || "操作失败");
    }
  });
};

const submitReject = async () => {
  try {
    await rejectFormRef.value.validate();
    auditLoading.value = true;
    await auditPlanReviewApi({ reviewId: rejectForm.reviewId, auditStatus: "2", auditReason: rejectForm.auditReason });
    ElMessage.success("已驳回");
    rejectVisible.value = false;
    detailVisible.value = false;
    handleRefresh();
  } catch (e) {
    if (e !== false) ElMessage.error(e?.msg || "操作失败");
  } finally {
    auditLoading.value = false;
  }
};

onMounted(loadPending);
</script>

<style scoped>
.table-box {
  background: #fff;
  border-radius: 8px;
}
.snapshot-json {
  max-height: 260px;
  overflow: auto;
  white-space: pre-wrap;
  word-break: break-all;
  margin: 0;
  font-size: 12px;
}
</style>
