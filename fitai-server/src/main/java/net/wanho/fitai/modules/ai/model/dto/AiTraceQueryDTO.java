package net.wanho.fitai.modules.ai.model.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.wanho.fitai.infra.page.PageQuery;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "AI链路追踪查询参数")
public class AiTraceQueryDTO extends PageQuery {

    @Schema(description = "模型")
    private String modelCode;
    @Schema(description = "Agent 键 coach/diet/rehab/cs")
    private String agentCode;
    @Schema(description = "状态（0成功 1失败）")
    private String status;
}
