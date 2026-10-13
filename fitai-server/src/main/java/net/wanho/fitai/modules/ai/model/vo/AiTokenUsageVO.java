package net.wanho.fitai.modules.ai.model.vo;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import net.wanho.fitai.modules.ai.model.po.AiTokenUsage;

@Data
@Schema(description = "AI非会话Token消耗(审计/统计)视图")
@AutoMapper(target = AiTokenUsage.class)
public class AiTokenUsageVO implements Serializable {

    @Schema(description = "流水ID")
    private Long usageId;
    @Schema(description = "用户ID")
    private Long customerId;
    @Schema(description = "场景 plan/plan_modify/diet/review/knowledge")
    private String scene;
    @Schema(description = "模型编码")
    private String modelCode;
    @Schema(description = "Prompt Token")
    private Integer promptTokens;
    @Schema(description = "Completion Token")
    private Integer completionTokens;
    @Schema(description = "总Token")
    private Integer totalTokens;
    @Schema(description = "关联业务ID")
    private Long bizId;
}
