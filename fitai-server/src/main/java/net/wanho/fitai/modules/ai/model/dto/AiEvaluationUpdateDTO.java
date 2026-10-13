package net.wanho.fitai.modules.ai.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.io.Serializable;
import net.wanho.fitai.modules.ai.model.po.AiEvaluation;

@Data
@Schema(description = "模型评测修改参数")
@AutoMapper(target = AiEvaluation.class)
public class AiEvaluationUpdateDTO implements Serializable {

    @Schema(description = "评测ID")
    @NotNull(message = "评测ID不能为空")
    private Long evaluationId;
    @Schema(description = "评测名称")
    private String evalName;
    @Schema(description = "模型ID")
    private Long modelId;
    @Schema(description = "数据集ID")
    private Long datasetId;
    @Schema(description = "指标结果JSON")
    private String metricsJson;
    @Schema(description = "状态（0进行中 1完成 2失败）")
    private String status;
}
