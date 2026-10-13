package net.wanho.fitai.modules.member.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import net.wanho.fitai.modules.member.model.po.MemPointsRule;

@Data
@Schema(description = "新增积分规则")
@AutoMapper(target = MemPointsRule.class)
public class MemPointsRuleAddDTO implements Serializable {

    @Schema(description = "规则名称")
    @NotBlank(message = "规则名称不能为空")
    private String ruleName;
    @Schema(description = "规则编码")
    @NotBlank(message = "规则编码不能为空")
    private String ruleCode;
    @Schema(description = "事件 sign/train/order/review")
    @NotBlank(message = "事件 sign不能为空")
    private String eventType;
    @Schema(description = "积分值(可负)")
    @NotNull(message = "积分值不能为空")
    private Integer points;
    @Schema(description = "日上限")
    private Integer dailyLimit;
    @Schema(description = "状态（0正常 1停用）")
    private String status;
}
