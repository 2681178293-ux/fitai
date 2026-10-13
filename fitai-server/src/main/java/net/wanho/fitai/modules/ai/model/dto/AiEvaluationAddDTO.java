package net.wanho.fitai.modules.ai.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import net.wanho.fitai.modules.ai.model.po.AiEvaluation;

@Data
@Schema(description = "新增模型评测")
@AutoMapper(target = AiEvaluation.class)
public class AiEvaluationAddDTO implements Serializable {

    @Schema(description = "评测名称")
    @NotBlank(message = "评测名称不能为空")
    private String evalName;
    @Schema(description = "模型ID")
    @NotNull(message = "模型ID不能为空")
    private Long modelId;
    @Schema(description = "数据集ID")
    private Long datasetId;
    @Schema(description = "指标结果JSON")
    private String metricsJson;
    @Schema(description = "状态（0进行中 1完成 2失败）")
    private String status;
}
