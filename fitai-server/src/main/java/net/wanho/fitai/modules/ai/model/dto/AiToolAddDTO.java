package net.wanho.fitai.modules.ai.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import net.wanho.fitai.modules.ai.model.po.AiTool;

@Data
@Schema(description = "新增AI工具管理")
@AutoMapper(target = AiTool.class)
public class AiToolAddDTO implements Serializable {

    @Schema(description = "工具名称")
    @NotBlank(message = "工具名称不能为空")
    private String toolName;
    @Schema(description = "编码")
    @NotBlank(message = "编码不能为空")
    private String toolCode;
    @Schema(description = "类型 function/mcp/http")
    private String toolType;
    @Schema(description = "描述")
    private String description;
    @Schema(description = "参数Schema(JSON Schema)")
    @NotBlank(message = "参数Schema不能为空")
    private String schemaJson;
    @Schema(description = "HTTP/MCP端点")
    private String endpoint;
    @Schema(description = "状态（0正常 1停用）")
    private String status;
}
