package net.wanho.fitai.modules.ai.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import net.wanho.fitai.modules.ai.model.po.AiGuardrail;

@Data
@Schema(description = "新增内容护栏")
@AutoMapper(target = AiGuardrail.class)
public class AiGuardrailAddDTO implements Serializable {

    @Schema(description = "规则名称")
    @NotBlank(message = "规则名称不能为空")
    private String ruleName;
    @Schema(description = "类型 keyword/medical/disclaimer/model")
    @NotBlank(message = "类型 keyword不能为空")
    private String ruleType;
    @Schema(description = "规则内容/关键词")
    @NotBlank(message = "规则内容不能为空")
    private String ruleContent;
    @Schema(description = "动作 block/warn/replace")
    private String action;
    @Schema(description = "优先级")
    private Integer priority;
    @Schema(description = "状态（0正常 1停用）")
    private String status;
}
