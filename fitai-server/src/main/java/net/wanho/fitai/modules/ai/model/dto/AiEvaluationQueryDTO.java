package net.wanho.fitai.modules.ai.model.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.wanho.fitai.infra.page.PageQuery;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "模型评测查询参数")
public class AiEvaluationQueryDTO extends PageQuery {

    @Schema(description = "评测名称")
    private String evalName;
    @Schema(description = "状态（0进行中 1完成 2失败）")
    private String status;
}
