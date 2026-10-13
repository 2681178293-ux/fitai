package net.wanho.fitai.modules.member.model.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.wanho.fitai.infra.page.PageQuery;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "会员套餐表查询参数")
public class MemberPlanQueryDTO extends PageQuery {

    @Schema(description = "套餐名称")
    private String name;
}
