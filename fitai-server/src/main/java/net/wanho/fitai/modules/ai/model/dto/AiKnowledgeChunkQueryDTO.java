package net.wanho.fitai.modules.ai.model.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.wanho.fitai.infra.page.PageQuery;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "知识库分片(Redis向量元数据)查询参数")
public class AiKnowledgeChunkQueryDTO extends PageQuery {

    @Schema(description = "Redis文档键 fitai:vec:{kb}:{doc}:{idx}")
    private String redisDocKey;
    @Schema(description = "Redis索引名(冗余)")
    private String redisIndexName;
}
