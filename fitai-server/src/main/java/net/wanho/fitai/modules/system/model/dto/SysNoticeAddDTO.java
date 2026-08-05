package net.wanho.fitai.modules.system.model.dto;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import net.wanho.fitai.modules.system.model.po.SysNotice;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Schema(description = "新增SysNotice参数")
@AutoMapper(target = SysNotice.class)
public class SysNoticeAddDTO implements Serializable {
    @Schema(description = "公告标题")
    @NotBlank(message = "公告标题不能为空")
    private String noticeTitle;
    @Schema(description = "公告类型")
    @NotBlank(message = "公告类型不能为空")
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
