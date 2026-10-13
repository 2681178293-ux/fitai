package net.wanho.fitai.modules.mall.model.vo;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.io.Serializable;
import net.wanho.fitai.modules.mall.model.po.OrdRefund;

@Data
@Schema(description = "售后退款视图")
@AutoMapper(target = OrdRefund.class)
public class OrdRefundVO implements Serializable {

    @Schema(description = "退款ID")
    private Long refundId;
    @Schema(description = "退款单号")
    private String refundNo;
    @Schema(description = "订单类型（1商品 2会员）")
    private String orderType;
    @Schema(description = "原订单ID")
    private Long orderId;
    @Schema(description = "原订单号(冗余)")
    private String orderNo;
    @Schema(description = "用户ID")
    private Long customerId;
    @Schema(description = "退款金额")
    private BigDecimal refundAmount;
    @Schema(description = "原因")
    private String reason;
    @Schema(description = "状态（0待审核 1已同意 2已拒绝 3已退款）")
    private String status;
    @Schema(description = "申请时订单原状态(拒绝时恢复)")
    private String orderPrevStatus;
}
