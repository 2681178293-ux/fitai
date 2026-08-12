package net.wanho.fitai.modules.system.sse;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 管理端公告 SSE 连接池：在线管理员订阅，发布时广播。
 */
@Slf4j
@Component
public class NoticeSseHub {

    /** 30 分钟超时，前端断线后自动重连 */
    private static final long TIMEOUT_MS = 30 * 60 * 1000L;

    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    public SseEmitter connect() {
        SseEmitter emitter = new SseEmitter(TIMEOUT_MS);
        emitters.add(emitter);
        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
        emitter.onError(ex -> emitters.remove(emitter));
        try {
            emitter.send(SseEmitter.event().name("connected").data("ok"));
        } catch (IOException ex) {
            emitters.remove(emitter);
            emitter.completeWithError(ex);
        }
        return emitter;
    }

    /**
     * 向所有在线管理端推送公告摘要。
     * <p>必须用 APPLICATION_JSON 推 Map/对象，不要先写成 JSON 字符串再 data(String)，
     * 否则会被二次序列化，前端 JSON.parse 一次后字段错乱。
     */
    public void broadcast(Map<String, Object> payload) {
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event()
                        .name("notice")
                        .data(payload, MediaType.APPLICATION_JSON));
            } catch (Exception ex) {
                emitters.remove(emitter);
                try {
                    emitter.complete();
                } catch (Exception ignored) {
                    // ignore
                }
                log.debug("SSE 推送失败，已移除连接: {}", ex.getMessage());
            }
        }
    }
}
