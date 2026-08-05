package net.wanho.fitai.modules.system.model.dto;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import net.wanho.fitai.modules.system.model.po.SysNotice;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.LocalDate;

@Data
@Schema(description = "修改SysNotice参数")
@AutoMapper(target = SysNotice.class)
public class SysNoticeUpdateDTO implements Serializable {
    @Schema(description = "noticeId")
    @NotNull(message = "noticeId不能为空")
    private Long noticeId;
    @Schema(description = "公告标题")
    private String noticeTitle;
    @Schema(description = "公告类型")
    private String noticeType;
    @Schema(description = "公告内容")
    private String noticeContent;
    @Schema(description = "目标类型")
    private String targetType;
    @Schema(description = "目标ID列表")
    private String targetIds;
    @Schema(description = "推送渠道")
    private String pushChannel;
    @Schema(description = "状态")
    private String status;
    @Schema(description = "发布时间")
    private LocalDateTime publishTime;

}
