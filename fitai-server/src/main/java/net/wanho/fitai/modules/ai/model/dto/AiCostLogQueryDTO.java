package net.wanho.fitai.modules.ai.model.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.wanho.fitai.infra.page.PageQuery;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "AI成本流水查询参数")
public class AiCostLogQueryDTO extends PageQuery {

    @Schema(description = "模型")
    private String modelCode;
}
