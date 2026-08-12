package net.wanho.fitai.modules.system.model.dto;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import net.wanho.fitai.modules.system.model.po.SysNoticeRead;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Schema(description = "修改SysNoticeRead参数")
@AutoMapper(target = SysNoticeRead.class)
public class SysNoticeReadUpdateDTO implements Serializable {
    @Schema(description = "readId")
    @NotNull(message = "readId不能为空")
    private Long readId;
    @Schema(description = "公告ID")
    private Long noticeId;
    @Schema(description = "用户ID")
    private Long customerId;
    @Schema(description = "管理员ID")
    private Long userID;
    @Schema(description = "阅读时间")
    private LocalDateTime readTime;

}
