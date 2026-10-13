package net.wanho.fitai.modules.ai.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import net.wanho.fitai.modules.ai.model.po.AiTrace;

@Data
@Schema(description = "新增AI链路追踪")
@AutoMapper(target = AiTrace.class)
public class AiTraceAddDTO implements Serializable {

    @Schema(description = "链路UUID")
    @NotBlank(message = "链路UUID不能为空")
    private String traceUuid;
    @Schema(description = "会话ID")
    private Long sessionId;
    @Schema(description = "conversationId")
    private String conversationId;
    @Schema(description = "用户ID")
    private Long customerId;
    @Schema(description = "模型")
    private String modelCode;
    @Schema(description = "业务场景 chat/train_plan 等")
    private String bizScene;
    @Schema(description = "Agent 键 coach/diet/rehab/cs")
    private String agentCode;
    @Schema(description = "输入 Token")
    private Integer promptTokens;
    @Schema(description = "输出 Token")
    private Integer completionTokens;
    @Schema(description = "工具调用次数")
    private Integer toolCallCount;
    @Schema(description = "链路步骤 retrieve/prompt/llm/output")
    @NotBlank(message = "链路步骤 retrieve不能为空")
    private String stepsJson;
    @Schema(description = "总耗时")
    private Integer totalLatencyMs;
    @Schema(description = "状态（0成功 1失败）")
    private String status;
    @Schema(description = "错误信息")
    private String errorMsg;
}
