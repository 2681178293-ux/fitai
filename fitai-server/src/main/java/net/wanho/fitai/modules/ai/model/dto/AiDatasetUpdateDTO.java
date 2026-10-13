package net.wanho.fitai.modules.ai.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.io.Serializable;
import net.wanho.fitai.modules.ai.model.po.AiDataset;

@Data
@Schema(description = "AI数据集修改参数")
@AutoMapper(target = AiDataset.class)
public class AiDatasetUpdateDTO implements Serializable {

    @Schema(description = "数据集ID")
    @NotNull(message = "数据集ID不能为空")
    private Long datasetId;
    @Schema(description = "名称")
    private String datasetName;
    @Schema(description = "编码")
    private String datasetCode;
    @Schema(description = "类型 eval=模型评测 rag=RAG质量评估")
    private String datasetType;
    @Schema(description = "行数(冗余)")
    private Integer rowCount;
    @Schema(description = "状态（0正常 1停用）")
    private String status;
}
