package net.wanho.fitai.modules.ai.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;
import java.io.Serializable;
import net.wanho.fitai.modules.ai.model.po.AiChatSession;

@Data
@Schema(description = "AI对话会话修改参数")
@AutoMapper(target = AiChatSession.class)
public class AiChatSessionUpdateDTO implements Serializable {

    @Schema(description = "会话ID")
    @NotNull(message = "会话ID不能为空")
    private Long sessionId;
    @Schema(description = "Spring AI conversationId(UUID)")
    private String conversationId;
    @Schema(description = "用户ID")
    private Long customerId;
    @Schema(description = "模型ID")
    private Long modelId;
    @Schema(description = "模型编码(冗余)")
    private String modelCode;
    @Schema(description = "会话标题")
    private String sessionTitle;
    @Schema(description = "场景")
    private String scene;
    @Schema(description = "消息数(冗余)")
    private Integer messageCount;
    @Schema(description = "总Token(冗余)")
    private Integer totalTokens;
    @Schema(description = "最后消息时间")
    private LocalDateTime lastMessageTime;
    @Schema(description = "状态（0进行中 1已结束 2已归档）")
    private String status;
}
