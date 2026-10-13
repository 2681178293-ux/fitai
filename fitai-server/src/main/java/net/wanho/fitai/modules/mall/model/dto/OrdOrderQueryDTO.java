package net.wanho.fitai.modules.mall.model.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.wanho.fitai.infra.page.PageQuery;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "商品订单查询参数")
public class OrdOrderQueryDTO extends PageQuery {

    @Schema(description = "订单号")
    private String orderNo;
    @Schema(description = "昵称(冗余)")
    private String customerName;
    @Schema(description = "收货人")
    private String receiverName;
    @Schema(description = "收货电话")
    private String receiverPhone;
    @Schema(description = "状态（0待支付 1已支付 2已发货 3已完成 4已取消 5退款中 6已退款）")
    private String status;
}
