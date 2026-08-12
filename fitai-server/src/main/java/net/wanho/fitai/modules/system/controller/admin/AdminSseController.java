package net.wanho.fitai.modules.system.controller.admin;

import cn.dev33.satoken.exception.NotLoginException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.auth.helper.StpKit;
import net.wanho.fitai.modules.system.sse.NoticeSseHub;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@RestController
@RequestMapping("/admin/system/sse")
@RequiredArgsConstructor
@Tag(name = "管理端SSE")
public class AdminSseController {

    private final NoticeSseHub noticeSseHub;

    @GetMapping(value = "/connect", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "订阅公告推送")
    public SseEmitter connect() {
        try {
            StpKit.ADMIN.checkLogin();
        } catch (NotLoginException ex) {
            return reject("未登录或登录已过期");
        }
        return noticeSseHub.connect();
    }

    /** 鉴权失败：仍返回 SseEmitter，用 error 事件通知前端后关闭 */
    private static SseEmitter reject(String message) {
        SseEmitter emitter = new SseEmitter(5_000L);
        try {
            emitter.send(SseEmitter.event().name("error").data(message));
            emitter.complete();
        } catch (IOException ex) {
            emitter.completeWithError(ex);
        }
        return emitter;
    }
}
