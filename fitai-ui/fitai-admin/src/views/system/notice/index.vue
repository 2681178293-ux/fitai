<template>
  <div class="table-box p-5">
    <vxe-grid ref="xGrid" v-bind="gridOptions">
      <template #toolbar_buttons>
        <el-button type="primary" :icon="Plus" @click="handleAdd">新增公告</el-button>
        <el-button :icon="Refresh" @click="handleRefresh">刷新</el-button>
      </template>

      <template #type="{ row }">
        <el-tag :type="Number(row.noticeType) === 1 ? 'primary' : 'success'" size="small">
          {{ typeMap[row.noticeType] || row.noticeType }}
        </el-tag>
      </template>

      <template #target="{ row }">
        <span>{{ formatTarget(row) }}</span>
      </template>

      <template #channel="{ row }">
        <span>{{ formatChannel(row) }}</span>
      </template>

      <template #status="{ row }">
        <el-tag :type="statusMap[row.status]?.type || 'info'" size="small">
          {{ statusMap[row.status]?.label || row.status }}
        </el-tag>
      </template>

      <template #action="{ row }">
        <el-button type="primary" link :icon="Edit" @click="handleEdit(row)">编辑</el-button>
        <el-button
          v-if="Number(row.status) !== 1"
          type="success"
          link
          :icon="Promotion"
          @click="handlePublish(row)"
        >
          发布
        </el-button>
        <el-button v-else type="warning" link :icon="RefreshLeft" @click="handleRevoke(row)">撤回</el-button>
        <el-button type="info" link :icon="View" @click="openReadStats(row)">已读统计</el-button>
        <el-button type="danger" link :icon="Delete" @click="handleDelete(row)">删除</el-button>
      </template>
    </vxe-grid>

    <!-- 新增/编辑 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑公告' : '新增公告'" width="640px" destroy-on-close>
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="90px">
        <el-form-item label="标题" prop="noticeTitle">
          <el-input v-model="formData.noticeTitle" placeholder="公告标题" />
        </el-form-item>
        <el-form-item label="类型" prop="noticeType">
          <el-select v-model="formData.noticeType" placeholder="请选择" style="width: 100%">
            <el-option label="公告" value="1" />
            <el-option label="推送" value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="内容">
          <el-input v-model="formData.noticeContent" type="textarea" :rows="6" placeholder="公告内容" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 已读统计 -->
    <el-dialog v-model="readVisible" title="公告已读统计" width="600px">
      <el-alert
        :title="`已读人数：${readCount}`"
        type="success"
        :closable="false"
        show-icon
        class="mb-3"
      />
      <vxe-grid border :loading="readLoading" :data="readList" :columns="readColumns" max-height="400" />
    </el-dialog>
  </div>
</template>

<script setup name="systemNotice">
import { ref, reactive } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { Plus, Refresh, Edit, Delete, Promotion, RefreshLeft, View } from "@element-plus/icons-vue";
import {
  getNoticePageApi,
  addNoticeApi,
  updateNoticeApi,
  deleteNoticeApi,
  publishNoticeApi,
  revokeNoticeApi,
  getNoticeReadStatsApi,
  getNoticeReadListApi
} from "@/api/modules/biz-ext";
import { showNoticeNotification } from "@/utils/noticeNotify";
import mittBus from "@/utils/mittBus";

const xGrid = ref();
const formRef = ref();
const dialogVisible = ref(false);
const isEdit = ref(false);
const submitLoading = ref(false);

const readVisible = ref(false);
const readLoading = ref(false);
const readCount = ref(0);
const readList = ref([]);

const typeMap = { 1: "公告", 2: "推送", "1": "公告", "2": "推送" };
/** 目标：0全部 1指定用户 2用户分群（公告不展示，显示 -） */
const targetMap = { 0: "全部", 1: "指定用户", 2: "用户分群", "0": "全部", "1": "指定用户", "2": "用户分群" };
const statusMap = {
  0: { label: "草稿", type: "info" },
  1: { label: "已发布", type: "success" },
  2: { label: "已撤回", type: "warning" },
  "0": { label: "草稿", type: "info" },
  "1": { label: "已发布", type: "success" },
  "2": { label: "已撤回", type: "warning" }
};

const isAnnounce = row => String(row.noticeType) === "1";
const formatTarget = row => {
  if (isAnnounce(row)) return "-";
  return targetMap[row.targetType] || row.targetType || "-";
};
const formatChannel = row => {
  if (isAnnounce(row)) return "-";
  return row.pushChannel || "-";
};

const readColumns = [
  { type: "seq", width: 60 },
  { field: "userId", title: "用户ID", width: 120 },
  { field: "readTime", title: "阅读时间", minWidth: 180 }
];

const formData = reactive({
  noticeId: null,
  noticeTitle: "",
  noticeType: "1",
  noticeContent: ""
});

const formRules = {
  noticeTitle: [{ required: true, message: "请输入公告标题", trigger: "blur" }],
  noticeType: [{ required: true, message: "请选择公告类型", trigger: "change" }]
};

const gridOptions = reactive({
  border: true,
  showOverflow: true,
  height: 600,
  loading: false,
  rowConfig: { keyField: "noticeId" },
  toolbarConfig: { slots: { buttons: "toolbar_buttons" }, refresh: true, zoom: false },
  proxyConfig: {
    form: true,
    response: { result: "list", total: "total" },
    ajax: {
      query: async ({ page, form }) => {
        const params = { pageNum: page.currentPage, pageSize: page.pageSize, ...form };
        try {
          const res = await getNoticePageApi(params);
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
      { field: "noticeTitle", title: "关键词", span: 6, itemRender: { name: "$input", props: { placeholder: "公告标题" } } },
      {
        field: "noticeType",
        title: "类型",
        span: 6,
        itemRender: {
          name: "$select",
          options: [
            { label: "全部", value: "" },
            { label: "公告", value: "1" },
            { label: "推送", value: "2" }
          ],
          props: { placeholder: "请选择" }
        }
      },
      {
        field: "status",
        title: "状态",
        span: 6,
        itemRender: {
          name: "$select",
          options: [
            { label: "全部", value: "" },
            { label: "草稿", value: "0" },
            { label: "已发布", value: "1" },
            { label: "已撤回", value: "2" }
          ],
          props: { placeholder: "请选择" }
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
  pagerConfig: { enabled: true },
  columns: [
    { type: "seq", width: 60 },
    { field: "noticeId", title: "主键", width: 90 },
    { field: "noticeTitle", title: "标题", minWidth: 180 },
    { field: "noticeType", title: "类型", width: 90, slots: { default: "type" } },
    { field: "targetType", title: "目标", width: 100, slots: { default: "target" } },
    { field: "pushChannel", title: "推送渠道", width: 100, slots: { default: "channel" } },
    { field: "status", title: "状态", width: 100, slots: { default: "status" } },
    { field: "publishTime", title: "发布时间", width: 170 },
    { field: "createBy", title: "创建者", width: 110 },
    { field: "createTime", title: "创建时间", width: 170 },
    { title: "操作", width: 290, fixed: "right", slots: { default: "action" } }
  ]
});

const handleRefresh = () => xGrid.value?.commitProxy("query");

const handleAdd = () => {
  isEdit.value = false;
  Object.assign(formData, { noticeId: null, noticeTitle: "", noticeType: "1", noticeContent: "" });
  dialogVisible.value = true;
};

const handleEdit = row => {
  isEdit.value = true;
  Object.assign(formData, {
    noticeId: row.noticeId,
    noticeTitle: row.noticeTitle,
    noticeType: row.noticeType != null ? String(row.noticeType) : "1",
    noticeContent: row.noticeContent || ""
  });
  dialogVisible.value = true;
};

const handleSubmit = async () => {
  try {
    await formRef.value.validate();
    submitLoading.value = true;
    const payload = {
      noticeTitle: formData.noticeTitle,
      noticeType: String(formData.noticeType),
      noticeContent: formData.noticeContent || null
    };
    if (isEdit.value) await updateNoticeApi({ noticeId: formData.noticeId, ...payload });
    else await addNoticeApi(payload);
    ElMessage.success(isEdit.value ? "编辑成功" : "新增成功");
    dialogVisible.value = false;
    handleRefresh();
  } catch (e) {
    if (e !== false) ElMessage.error("操作失败");
  } finally {
    submitLoading.value = false;
  }
};

const handleDelete = row => {
  ElMessageBox.confirm("确定删除该公告？", "提示", { type: "warning" }).then(async () => {
    try {
      await deleteNoticeApi(row.noticeId);
      ElMessage.success("删除成功");
      handleRefresh();
    } catch (e) {
      ElMessage.error("删除失败");
    }
  });
};

const handlePublish = row => {
  ElMessageBox.confirm("确定发布该公告？", "提示", { type: "warning" }).then(async () => {
    try {
      await publishNoticeApi(row.noticeId);
      ElMessage.success("发布成功");
      // 右上角通知弹窗（SSE 未连上时也保证发布者能看到）
      showNoticeNotification({
        noticeId: row.noticeId,
        noticeTitle: row.noticeTitle,
        noticeContent: row.noticeContent
      });
      mittBus.emit("noticePublished", row);
      handleRefresh();
    } catch (e) {
      ElMessage.error("发布失败");
    }
  });
};

const handleRevoke = row => {
  ElMessageBox.confirm("确定撤回该公告？", "提示", { type: "warning" }).then(async () => {
    try {
      await revokeNoticeApi(row.noticeId);
      ElMessage.success("撤回成功");
      handleRefresh();
    } catch (e) {
      ElMessage.error("撤回失败");
    }
  });
};

const openReadStats = async row => {
  readVisible.value = true;
  readLoading.value = true;
  readCount.value = 0;
  readList.value = [];
  try {
    const [statRes, listRes] = await Promise.all([
      getNoticeReadStatsApi(row.noticeId),
      getNoticeReadListApi(row.noticeId)
    ]);
    readCount.value = statRes.data ?? 0;
    readList.value = listRes.data || [];
  } catch (e) {
    // ignore
  } finally {
    readLoading.value = false;
  }
};
</script>

<style scoped>
.table-box {
  background: #fff;
  border-radius: 8px;
}
</style>
