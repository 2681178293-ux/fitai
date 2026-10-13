package net.wanho.fitai.modules.ai.model.vo;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import net.wanho.fitai.modules.ai.model.po.AiGuardrail;

@Data
@Schema(description = "内容护栏视图")
@AutoMapper(target = AiGuardrail.class)
public class AiGuardrailVO implements Serializable {

    @Schema(description = "护栏ID")
    private Long guardrailId;
    @Schema(description = "规则名称")
    private String ruleName;
    @Schema(description = "类型 keyword/medical/disclaimer/model")
    private String ruleType;
    @Schema(description = "规则内容/关键词")
    private String ruleContent;
    @Schema(description = "动作 block/warn/replace")
    private String action;
    @Schema(description = "优先级")
    private Integer priority;
    @Schema(description = "状态（0正常 1停用）")
    private String status;
}
