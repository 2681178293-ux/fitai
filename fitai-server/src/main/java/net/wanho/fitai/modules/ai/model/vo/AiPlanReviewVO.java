package net.wanho.fitai.modules.ai.model.vo;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;
import java.io.Serializable;
import net.wanho.fitai.modules.ai.model.po.AiPlanReview;

@Data
@Schema(description = "AI方案审核视图")
@AutoMapper(target = AiPlanReview.class)
public class AiPlanReviewVO implements Serializable {

    @Schema(description = "审核ID")
    private Long reviewId;
    @Schema(description = "方案类型 train/diet")
    private String planType;
    @Schema(description = "方案ID")
    private Long planId;
    @Schema(description = "用户ID")
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
