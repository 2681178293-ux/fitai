package net.wanho.fitai.modules.ai.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.io.Serializable;
import net.wanho.fitai.modules.ai.model.po.AiChatMessage;

@Data
@Schema(description = "AI对话消息修改参数")
@AutoMapper(target = AiChatMessage.class)
public class AiChatMessageUpdateDTO implements Serializable {

    @Schema(description = "消息ID")
    @NotNull(message = "消息ID不能为空")
    private Long messageId;
    @Schema(description = "会话ID")
    private Long sessionId;
    @Schema(description = "conversationId(冗余)")
    private String conversationId;
    @Schema(description = "角色 user/assistant/system/tool")
    private String role;
    @Schema(description = "消息内容")
    private String content;
    @Schema(description = "Prompt Token")
    private Integer promptTokens;
    @Schema(description = "Completion Token")
    private Integer completionTokens;
    @Schema(description = "总Token")
    private Integer totalTokens;
    @Schema(description = "结束原因 stop/length/tool_calls")
    private String finishReason;
    @Schema(description = "工具调用")
    private String toolCallsJson;
    @Schema(description = "RAG检索上下文")
    private String ragContextJson;
    @Schema(description = "模型编码(冗余)")
    private String modelCode;
    @Schema(description = "耗时毫秒")
    private Integer latencyMs;
    @Schema(description = "消息状态：0 成功 1 失败")
    private String status;
    @Schema(description = "失败详情（超时/限流/异常堆栈摘要）")
    private String errorMsg;
}
