package net.wanho.fitai.modules.system.model.vo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import net.wanho.fitai.modules.system.model.po.SysNoticeRead;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.LocalDate;

@Data
@Schema(description = "SysNoticeRead视图")
@AutoMapper(target = SysNoticeRead.class)
public class SysNoticeReadVO implements Serializable {
    @Schema(description = "已读ID")
    private Long readId;
    @Schema(description = "公告ID")
    private Long noticeId;
    @Schema(description = "用户ID")
    private Long customerId;
    @Schema(description = "阅读时间")
    private LocalDateTime readTime;

}
