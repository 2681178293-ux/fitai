<template>
  <div class="table-box p-5">
    <el-tabs v-model="activeTab">
      <el-tab-pane label="待审列表" name="pending">
        <vxe-grid ref="pendingGrid" v-bind="pendingOptions">
          <template #toolbar_buttons>
            <el-button :icon="Refresh" @click="refreshPending">刷新</el-button>
          </template>
          <template #target_type="{ row }">
            <el-tag size="small">{{ targetTypeMap[row.targetType] || row.targetType }}</el-tag>
          </template>
          <template #snapshot="{ row }">
            <div class="max-w-xs truncate" :title="row.contentSnapshot">{{ row.contentSnapshot || "-" }}</div>
          </template>
          <template #action="{ row }">
            <el-button type="success" link :icon="Select" @click="handlePass(row)">通过</el-button>
            <el-button type="danger" link :icon="CloseBold" @click="openReject(row)">驳回</el-button>
            <el-button type="primary" link :icon="View" @click="handleView(row)">详情</el-button>
          </template>
        </vxe-grid>
      </el-tab-pane>

      <el-tab-pane label="审核历史" name="history">
        <vxe-grid ref="historyGrid" v-bind="historyOptions">
          <template #toolbar_buttons>
            <el-button :icon="Refresh" @click="refreshHistory">刷新</el-button>
          </template>
          <template #target_type="{ row }">
            <el-tag size="small">{{ targetTypeMap[row.targetType] || row.targetType }}</el-tag>
          </template>
          <template #audit_status="{ row }">
            <el-tag :type="auditStatusMap[row.auditStatus]?.type || 'info'" size="small">
              {{ auditStatusMap[row.auditStatus]?.label || row.auditStatus }}
            </el-tag>
          </template>
          <template #snapshot="{ row }">
            <div class="max-w-xs truncate" :title="row.contentSnapshot">{{ row.contentSnapshot || "-" }}</div>
          </template>
          <template #action="{ row }">
            <el-button type="primary" link :icon="View" @click="handleView(row)">详情</el-button>
          </template>
        </vxe-grid>
      </el-tab-pane>
    </el-tabs>

    <!-- 驳回 -->
    <el-dialog v-model="rejectVisible" title="审核驳回" width="480px">
      <el-form ref="rejectRef" :model="rejectForm" :rules="rejectRules" label-width="90px">
        <el-form-item label="驳回原因" prop="reason">
          <el-input v-model="rejectForm.reason" type="textarea" :rows="3" placeholder="请输入驳回原因" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="rejectVisible = false">取消</el-button>
        <el-button type="primary" :loading="rejectLoading" @click="submitReject">确定</el-button>
      </template>
    </el-dialog>

    <!-- 详情 -->
    <el-dialog v-model="detailVisible" title="审核记录详情" width="620px">
      <el-descriptions v-if="detail.id" :column="1" border>
        <el-descriptions-item label="记录ID">{{ detail.id }}</el-descriptions-item>
        <el-descriptions-item label="对象类型">{{ targetTypeMap[detail.targetType] || detail.targetType }}</el-descriptions-item>
        <el-descriptions-item label="对象ID">{{ detail.targetId }}</el-descriptions-item>
        <el-descriptions-item label="审核状态">
          <el-tag :type="auditStatusMap[detail.auditStatus]?.type || 'info'" size="small">
            {{ auditStatusMap[detail.auditStatus]?.label || detail.auditStatus }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="驳回原因">{{ detail.auditReason || "-" }}</el-descriptions-item>
        <el-descriptions-item label="审核人ID">{{ detail.auditorId || "-" }}</el-descriptions-item>
        <el-descriptions-item label="审核时间">{{ detail.auditTime || "-" }}</el-descriptions-item>
        <el-descriptions-item label="提交时间">{{ detail.createTime }}</el-descriptions-item>
        <el-descriptions-item label="内容快照">
          <div class="whitespace-pre-wrap">{{ detail.contentSnapshot || "-" }}</div>
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup name="communityModeration">
import { ref, reactive } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { Refresh, Select, CloseBold, View } from "@element-plus/icons-vue";
import {
  getModerationPageApi,
  getModerationHistoryApi,
  getModerationApi,
  passModerationApi,
  rejectModerationApi
} from "@/api/modules/biz-ext";

const activeTab = ref("pending");
const pendingGrid = ref();
const historyGrid = ref();

const rejectVisible = ref(false);
const rejectLoading = ref(false);
const rejectRef = ref();
const rejectForm = reactive({ moderationId: null, reason: "" });
const rejectRules = { reason: [{ required: true, message: "请输入驳回原因", trigger: "blur" }] };

const detailVisible = ref(false);
const detail = ref({});

const targetTypeMap = { post: "动态", comment: "评论" };
const auditStatusMap = {
  0: { label: "待审", type: "warning" },
  1: { label: "通过", type: "success" },
  2: { label: "拒绝", type: "danger" }
};

const targetTypeFilter = {
  field: "targetType",
  title: "对象类型",
  span: 6,
  itemRender: {
    name: "$select",
    options: [
      { label: "全部", value: "" },
      { label: "动态", value: "post" },
      { label: "评论", value: "comment" }
    ],
    props: { placeholder: "请选择" }
  }
};

const searchButtons = {
  span: 6,
  align: "center",
  itemRender: {
    name: "$buttons",
    children: [
      { props: { type: "submit", content: "搜索", status: "primary" } },
      { props: { type: "reset", content: "重置" } }
    ]
  }
};

const pendingOptions = reactive({
  border: true,
  showOverflow: true,
  height: 560,
  rowConfig: { keyField: "id" },
  toolbarConfig: { slots: { buttons: "toolbar_buttons" }, refresh: true, zoom: false },
  proxyConfig: {
    form: true,
    response: { result: "list", total: "total" },
    ajax: {
      query: async ({ page, form }) => {
        const params = { pageNum: page.currentPage, pageSize: page.pageSize, auditStatus: 0, ...form };
        try {
          const res = await getModerationPageApi(params);
          const data = res.data || {};
          return { list: data.records || [], total: data.total || 0 };
        } catch (e) {
          return { list: [], total: 0 };
        }
      }
    }
  },
  formConfig: { items: [targetTypeFilter, searchButtons] },
  pagerConfig: { enabled: true },
  columns: [
    { type: "seq", width: 60 },
    { field: "id", title: "ID", width: 90 },
    { field: "targetType", title: "对象类型", width: 110, slots: { default: "target_type" } },
    { field: "targetId", title: "对象ID", width: 100 },
    { field: "contentSnapshot", title: "内容快照", minWidth: 240, slots: { default: "snapshot" } },
    { field: "createTime", title: "提交时间", width: 170 },
    { title: "操作", width: 200, fixed: "right", slots: { default: "action" } }
  ]
});

const historyOptions = reactive({
  border: true,
  showOverflow: true,
  height: 560,
  rowConfig: { keyField: "id" },
  toolbarConfig: { slots: { buttons: "toolbar_buttons" }, refresh: true, zoom: false },
  proxyConfig: {
    form: true,
    response: { result: "list", total: "total" },
    ajax: {
      query: async ({ page, form }) => {
        const params = { pageNum: page.currentPage, pageSize: page.pageSize, ...form };
        try {
          const res = await getModerationHistoryApi(params);
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
      targetTypeFilter,
      {
        field: "auditStatus",
        title: "审核状态",
        span: 6,
        itemRender: {
          name: "$select",
          options: [
            { label: "全部", value: "" },
            { label: "通过", value: 1 },
            { label: "拒绝", value: 2 }
          ],
          props: { placeholder: "请选择" }
        }
      },
      searchButtons
    ]
  },
  pagerConfig: { enabled: true },
  columns: [
    { type: "seq", width: 60 },
    { field: "id", title: "ID", width: 90 },
    { field: "targetType", title: "对象类型", width: 110, slots: { default: "target_type" } },
    { field: "targetId", title: "对象ID", width: 100 },
    { field: "auditStatus", title: "审核状态", width: 100, slots: { default: "audit_status" } },
    { field: "auditReason", title: "驳回原因", minWidth: 150 },
    { field: "contentSnapshot", title: "内容快照", minWidth: 200, slots: { default: "snapshot" } },
    { field: "auditTime", title: "审核时间", width: 170 },
    { title: "操作", width: 100, fixed: "right", slots: { default: "action" } }
  ]
});

const refreshPending = () => pendingGrid.value?.commitProxy("query");
const refreshHistory = () => historyGrid.value?.commitProxy("query");

const handlePass = row => {
  ElMessageBox.confirm("确定审核通过该内容？", "提示", { type: "warning" }).then(async () => {
    try {
      await passModerationApi(row.id);
      ElMessage.success("已通过");
      refreshPending();
    } catch (e) {
      ElMessage.error("操作失败");
    }
  });
};

const openReject = row => {
  rejectForm.moderationId = row.id;
  rejectForm.reason = "";
  rejectVisible.value = true;
};

const submitReject = async () => {
  try {
    await rejectRef.value.validate();
    rejectLoading.value = true;
    await rejectModerationApi({ moderationId: rejectForm.moderationId, reason: rejectForm.reason });
    ElMessage.success("已驳回");
    rejectVisible.value = false;
    refreshPending();
  } catch (e) {
    if (e !== false) ElMessage.error("操作失败");
  } finally {
    rejectLoading.value = false;
  }
};

const handleView = async row => {
  detail.value = {};
  detailVisible.value = true;
  try {
    const res = await getModerationApi(row.id);
    detail.value = res.data || {};
  } catch (e) {
    detail.value = { ...row };
  }
};
</script>

<style scoped>
.table-box {
  background: #fff;
  border-radius: 8px;
}
</style>
