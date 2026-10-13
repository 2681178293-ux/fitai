package net.wanho.fitai.modules.ai.model.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.wanho.fitai.infra.page.PageQuery;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "提示词管理查询参数")
public class AiPromptQueryDTO extends PageQuery {

    @Schema(description = "名称")
    private String promptName;
    @Schema(description = "编码")
    private String promptCode;
    @Schema(description = "状态（0正常 1停用）")
    private String status;
}
