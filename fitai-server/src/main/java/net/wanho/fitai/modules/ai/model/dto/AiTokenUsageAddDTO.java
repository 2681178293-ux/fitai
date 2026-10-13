package net.wanho.fitai.modules.ai.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import net.wanho.fitai.modules.ai.model.po.AiTokenUsage;

@Data
@Schema(description = "新增AI非会话Token消耗(审计/统计)")
@AutoMapper(target = AiTokenUsage.class)
public class AiTokenUsageAddDTO implements Serializable {

    @Schema(description = "用户ID")
    @NotNull(message = "用户ID不能为空")
    private Long customerId;
    @Schema(description = "场景 plan/plan_modify/diet/review/knowledge")
    @NotBlank(message = "场景 plan不能为空")
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
