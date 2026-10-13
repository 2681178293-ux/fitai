package net.wanho.fitai.modules.system.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "系统通知已读")
public class SysNoticeReadDTO {

    @Schema(description = "通知ID")
    private List<Long> noticeIds;
}
