package net.wanho.fitai.modules.ai.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.io.Serializable;
import net.wanho.fitai.modules.ai.model.po.AiChatSession;

@Data
@Schema(description = "新增AI对话会话")
@AutoMapper(target = AiChatSession.class)
public class AiChatSessionAddDTO implements Serializable {

    @Schema(description = "Spring AI conversationId(UUID)")
    @NotBlank(message = "Spring AI conversationId不能为空")
    private String conversationId;
    @Schema(description = "用户ID")
    @NotNull(message = "用户ID不能为空")
    private Long customerId;
    @Schema(description = "模型ID")
    @NotNull(message = "模型ID不能为空")
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
