package net.wanho.fitai.modules.system.model.dto;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import net.wanho.fitai.modules.system.model.po.SysNoticeRead;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Schema(description = "新增SysNoticeRead参数")
@AutoMapper(target = SysNoticeRead.class)
public class SysNoticeReadAddDTO implements Serializable {
    @Schema(description = "公告ID")
    @NotNull(message = "公告ID不能为空")
    private Long noticeId;
    @Schema(description = "用户ID")
    @NotNull(message = "用户ID不能为空")
    private Long customerId;
    @Schema(description = "阅读时间")
    private LocalDateTime readTime;

}
