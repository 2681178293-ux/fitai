package net.wanho.fitai.modules.ai.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import net.wanho.fitai.modules.ai.model.po.AiDataset;

@Data
@Schema(description = "新增AI数据集")
@AutoMapper(target = AiDataset.class)
public class AiDatasetAddDTO implements Serializable {

    @Schema(description = "名称")
    @NotBlank(message = "名称不能为空")
    private String datasetName;
    @Schema(description = "编码")
    @NotBlank(message = "编码不能为空")
    private String datasetCode;
    @Schema(description = "类型 eval=模型评测 rag=RAG质量评估")
    private String datasetType;
    @Schema(description = "行数(冗余)")
    private Integer rowCount;
    @Schema(description = "状态（0正常 1停用）")
    private String status;
}
