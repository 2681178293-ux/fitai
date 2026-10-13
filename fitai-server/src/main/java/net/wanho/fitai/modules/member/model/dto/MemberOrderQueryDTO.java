package net.wanho.fitai.modules.member.model.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.wanho.fitai.infra.page.PageQuery;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "会员订单表查询参数")
public class MemberOrderQueryDTO extends PageQuery {

    @Schema(description = "订单号")
    private String orderNo;
    @Schema(description = "套餐名称快照")
    private String planName;
}
