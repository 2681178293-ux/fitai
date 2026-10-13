package net.wanho.fitai.modules.ai.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import net.wanho.fitai.modules.ai.model.po.AiDatasetRow;

@Data
@Schema(description = "新增数据集行")
@AutoMapper(target = AiDatasetRow.class)
public class AiDatasetRowAddDTO implements Serializable {

    @Schema(description = "数据集ID")
    @NotNull(message = "数据集ID不能为空")
    private Long datasetId;
    @Schema(description = "输入")
    @NotBlank(message = "输入不能为空")
    private String inputText;
    @Schema(description = "期望输出")
    private String outputText;
    @Schema(description = "元数据")
    private String metadataJson;
}
