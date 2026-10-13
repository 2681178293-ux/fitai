package net.wanho.fitai.modules.ai.model.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.wanho.fitai.infra.page.PageQuery;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "RAG质量评估查询参数")
public class AiRagEvalQueryDTO extends PageQuery {

    @Schema(description = "评估名称")
    private String evalName;
}
