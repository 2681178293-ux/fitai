package net.wanho.fitai.modules.member.model.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.wanho.fitai.infra.page.PageQuery;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "会员权益表查询参数")
public class MemberBenefitQueryDTO extends PageQuery {

    @Schema(description = "权益代码")
    private String code;
    @Schema(description = "权益名称")
    private String name;
}
