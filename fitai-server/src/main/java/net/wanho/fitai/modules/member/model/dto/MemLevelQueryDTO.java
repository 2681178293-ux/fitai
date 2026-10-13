package net.wanho.fitai.modules.member.model.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.wanho.fitai.infra.page.PageQuery;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "会员等级查询参数")
public class MemLevelQueryDTO extends PageQuery {

    @Schema(description = "等级名称")
    private String levelName;
    @Schema(description = "等级编码")
    private String levelCode;
    @Schema(description = "状态（0正常 1停用）")
    private String status;
}
