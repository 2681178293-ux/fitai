package net.wanho.fitai.modules.community.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;
import java.io.Serializable;
import net.wanho.fitai.modules.community.model.po.CmsModeration;

@Data
@Schema(description = "内容审核修改参数")
@AutoMapper(target = CmsModeration.class)
public class CmsModerationUpdateDTO implements Serializable {

    @Schema(description = "审核ID")
    @NotNull(message = "审核ID不能为空")
    private Long moderationId;
    @Schema(description = "对象类型 post/comment/ai_plan")
    private String targetType;
    @Schema(description = "对象ID")
    private Long targetId;
    @Schema(description = "内容快照")
    private String contentSnapshot;
    @Schema(description = "审核（0待审 1通过 2拒绝）")
    private String auditStatus;
    @Schema(description = "拒绝原因")
    private String auditReason;
    @Schema(description = "审核人")
    private Long auditorId;
    @Schema(description = "审核时间")
    private LocalDateTime auditTime;
}
