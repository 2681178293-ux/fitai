package net.wanho.fitai.modules.mall.model.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.wanho.fitai.infra.page.PageQuery;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "会员订单查询参数")
public class OrdMemberOrderQueryDTO extends PageQuery {

    @Schema(description = "订单号")
    private String orderNo;
    @Schema(description = "套餐名(冗余)")
    private String packageName;
    @Schema(description = "状态（0待支付 1已支付 2已取消 3已退款）")
    private String status;
}
