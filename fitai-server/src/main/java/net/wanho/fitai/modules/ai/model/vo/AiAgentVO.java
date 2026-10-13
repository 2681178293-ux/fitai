package net.wanho.fitai.modules.ai.model.vo;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import net.wanho.fitai.modules.ai.model.po.AiAgent;

@Data
@Schema(description = "Agent编排视图")
@AutoMapper(target = AiAgent.class)
public class AiAgentVO implements Serializable {

    @Schema(description = "AgentID")
    private Long agentId;
    @Schema(description = "名称")
    private String agentName;
    @Schema(description = "编码")
    private String agentCode;
    @Schema(description = "描述")
    private String description;
    @Schema(description = "主模型ID")
    private Long modelId;
    @Schema(description = "提示词ID")
    private Long promptId;
    @Schema(description = "编排流程JSON(nodes/edges)")
    private String flowJson;
    @Schema(description = "绑定工具ID列表")
    private String toolIdsJson;
    @Schema(description = "状态（0正常 1停用）")
    private String status;
}
