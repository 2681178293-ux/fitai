package net.wanho.fitai.modules.ai.model.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.wanho.fitai.infra.page.PageQuery;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "AI模型配置查询参数")
public class AiModelQueryDTO extends PageQuery {

    @Schema(description = "展示名称")
    private String modelName;
    @Schema(description = "模型编码")
    private String modelCode;
    @Schema(description = "密钥配置键(非明文)")
    private String apiKeyRef;
    @Schema(description = "状态（0正常 1停用）")
    private String status;
}
