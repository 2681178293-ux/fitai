package net.wanho.fitai.modules.ai.model.vo;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.io.Serializable;
import net.wanho.fitai.modules.ai.model.po.AiCostLog;

@Data
@Schema(description = "AI成本流水视图")
@AutoMapper(target = AiCostLog.class)
public class AiCostLogVO implements Serializable {

    @Schema(description = "成本ID")
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
