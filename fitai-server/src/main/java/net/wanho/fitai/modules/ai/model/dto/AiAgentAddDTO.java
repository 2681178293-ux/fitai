package net.wanho.fitai.modules.ai.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import net.wanho.fitai.modules.ai.model.po.AiAgent;

@Data
@Schema(description = "新增Agent编排")
@AutoMapper(target = AiAgent.class)
public class AiAgentAddDTO implements Serializable {

    @Schema(description = "名称")
    @NotBlank(message = "名称不能为空")
    private String agentName;
    @Schema(description = "编码")
    @NotBlank(message = "编码不能为空")
    private String agentCode;
    @Schema(description = "描述")
    private String description;
    @Schema(description = "主模型ID")
    @NotNull(message = "主模型ID不能为空")
    private Long modelId;
    @Schema(description = "提示词ID")
    private Long promptId;
    @Schema(description = "编排流程JSON(nodes/edges)")
    @NotBlank(message = "编排流程JSON不能为空")
    private String flowJson;
    @Schema(description = "绑定工具ID列表")
    private String toolIdsJson;
    @Schema(description = "状态（0正常 1停用）")
    private String status;
}
