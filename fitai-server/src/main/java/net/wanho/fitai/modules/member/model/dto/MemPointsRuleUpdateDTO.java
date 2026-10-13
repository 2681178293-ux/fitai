package net.wanho.fitai.modules.member.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.io.Serializable;
import net.wanho.fitai.modules.member.model.po.MemPointsRule;

@Data
@Schema(description = "积分规则修改参数")
@AutoMapper(target = MemPointsRule.class)
public class MemPointsRuleUpdateDTO implements Serializable {

    @Schema(description = "规则ID")
    @NotNull(message = "规则ID不能为空")
    private Long ruleId;
    @Schema(description = "规则名称")
    private String ruleName;
    @Schema(description = "规则编码")
    private String ruleCode;
    @Schema(description = "事件 sign/train/order/review")
    private String eventType;
    @Schema(description = "积分值(可负)")
    private Integer points;
    @Schema(description = "日上限")
    private Integer dailyLimit;
    @Schema(description = "状态（0正常 1停用）")
    private String status;
}
