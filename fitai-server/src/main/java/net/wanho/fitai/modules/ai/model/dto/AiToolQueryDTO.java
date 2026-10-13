package net.wanho.fitai.modules.ai.model.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.wanho.fitai.infra.page.PageQuery;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "AI工具管理查询参数")
public class AiToolQueryDTO extends PageQuery {

    @Schema(description = "工具名称")
    private String toolName;
    @Schema(description = "编码")
    private String toolCode;
    @Schema(description = "状态（0正常 1停用）")
    private String status;
}
