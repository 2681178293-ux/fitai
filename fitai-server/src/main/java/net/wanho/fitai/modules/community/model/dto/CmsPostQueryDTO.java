package net.wanho.fitai.modules.community.model.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.wanho.fitai.infra.page.PageQuery;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "社区动态查询参数")
public class CmsPostQueryDTO extends PageQuery {

    @Schema(description = "昵称(冗余)")
    private String customerName;
    @Schema(description = "状态（0正常 1隐藏）")
    private String status;
}
