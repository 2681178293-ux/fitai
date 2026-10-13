package net.wanho.fitai.modules.ai.model.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.wanho.fitai.infra.page.PageQuery;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "知识库文档查询参数")
public class AiKnowledgeDocQueryDTO extends PageQuery {

    @Schema(description = "文档标题")
    private String docTitle;
    @Schema(description = "状态（0正常 1停用）")
    private String status;
}
