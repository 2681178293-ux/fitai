package net.wanho.fitai.modules.ai.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.io.Serializable;
import net.wanho.fitai.modules.ai.model.po.AiDatasetRow;

@Data
@Schema(description = "数据集行修改参数")
@AutoMapper(target = AiDatasetRow.class)
public class AiDatasetRowUpdateDTO implements Serializable {

    @Schema(description = "行ID")
    @NotNull(message = "行ID不能为空")
    private Long rowId;
    @Schema(description = "数据集ID")
    private Long datasetId;
    @Schema(description = "输入")
    private String inputText;
    @Schema(description = "期望输出")
    private String outputText;
    @Schema(description = "元数据")
    private String metadataJson;
}
