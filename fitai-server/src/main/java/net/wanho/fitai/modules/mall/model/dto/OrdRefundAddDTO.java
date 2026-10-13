package net.wanho.fitai.modules.mall.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.io.Serializable;
import net.wanho.fitai.modules.mall.model.po.OrdRefund;

@Data
@Schema(description = "新增售后退款")
@AutoMapper(target = OrdRefund.class)
public class OrdRefundAddDTO implements Serializable {

    @Schema(description = "退款单号")
    @NotBlank(message = "退款单号不能为空")
    private String refundNo;
    @Schema(description = "订单类型（1商品 2会员）")
    @NotBlank(message = "订单类型不能为空")
    private String orderType;
    @Schema(description = "原订单ID")
    @NotNull(message = "原订单ID不能为空")
    private Long orderId;
    @Schema(description = "原订单号(冗余)")
    @NotBlank(message = "原订单号不能为空")
    private String orderNo;
    @Schema(description = "用户ID")
    @NotNull(message = "用户ID不能为空")
    private Long customerId;
    @Schema(description = "退款金额")
    @NotNull(message = "退款金额不能为空")
    private BigDecimal refundAmount;
    @Schema(description = "原因")
    private String reason;
    @Schema(description = "状态（0待审核 1已同意 2已拒绝 3已退款）")
    private String status;
    @Schema(description = "申请时订单原状态(拒绝时恢复)")
    private String orderPrevStatus;
}
