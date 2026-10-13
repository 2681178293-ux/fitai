package net.wanho.fitai.modules.ai.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import net.wanho.fitai.modules.ai.model.po.AiPrompt;

@Data
@Schema(description = "新增提示词管理")
@AutoMapper(target = AiPrompt.class)
public class AiPromptAddDTO implements Serializable {

    @Schema(description = "名称")
    @NotBlank(message = "名称不能为空")
    private String promptName;
    @Schema(description = "编码")
    @NotBlank(message = "编码不能为空")
    private String promptCode;
    @Schema(description = "场景 train_plan/diet_plan/chat/coach")
    @NotBlank(message = "场景 train_plan不能为空")
    private String scene;
    @Schema(description = "System提示词")
    @NotBlank(message = "System提示词不能为空")
    private String systemPrompt;
    @Schema(description = "User模板")
    private String userTemplate;
    @Schema(description = "变量定义")
    private String variablesJson;
    @Schema(description = "默认模型ID")
    private Long modelId;
    @Schema(description = "版本")
    private Integer version;
    @Schema(description = "状态（0正常 1停用）")
    private String status;
}
