package net.wanho.fitai.modules.ai.model.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.wanho.fitai.infra.page.PageQuery;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "AI对话会话查询参数")
public class AiChatSessionQueryDTO extends PageQuery {

    @Schema(description = "模型编码(冗余)")
    private String modelCode;
    @Schema(description = "会话标题")
    private String sessionTitle;
    @Schema(description = "状态（0进行中 1已结束 2已归档）")
    private String status;
}
