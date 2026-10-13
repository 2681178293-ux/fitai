package net.wanho.fitai.modules.ai.model.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.wanho.fitai.infra.page.PageQuery;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "知识库查询参数")
public class AiKnowledgeQueryDTO extends PageQuery {

    @Schema(description = "知识库名称")
    private String kbName;
    @Schema(description = "编码")
    private String kbCode;
    @Schema(description = "Redis索引名 idx:fitai:kb:{code}")
    private String redisIndexName;
    @Schema(description = "状态（0正常 1停用）")
    private String status;
}
