package net.wanho.fitai.modules.ai.model.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.wanho.fitai.infra.page.PageQuery;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "AI对话消息查询参数")
public class AiChatMessageQueryDTO extends PageQuery {

    @Schema(description = "模型编码(冗余)")
    private String modelCode;
    @Schema(description = "消息状态：0 成功 1 失败")
    private String status;
}
