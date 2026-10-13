package net.wanho.fitai.modules.system.sse;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class NoticeSseConnectionManager {

    //ConcurrentHashMap
    //CopyOnWriteArrayList
    //computeIfAbsent  原子性
    //sseEmitterMap.remove(userId,list)

    private  final ConcurrentHashMap<Long, CopyOnWriteArrayList<SseEmitter>> sseEmitterMap = new ConcurrentHashMap<>();
    /** 30 分钟超时，前端断线后自动重连 */
    private  final long TIMEOUT_MS = 30 * 60 * 1000L;

    /** 为用户建立 SSE 连接并登记到内存 Map */
    public SseEmitter connect(Long userId) {

        // 创建一个SseEmitter对象，用于发送消息给用户
        SseEmitter sseEmitter = new SseEmitter(TIMEOUT_MS);
        sseEmitterMap.computeIfAbsent(userId, k -> new CopyOnWriteArrayList<>()).add(sseEmitter);


        sseEmitter.onCompletion(() -> {
            removeEmitter(userId, sseEmitter);
        });
        sseEmitter.onTimeout(() -> {
            removeEmitter(userId, sseEmitter);
        });
        sseEmitter.onError((ex) -> {
            removeEmitter(userId, sseEmitter);
        });

        try {
            sseEmitter.send(SseEmitter.event().name("connected").data(userId ));
        } catch (IOException e) {
            removeEmitter(userId, sseEmitter);
            sseEmitter.completeWithError(e);
        }
        // 返回当前用户的连接
        return sseEmitter;
    }

    /** 移除失效连接；列表空时再按条件删掉 Map 中的 key */
    private void removeEmitter(Long userId, SseEmitter sseEmitter) {
        CopyOnWriteArrayList<SseEmitter> list = sseEmitterMap.get(userId);
        if (list == null) {
            return;
        }
        list.remove(sseEmitter);
        if (list.isEmpty()) {
            sseEmitterMap.remove(userId, list);
        }
    }

    /** 向指定用户的在线连接推送公告事件 */
    public void send(List<Long> userIds, Map<String, Object> payload) {
        if (userIds == null || userIds.isEmpty()) {
            return;
        }
        for (Long userId : userIds) {
            CopyOnWriteArrayList<SseEmitter> sseEmitters = sseEmitterMap.get(userId);
            // 该用户当前没有 SSE 连接，跳过即可，不能对 null 做 for-each
            if (sseEmitters == null || sseEmitters.isEmpty()) {
                continue;
            }
            for (SseEmitter emitter : sseEmitters) {
                try {
                    emitter.send(SseEmitter.event().name("notice").data(payload));
                } catch (IOException e) {
                    removeEmitter(userId, emitter);
                    emitter.completeWithError(e);
                }
            }
        }
    }

}
