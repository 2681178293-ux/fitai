import { onBeforeUnmount, watch } from "vue";
import { useUserStore } from "@/stores/modules/user";
import mittBus from "@/utils/mittBus";
import { showNoticeNotification } from "@/utils/noticeNotify";

/**
 * 订阅管理端公告 SSE：发布后右上角弹出通知
 * 使用 fetch + Authorization，与现有接口鉴权一致（原生 EventSource 无法带自定义头）
 */
export function useNoticeSse() {
  const userStore = useUserStore();
  let abortController = null;
  let reconnectTimer = null;
  let closed = false;

  const clearReconnect = () => {
    if (reconnectTimer) {
      clearTimeout(reconnectTimer);
      reconnectTimer = null;
    }
  };

  const disconnect = () => {
    closed = true;
    clearReconnect();
    if (abortController) {
      abortController.abort();
      abortController = null;
    }
  };

  const handlePayload = raw => {
    let data = raw;
    if (typeof raw === "string") {
      try {
        data = JSON.parse(raw);
      } catch (e) {
        return;
      }
    }
    if (!data || typeof data !== "object") return;
    showNoticeNotification(data);
    mittBus.emit("noticePublished", data);
  };

  const parseSseChunk = (buffer, onEvent) => {
    const parts = buffer.split("\n\n");
    const rest = parts.pop() || "";
    for (const part of parts) {
      if (!part.trim()) continue;
      let eventName = "message";
      const dataLines = [];
      for (const line of part.split("\n")) {
        if (line.startsWith("event:")) eventName = line.slice(6).trim();
        else if (line.startsWith("data:")) dataLines.push(line.slice(5).trim());
      }
      if (dataLines.length) onEvent(eventName, dataLines.join("\n"));
    }
    return rest;
  };

  const connect = async () => {
    clearReconnect();
    if (!userStore.token || closed) return;

    if (abortController) abortController.abort();
    abortController = new AbortController();

    const base = (import.meta.env.VITE_API_URL || "").replace(/\/$/, "");
    const url = `${base}/admin/system/sse/connect`;

    try {
      const res = await fetch(url, {
        method: "GET",
        headers: {
          Authorization: userStore.token,
          Accept: "text/event-stream"
        },
        credentials: "include",
        signal: abortController.signal
      });

      if (!res.ok || !res.body) {
        scheduleReconnect();
        return;
      }

      const reader = res.body.getReader();
      const decoder = new TextDecoder("utf-8");
      let buffer = "";

      while (!closed) {
        const { done, value } = await reader.read();
        if (done) break;
        buffer += decoder.decode(value, { stream: true });
        buffer = parseSseChunk(buffer, (eventName, data) => {
          if (eventName === "notice") handlePayload(data);
        });
      }

      if (!closed) scheduleReconnect();
    } catch (e) {
      if (e?.name === "AbortError") return;
      if (!closed) scheduleReconnect();
    }
  };

  const scheduleReconnect = () => {
    clearReconnect();
    if (closed || !userStore.token) return;
    reconnectTimer = setTimeout(() => {
      closed = false;
      connect();
    }, 3000);
  };

  const start = () => {
    closed = false;
    connect();
  };

  watch(
    () => userStore.token,
    token => {
      if (token) {
        closed = false;
        connect();
      } else {
        disconnect();
        closed = false;
      }
    },
    { immediate: true }
  );

  onBeforeUnmount(disconnect);

  return { start, disconnect };
}
