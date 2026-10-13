package net.wanho.fitai.modules.ai.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.io.Serializable;
import net.wanho.fitai.modules.ai.model.po.AiRagEval;

@Data
@Schema(description = "新增RAG质量评估")
@AutoMapper(target = AiRagEval.class)
public class AiRagEvalAddDTO implements Serializable {

    @Schema(description = "知识库ID")
    @NotNull(message = "知识库ID不能为空")
    private Long knowledgeId;
    @Schema(description = "评估名称")
    @NotBlank(message = "评估名称不能为空")
    private String evalName;
    @Schema(description = "测试问题")
    @NotBlank(message = "测试问题不能为空")
    private String question;
    @Schema(description = "期望答案")
    private String expectedAnswer;
    @Schema(description = "实际答案")
    private String actualAnswer;
    @Schema(description = "上下文召回（期望答案能否由检索上下文推出）")
    private BigDecimal recallScore;
    @Schema(description = "忠实度")
    private BigDecimal faithfulness;
    @Schema(description = "回答相关性")
    private BigDecimal relevancyScore;
    @Schema(description = "答案正确性（对比期望答案）")
    private BigDecimal correctness;
    @Schema(description = "耗时")
    private Integer latencyMs;
}
