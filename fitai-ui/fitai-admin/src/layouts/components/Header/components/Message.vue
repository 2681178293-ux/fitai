<template>
  <div class="message">
    <el-popover placement="bottom" :width="320" trigger="click" @show="loadNotices">
      <template #reference>
        <el-badge :value="unreadCount || undefined" :hidden="!unreadCount" class="item">
          <el-icon class="toolBar-icon" :size="20"><Bell /></el-icon>
        </el-badge>
      </template>
      <el-tabs v-model="activeName">
        <el-tab-pane name="announce">
          <template #label>
            <span class="tab-label">
              公告
              <el-badge
                v-if="announceUnread"
                :value="announceUnread"
                class="tab-badge"
              />
            </span>
          </template>
          <div v-if="announceList.length" class="message-list">
            <div
              v-for="item in announceList"
              :key="item.noticeId"
              class="message-item"
              :class="{ unread: !isRead(item.noticeId) }"
              @click="handleClick(item)"
            >
              <div class="message-icon announce">
                <el-icon :size="18"><Notification /></el-icon>
              </div>
              <div class="message-content">
                <span class="message-title">{{ item.noticeTitle }}</span>
                <span class="message-date">{{ formatTime(item.publishTime || item.createTime) }}</span>
              </div>
            </div>
          </div>
          <div v-else class="message-empty">
            <div>暂无公告</div>
          </div>
        </el-tab-pane>
        <el-tab-pane name="push">
          <template #label>
            <span class="tab-label">
              推送
              <el-badge
                v-if="pushUnread"
                :value="pushUnread"
                class="tab-badge"
              />
            </span>
          </template>
          <div v-if="pushList.length" class="message-list">
            <div
              v-for="item in pushList"
              :key="item.noticeId"
              class="message-item"
              :class="{ unread: !isRead(item.noticeId) }"
              @click="handleClick(item)"
            >
              <div class="message-icon push">
                <el-icon :size="18"><Promotion /></el-icon>
              </div>
              <div class="message-content">
                <span class="message-title">{{ item.noticeTitle }}</span>
                <span class="message-date">{{ formatTime(item.publishTime || item.createTime) }}</span>
              </div>
            </div>
          </div>
          <div v-else class="message-empty">
            <div>暂无推送</div>
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-popover>
  </div>
</template>

<script setup>
import { computed, onBeforeUnmount, onMounted, ref } from "vue";
import { Bell, Notification, Promotion } from "@element-plus/icons-vue";
import { ElMessageBox } from "element-plus";
import {
  getNoticePageApi,
  getMyNoticeReadIdsApi,
  readNoticeBatchApi
} from "@/api/modules/biz-ext";
import mittBus from "@/utils/mittBus";

/** 类型：1 公告，2 推送（与 sys_notice / 字典一致） */
const TYPE_ANNOUNCE = "1";
const TYPE_PUSH = "2";

const activeName = ref("announce");
const noticeList = ref([]);
const readIds = ref([]);
const loading = ref(false);

const announceList = computed(() =>
  noticeList.value.filter(n => String(n.noticeType) === TYPE_ANNOUNCE)
);
const pushList = computed(() =>
  noticeList.value.filter(n => String(n.noticeType) === TYPE_PUSH)
);
const announceUnread = computed(
  () => announceList.value.filter(n => !isRead(n.noticeId)).length
);
const pushUnread = computed(
  () => pushList.value.filter(n => !isRead(n.noticeId)).length
);
const unreadCount = computed(() => announceUnread.value + pushUnread.value);

const isRead = id => readIds.value.includes(Number(id));

const formatTime = val => {
  if (!val) return "";
  const s = String(val).replace("T", " ");
  return s.length > 19 ? s.slice(0, 19) : s;
};

const loadNotices = async () => {
  if (loading.value) return;
  loading.value = true;
  try {
    const [listRes, readRes] = await Promise.all([
      getNoticePageApi({ pageNum: 1, pageSize: 50, status: "1" }),
      getMyNoticeReadIdsApi().catch(() => ({ data: [] }))
    ]);
    const page = listRes.data || {};
    noticeList.value = page.records || [];
    readIds.value = (readRes.data || []).map(id => Number(id));
  } catch (e) {
    noticeList.value = [];
  } finally {
    loading.value = false;
  }
};

const handleClick = async item => {
  await ElMessageBox.alert(item.noticeContent || "暂无内容", item.noticeTitle || "消息详情", {
    confirmButtonText: "知道了"
  }).catch(() => {});
  if (isRead(item.noticeId)) return;
  try {
    await readNoticeBatchApi({ noticeIds: [item.noticeId] });
    readIds.value = [...readIds.value, Number(item.noticeId)];
  } catch (e) {
    // ignore
  }
};

onMounted(() => {
  loadNotices();
  mittBus.on("noticePublished", loadNotices);
});

onBeforeUnmount(() => {
  mittBus.off("noticePublished", loadNotices);
});
</script>

<style scoped>
.tab-label {
  position: relative;
  display: inline-flex;
  align-items: center;
  padding-right: 10px;
}
.tab-badge {
  position: absolute;
  top: -8px;
  right: -10px;
  transform: scale(0.85);
  transform-origin: left center;
}
.tab-badge :deep(.el-badge__content) {
  border: none;
}
.message-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 220px;
  color: var(--el-text-color-secondary);
  font-size: 13px;
}
.message-list {
  display: flex;
  flex-direction: column;
  max-height: 320px;
  overflow-y: auto;
}
.message-item {
  display: flex;
  align-items: center;
  padding: 14px 0;
  border-bottom: 1px solid var(--el-border-color-light);
  cursor: pointer;
}
.message-item:last-child {
  border: none;
}
.message-item:hover .message-title {
  color: var(--el-color-primary);
}
.message-item.unread .message-title {
  font-weight: 600;
}
.message-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  margin: 0 14px 0 4px;
  border-radius: 50%;
  flex-shrink: 0;
}
.message-icon.announce {
  color: #409eff;
  background: rgba(64, 158, 255, 0.12);
}
.message-icon.push {
  color: #67c23a;
  background: rgba(103, 194, 58, 0.12);
}
.message-content {
  display: flex;
  flex-direction: column;
  min-width: 0;
}
.message-title {
  margin-bottom: 4px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.message-date {
  font-size: 12px;
  color: var(--el-text-color-secondary);
}
</style>
