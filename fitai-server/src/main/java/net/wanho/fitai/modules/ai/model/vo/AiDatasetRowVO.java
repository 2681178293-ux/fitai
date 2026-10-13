package net.wanho.fitai.modules.ai.model.vo;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import net.wanho.fitai.modules.ai.model.po.AiDatasetRow;

@Data
@Schema(description = "数据集行视图")
@AutoMapper(target = AiDatasetRow.class)
public class AiDatasetRowVO implements Serializable {

    @Schema(description = "行ID")
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
