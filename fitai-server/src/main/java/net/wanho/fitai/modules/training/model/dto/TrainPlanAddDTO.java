package net.wanho.fitai.modules.training.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.io.Serializable;
import net.wanho.fitai.modules.training.model.po.TrainPlan;

@Data
@Schema(description = "新增训练计划")
@AutoMapper(target = TrainPlan.class)
public class TrainPlanAddDTO implements Serializable {

    @Schema(description = "计划名称")
    @NotBlank(message = "计划名称不能为空")
    private String planName;
    @Schema(description = "用户ID(个人计划)")
    private Long customerId;
    @Schema(description = "教练ID")
    private Long coachId;
    @Schema(description = "类型（1系统 2个人 3AI生成）")
    private String planType;
    @Schema(description = "目标 fat_loss/muscle/keep")
    private String goal;
    @Schema(description = "周期周数")
    private Integer weeks;
    @Schema(description = "每周训练天")
    private Integer daysPerWeek;
    @Schema(description = "难度 1入门 2进阶 3高级")
    private Integer fitnessLevel;
    @Schema(description = "开始日期")
    private LocalDate startDate;
    @Schema(description = "结束日期")
    private LocalDate endDate;
    @Schema(description = "AI计划JSON(可选冗余)")
    private String planJson;
    @Schema(description = "AI会话ID")
    private Long aiSessionId;
    @Schema(description = "审核（0待审 1通过 2拒绝）")
    private String auditStatus;
    @Schema(description = "状态（0正常 1停用）")
    private String status;
    @Schema(description = "C端生命周期（0草稿 1进行中 2已完成 3已放弃）")
    private String lifecycleStatus;
    @Schema(description = "AI生成态（0生成中 1完成 2失败）")
    private String genStatus;
    @Schema(description = "AI生成失败原因")
    private String genErrorMsg;
}
