package net.wanho.fitai.modules.diet.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;
import java.io.Serializable;
import net.wanho.fitai.modules.diet.model.po.DietPlan;

@Data
@Schema(description = "饮食方案修改参数")
@AutoMapper(target = DietPlan.class)
public class DietPlanUpdateDTO implements Serializable {

    @Schema(description = "方案ID")
    @NotNull(message = "方案ID不能为空")
    private Long planId;
    @Schema(description = "方案名称")
    private String planName;
    @Schema(description = "用户ID")
    private Long customerId;
    @Schema(description = "类型（1系统 2个人 3AI）")
    private String planType;
    @Schema(description = "目标热量")
    private BigDecimal targetCalories;
    @Schema(description = "宏量营养素目标JSON")
    private String macrosJson;
    @Schema(description = "AI方案JSON(可选冗余)")
    private String planJson;
    @Schema(description = "AI会话ID")
    private Long aiSessionId;
    @Schema(description = "审核（0待审 1通过 2拒绝）")
    private String auditStatus;
    @Schema(description = "状态（0正常 1停用）")
    private String status;
}
