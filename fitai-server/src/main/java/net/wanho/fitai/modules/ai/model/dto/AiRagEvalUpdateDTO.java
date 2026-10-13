package net.wanho.fitai.modules.ai.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;
import java.io.Serializable;
import net.wanho.fitai.modules.ai.model.po.AiRagEval;

@Data
@Schema(description = "RAG质量评估修改参数")
@AutoMapper(target = AiRagEval.class)
public class AiRagEvalUpdateDTO implements Serializable {

    @Schema(description = "评估ID")
    @NotNull(message = "评估ID不能为空")
    private Long evalId;
    @Schema(description = "知识库ID")
    private Long knowledgeId;
    @Schema(description = "评估名称")
    private String evalName;
    @Schema(description = "测试问题")
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
