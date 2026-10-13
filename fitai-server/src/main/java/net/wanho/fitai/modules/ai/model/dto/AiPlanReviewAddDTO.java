package net.wanho.fitai.modules.ai.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.io.Serializable;
import net.wanho.fitai.modules.ai.model.po.AiPlanReview;

@Data
@Schema(description = "新增AI方案审核")
@AutoMapper(target = AiPlanReview.class)
public class AiPlanReviewAddDTO implements Serializable {

    @Schema(description = "方案类型 train/diet")
    @NotBlank(message = "方案类型 train不能为空")
    private String planType;
    @Schema(description = "方案ID")
    @NotNull(message = "方案ID不能为空")
    private Long planId;
    @Schema(description = "用户ID")
    @NotNull(message = "用户ID不能为空")
    private Long customerId;
    @Schema(description = "AI会话ID")
    private Long sessionId;
    @Schema(description = "方案快照JSON")
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
