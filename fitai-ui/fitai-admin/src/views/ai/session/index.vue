<template>
  <div class="table-box p-5">
    <vxe-grid ref="xGrid" v-bind="gridOptions" @checkbox-change="syncSelection" @checkbox-all="syncSelection">
      <template #toolbar_buttons>
        <el-button type="danger" :icon="Delete" :disabled="!selection.length" @click="handleBatchDelete">批量删除</el-button>
        <el-date-picker
          v-model="dateRange"
          type="datetimerange"
          value-format="YYYY-MM-DD HH:mm:ss"
          range-separator="至"
          start-placeholder="开始时间"
          end-placeholder="结束时间"
          class="mx-2"
          @change="handleRefresh"
        />
        <el-button :icon="Refresh" @click="handleRefresh">刷新</el-button>
      </template>
      <template #type="{ row }">
        <el-tag size="small">{{ typeLabel(row.type) }}</el-tag>
      </template>
      <template #status="{ row }">
        <el-tag v-if="row.status === 1" type="success">活跃</el-tag>
        <el-tag v-else type="info">已归档</el-tag>
      </template>
      <template #action="{ row }">
        <el-button type="primary" link :icon="View" @click="handleDetail(row)">详情</el-button>
        <el-button v-if="row.status === 1" type="warning" link @click="handleArchive(row, 0)">归档</el-button>
        <el-button v-else type="success" link @click="handleArchive(row, 1)">恢复</el-button>
        <el-button type="primary" link :icon="Download" @click="handleExport(row)">导出</el-button>
        <el-button type="danger" link :icon="Delete" @click="handleDelete(row)">删除</el-button>
      </template>
    </vxe-grid>

    <!-- 会话详情 + 消息 -->
    <el-dialog v-model="detailVisible" title="会话详情" width="820px" destroy-on-close top="6vh">
      <el-descriptions v-if="detail" :column="3" border size="small" class="mb-3">
        <el-descriptions-item label="会话ID">{{ detail.id }}</el-descriptions-item>
        <el-descriptions-item label="用户ID">{{ detail.userId ?? "-" }}</el-descriptions-item>
        <el-descriptions-item label="场景">{{ typeLabel(detail.type) }}</el-descriptions-item>
        <el-descriptions-item label="标题" :span="2">{{ detail.title || "-" }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag v-if="detail.status === 1" type="success" size="small">活跃</el-tag>
          <el-tag v-else type="info" size="small">已归档</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="消息数">{{ detail.messageCount ?? 0 }}</el-descriptions-item>
        <el-descriptions-item label="Token 合计">{{ detail.totalTokens ?? 0 }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ detail.createTime || "-" }}</el-descriptions-item>
      </el-descriptions>

      <div class="msg-toolbar">
        <span class="msg-title">消息记录</span>
        <el-select v-model="msgRole" placeholder="全部角色" clearable size="small" style="width: 140px" @change="loadMessages(1)">
          <el-option label="用户" value="user" />
          <el-option label="助手" value="assistant" />
          <el-option label="系统" value="system" />
        </el-select>
      </div>
      <el-table :data="messages" border size="small" v-loading="msgLoading" max-height="360">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column label="角色" width="90">
          <template #default="{ row }">
            <el-tag size="small" :type="roleTagType(row.role)">{{ roleLabel(row.role) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="content" label="内容" min-width="260" show-overflow-tooltip />
        <el-table-column prop="tokens" label="Token" width="80" />
        <el-table-column prop="model" label="模型" width="130" />
        <el-table-column prop="createTime" label="时间" width="160" />
      </el-table>
      <el-pagination
        class="mt-2"
        layout="total, prev, pager, next"
        :total="msgTotal"
        :page-size="msgQuery.pageSize"
        :current-page="msgQuery.pageNum"
        @current-change="loadMessages"
      />
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="aiSession">
import { ref, reactive } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { Refresh, View, Delete, Download } from "@element-plus/icons-vue";
import {
  getSessionPageApi,
  getSessionByIdApi,
  getSessionMessagePageApi,
  archiveSessionApi,
  activateSessionApi,
  deleteSessionsApi,
  exportSessionApi
} from "@/api/modules/ai-obs";

const xGrid = ref();
const dateRange = ref([]);
const selection = ref([]);

function typeLabel(t) {
  const map = { chat: "对话", plan: "训练计划", diet: "饮食方案", review: "训练点评" };
  return map[t] || t || "-";
}
function roleLabel(r) {
  const map = { user: "用户", assistant: "助手", system: "系统" };
  return map[r] || r || "-";
}
function roleTagType(r) {
  const map = { user: "primary", assistant: "success", system: "info" };
  return map[r] || "";
}

const gridOptions = reactive({
  border: true,
  showOverflow: true,
  height: 600,
  loading: false,
  rowConfig: { keyField: "id" },
  checkboxConfig: { reserve: true },
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
          const res = await getSessionPageApi(params);
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
      { field: "keyword", title: "标题", span: 5, itemRender: { name: "$input", props: { placeholder: "会话标题" } } },
      { field: "userId", title: "用户ID", span: 5, itemRender: { name: "$input", props: { placeholder: "用户ID" } } },
      {
        field: "type",
        title: "场景",
        span: 5,
        itemRender: {
          name: "$select",
          options: [
            { label: "全部", value: "" },
            { label: "对话", value: "chat" },
            { label: "训练计划", value: "plan" },
            { label: "饮食方案", value: "diet" },
            { label: "训练点评", value: "review" }
          ],
          props: { placeholder: "请选择" }
        }
      },
      {
        field: "status",
        title: "状态",
        span: 4,
        itemRender: {
          name: "$select",
          options: [
            { label: "全部", value: "" },
            { label: "活跃", value: 1 },
            { label: "已归档", value: 0 }
          ],
          props: { placeholder: "请选择" }
        }
      },
      {
        span: 5,
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
    { type: "checkbox", width: 50 },
    { type: "seq", width: 60 },
    { field: "id", title: "会话ID", width: 90 },
    { field: "userId", title: "用户ID", width: 90 },
    { field: "title", title: "标题", minWidth: 180 },
    { field: "type", title: "场景", width: 100, slots: { default: "type" } },
    { field: "messageCount", title: "消息数", width: 90 },
    { field: "status", title: "状态", width: 90, slots: { default: "status" } },
    { field: "lastMessageTime", title: "最后消息", width: 170 },
    { field: "createTime", title: "创建时间", width: 170 },
    { title: "操作", width: 260, fixed: "right", slots: { default: "action" } }
  ]
});

function syncSelection() {
  selection.value = xGrid.value?.getCheckboxRecords() || [];
}

const handleRefresh = () => {
  xGrid.value?.commitProxy("query");
  selection.value = [];
};

// 详情 + 消息
const detailVisible = ref(false);
const detail = ref(null);
const messages = ref([]);
const msgTotal = ref(0);
const msgLoading = ref(false);
const msgRole = ref("");
const msgQuery = reactive({ conversationId: null, pageNum: 1, pageSize: 10 });

const handleDetail = async row => {
  try {
    const res = await getSessionByIdApi(row.id);
    detail.value = res.data || null;
    msgQuery.conversationId = row.id;
    msgRole.value = "";
    detailVisible.value = true;
    loadMessages(1);
  } catch (e) {
    ElMessage.error("获取详情失败");
  }
};

const loadMessages = async (page = 1) => {
  msgQuery.pageNum = page;
  msgLoading.value = true;
  try {
    const res = await getSessionMessagePageApi({
      conversationId: msgQuery.conversationId,
      role: msgRole.value || null,
      pageNum: msgQuery.pageNum,
      pageSize: msgQuery.pageSize
    });
    const data = res.data || {};
    messages.value = data.records || [];
    msgTotal.value = data.total || 0;
  } catch (e) {
    messages.value = [];
    msgTotal.value = 0;
  } finally {
    msgLoading.value = false;
  }
};

const handleArchive = (row, status) => {
  const api = status === 0 ? archiveSessionApi : activateSessionApi;
  api(row.id)
    .then(() => {
      ElMessage.success(status === 0 ? "已归档" : "已恢复");
      handleRefresh();
    })
    .catch(() => ElMessage.error("操作失败"));
};

const handleExport = async row => {
  try {
    const blob = await exportSessionApi(row.id);
    const url = window.URL.createObjectURL(new Blob([blob]));
    const link = document.createElement("a");
    link.href = url;
    link.download = `session_${row.id}.csv`;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    window.URL.revokeObjectURL(url);
  } catch (e) {
    ElMessage.error("导出失败");
  }
};

const handleDelete = row => {
  ElMessageBox.confirm("删除会话将同时删除其全部消息，确定删除？", "提示", { type: "warning" }).then(async () => {
    try {
      await deleteSessionsApi(row.id);
      ElMessage.success("删除成功");
      handleRefresh();
    } catch (e) {
      ElMessage.error("删除失败");
    }
  });
};

const handleBatchDelete = () => {
  const ids = selection.value.map(r => r.id);
  if (!ids.length) return;
  ElMessageBox.confirm(`确定删除选中的 ${ids.length} 个会话（含消息）？`, "提示", { type: "warning" }).then(async () => {
    try {
      await deleteSessionsApi(ids.join(","));
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
.msg-toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 8px;
}
.msg-title {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
}
</style>
