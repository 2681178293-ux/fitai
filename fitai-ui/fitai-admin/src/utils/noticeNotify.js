import { ElNotification } from "element-plus";

const recentIds = new Set();

/**
 * 公告发布后的右上角通知弹窗（与老师演示一致）
 * 同一 noticeId 2 秒内只弹一次，避免本地发布与 SSE 重复弹出
 */
export function showNoticeNotification(payload = {}) {
  const id = payload.noticeId != null ? Number(payload.noticeId) : null;
  if (id != null) {
    if (recentIds.has(id)) return;
    recentIds.add(id);
    setTimeout(() => recentIds.delete(id), 2000);
  }

  const title = payload.noticeTitle || payload.title || "新消息";
  const message = payload.noticeContent || payload.content || payload.message || "";
  ElNotification({
    title,
    message,
    type: "info",
    position: "top-right",
    duration: 4500,
    showClose: true
  });
}
