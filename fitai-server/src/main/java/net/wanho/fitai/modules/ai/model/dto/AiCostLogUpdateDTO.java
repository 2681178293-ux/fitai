package net.wanho.fitai.modules.ai.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;
import java.io.Serializable;
import net.wanho.fitai.modules.ai.model.po.AiCostLog;

@Data
@Schema(description = "AI成本流水修改参数")
@AutoMapper(target = AiCostLog.class)
public class AiCostLogUpdateDTO implements Serializable {

    @Schema(description = "成本ID")
    @NotNull(message = "成本ID不能为空")
    private Long costId;
    @Schema(description = "用户ID")
    private Long customerId;
    @Schema(description = "会话ID")
    private Long sessionId;
    @Schema(description = "模型")
    private String modelCode;
    @Schema(description = "Prompt Token")
    private Integer promptTokens;
    @Schema(description = "Completion Token")
    private Integer completionTokens;
    @Schema(description = "总Token")
    private Integer totalTokens;
    @Schema(description = "费用元")
    private BigDecimal costAmount;
    @Schema(description = "业务场景")
    private String bizScene;
}
