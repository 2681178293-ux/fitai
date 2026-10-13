package net.wanho.fitai.modules.member.model.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.wanho.fitai.infra.page.PageQuery;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "会员套餐查询参数")
public class MemPackageQueryDTO extends PageQuery {

    @Schema(description = "套餐名称")
    private String packageName;
    @Schema(description = "等级名(冗余)")
    private String levelName;
    @Schema(description = "状态（0上架 1下架）")
    private String status;
}
