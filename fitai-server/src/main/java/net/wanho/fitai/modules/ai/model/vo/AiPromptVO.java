package net.wanho.fitai.modules.ai.model.vo;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import net.wanho.fitai.modules.ai.model.po.AiPrompt;

@Data
@Schema(description = "提示词管理视图")
@AutoMapper(target = AiPrompt.class)
public class AiPromptVO implements Serializable {

    @Schema(description = "提示词ID")
    private Long promptId;
    @Schema(description = "名称")
    private String promptName;
    @Schema(description = "编码")
    private String promptCode;
    @Schema(description = "场景 train_plan/diet_plan/chat/coach")
    private String scene;
    @Schema(description = "System提示词")
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
