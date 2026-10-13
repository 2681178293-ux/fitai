package net.wanho.fitai.modules.system.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.auth.helper.StpKit;
import net.wanho.fitai.modules.system.sse.NoticeSseConnectionManager;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/admin/system/sse")
@RequiredArgsConstructor
@Tag(name = "管理端SSE")
public class AdminSseController {

    private final NoticeSseConnectionManager connectionManager;

    @GetMapping(value = "/connect", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "订阅公告推送")
    public SseEmitter connect() {
        StpKit.ADMIN.checkLogin();
        return connectionManager.connect(StpKit.ADMIN.getLoginIdAsLong());
    }

}