package net.wanho.fitai.modules.ai.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import net.wanho.fitai.modules.ai.model.po.AiChatMessage;

@Data
@Schema(description = "新增AI对话消息")
@AutoMapper(target = AiChatMessage.class)
public class AiChatMessageAddDTO implements Serializable {

    @Schema(description = "会话ID")
    @NotNull(message = "会话ID不能为空")
    private Long sessionId;
    @Schema(description = "conversationId(冗余)")
    @NotBlank(message = "conversationId不能为空")
    private String conversationId;
    @Schema(description = "角色 user/assistant/system/tool")
    @NotBlank(message = "角色 user不能为空")
    private String role;
    @Schema(description = "消息内容")
    @NotBlank(message = "消息内容不能为空")
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
