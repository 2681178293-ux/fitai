package net.wanho.fitai.modules.ai.model.vo;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.io.Serializable;
import net.wanho.fitai.modules.ai.model.po.AiModel;

@Data
@Schema(description = "AI模型配置视图")
@AutoMapper(target = AiModel.class)
public class AiModelVO implements Serializable {

    @Schema(description = "模型ID")
    private Long modelId;
    @Schema(description = "展示名称")
    private String modelName;
    @Schema(description = "模型编码")
    private String modelCode;
    @Schema(description = "提供商 dashscope/openai")
    private String provider;
    @Schema(description = "API模型名 qwen-plus/qwen-turbo")
    private String apiModel;
    @Schema(description = "类型 chat/embedding/image")
    private String modelType;
    @Schema(description = "温度")
    private BigDecimal temperature;
    @Schema(description = "TopP")
    private BigDecimal topP;
    @Schema(description = "最大Token")
    private Integer maxTokens;
    @Schema(description = "输入单价(元/百万Token)")
    private BigDecimal inputPrice;
    @Schema(description = "输出单价(元/百万Token)")
    private BigDecimal outputPrice;
    @Schema(description = "API Base URL")
    private String baseUrl;
    @Schema(description = "密钥配置键(非明文)")
    private String apiKeyRef;
    @Schema(description = "排序")
    private Integer sortOrder;
    @Schema(description = "默认（0否 1是）")
    private String isDefault;
    @Schema(description = "状态（0正常 1停用）")
    private String status;
}
