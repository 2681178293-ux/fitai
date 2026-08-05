<template>
  <div class="table-box p-5">
    <vxe-grid ref="xGrid" v-bind="gridOptions">
      <template #toolbar_buttons>
        <el-button :icon="Refresh" @click="handleRefresh">刷新</el-button>
      </template>
      <template #type="{ row }">
        <el-tag size="small">{{ typeMap[row.type] || row.type }}</el-tag>
      </template>
      <template #lastTime="{ row }">
        {{ formatDateTime(row.lastMessageTime) }}
      </template>
      <template #action="{ row }">
        <el-button type="primary" link :icon="View" @click="handleViewMessages(row)">查看消息</el-button>
      </template>
    </vxe-grid>
    <el-dialog v-model="messageVisible" title="对话消息" width="640px" destroy-on-close>
      <div v-if="currentConversation" class="mb-4 text-gray-500 text-sm">
        会话：{{ currentConversation.title || '未命名' }} | 类型：{{ typeMap[currentConversation.type] || currentConversation.type }}
      </div>
      <div v-loading="messagesLoading" class="message-list">
        <div v-for="msg in messages" :key="msg.id" :class="['message-item', msg.role]">
          <span class="role-tag">{{ roleMap[msg.role] || msg.role }}</span>
          <div class="content">{{ msg.content }}</div>
          <div v-if="msg.tokens || msg.model" class="meta text-gray-400 text-xs mt-1">
            <span v-if="msg.tokens">Tokens: {{ msg.tokens }}</span>
            <span v-if="msg.model" class="ml-2">模型: {{ msg.model }}</span>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup name="aiConversation">
import { ref, reactive } from "vue";
import { Refresh, View } from "@element-plus/icons-vue";
import { getConversationsPageApi, getConversationMessagesApi } from "@/api/modules/ai";

const typeMap = { chat: "闲聊", plan: "训练计划", diet: "饮食方案", review: "训练点评" };
const roleMap = { user: "用户", assistant: "AI", system: "系统" };

function formatDateTime(val) {
  if (val == null || val === "") return "-";
  const d = new Date(val);
  if (Number.isNaN(d.getTime())) return String(val);
  return d.toLocaleString();
}

const xGrid = ref();
const messageVisible = ref(false);
const messagesLoading = ref(false);
const messages = ref([]);
const currentConversation = ref(null);

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
        const params = {
          pageNum: page.currentPage,
          pageSize: page.pageSize,
          userId: form?.userId,
          type: form?.type
        };
        try {
          const res = await getConversationsPageApi(params);
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
      { field: "userId", title: "用户ID", span: 6, itemRender: { name: "$input", props: { placeholder: "用户ID" } } },
      {
        field: "type",
        title: "类型",
        span: 6,
        itemRender: {
          name: "$select",
          options: [
            { label: "全部", value: "" },
            { label: "闲聊", value: "chat" },
            { label: "训练计划", value: "plan" },
            { label: "饮食方案", value: "diet" },
            { label: "训练点评", value: "review" }
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
  columns: [
    { type: "seq", width: 60 },
    { field: "id", title: "会话ID", width: 100 },
    { field: "userId", title: "用户ID", width: 100 },
    { field: "title", title: "标题", minWidth: 160 },
    { field: "type", title: "类型", width: 100, slots: { default: "type" } },
    { field: "messageCount", title: "消息数", width: 80 },
    { field: "lastMessageTime", title: "最后消息", width: 180, slots: { default: "lastTime" } },
    { title: "操作", width: 120, fixed: "right", slots: { default: "action" } }
  ]
});

const handleRefresh = () => xGrid.value?.commitProxy("query");

const handleViewMessages = async row => {
  currentConversation.value = row;
  messageVisible.value = true;
  messages.value = [];
  messagesLoading.value = true;
  try {
    const res = await getConversationMessagesApi(row.id);
    messages.value = res.data || [];
  } catch (e) {
    messages.value = [];
  } finally {
    messagesLoading.value = false;
  }
};
</script>

<style scoped>
.table-box {
  background: #fff;
  border-radius: 8px;
}
.message-list {
  max-height: 400px;
  overflow-y: auto;
}
.message-item {
  padding: 10px 12px;
  margin-bottom: 8px;
  border-radius: 8px;
  background: #f5f7fa;
}
.message-item.assistant {
  background: #ecf5ff;
}
.message-item.user {
  background: #f0f9eb;
}
.role-tag {
  font-size: 12px;
  color: #909399;
  margin-right: 8px;
}
.content {
  white-space: pre-wrap;
  word-break: break-word;
}
</style>
