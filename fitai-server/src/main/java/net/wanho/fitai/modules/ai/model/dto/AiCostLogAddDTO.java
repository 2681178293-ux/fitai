package net.wanho.fitai.modules.ai.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.io.Serializable;
import net.wanho.fitai.modules.ai.model.po.AiCostLog;

@Data
@Schema(description = "新增AI成本流水")
@AutoMapper(target = AiCostLog.class)
public class AiCostLogAddDTO implements Serializable {

    @Schema(description = "用户ID")
    private Long customerId;
    @Schema(description = "会话ID")
    private Long sessionId;
    @Schema(description = "模型")
    @NotBlank(message = "模型不能为空")
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
