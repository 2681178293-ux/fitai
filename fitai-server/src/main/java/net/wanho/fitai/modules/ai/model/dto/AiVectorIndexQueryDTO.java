package net.wanho.fitai.modules.ai.model.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.wanho.fitai.infra.page.PageQuery;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "Redis向量索引注册查询参数")
public class AiVectorIndexQueryDTO extends PageQuery {

    @Schema(description = "Redis索引名")
    private String indexName;
}
