package net.wanho.fitai.modules.ai.model.vo;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import net.wanho.fitai.modules.ai.model.po.AiTool;

@Data
@Schema(description = "AI工具管理视图")
@AutoMapper(target = AiTool.class)
public class AiToolVO implements Serializable {

    @Schema(description = "工具ID")
    private Long toolId;
    @Schema(description = "工具名称")
    private String toolName;
    @Schema(description = "编码")
    private String toolCode;
    @Schema(description = "类型 function/mcp/http")
    private String toolType;
    @Schema(description = "描述")
    private String description;
    @Schema(description = "参数Schema(JSON Schema)")
    private String schemaJson;
    @Schema(description = "HTTP/MCP端点")
    private String endpoint;
    @Schema(description = "状态（0正常 1停用）")
    private String status;
}
