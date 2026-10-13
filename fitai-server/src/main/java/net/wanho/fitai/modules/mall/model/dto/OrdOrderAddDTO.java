package net.wanho.fitai.modules.mall.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.io.Serializable;
import net.wanho.fitai.modules.mall.model.po.OrdOrder;

@Data
@Schema(description = "新增商品订单")
@AutoMapper(target = OrdOrder.class)
public class OrdOrderAddDTO implements Serializable {

    @Schema(description = "订单号")
    @NotBlank(message = "订单号不能为空")
    private String orderNo;
    @Schema(description = "用户ID")
    @NotNull(message = "用户ID不能为空")
    private Long customerId;
    @Schema(description = "昵称(冗余)")
    private String customerName;
    @Schema(description = "订单总额")
    @NotNull(message = "订单总额不能为空")
    private BigDecimal totalAmount;
    @Schema(description = "实付金额")
    @NotNull(message = "实付金额不能为空")
    private BigDecimal payAmount;
    @Schema(description = "优惠金额")
    private BigDecimal discountAmount;
    @Schema(description = "使用的用户优惠券ID(取消/退款时退券)")
    private Long couponUserId;
    @Schema(description = "秒杀活动ID(仅SK订单,一人一单按活动判重)")
    private Long activityId;
    @Schema(description = "支付渠道")
    private String payChannel;
    @Schema(description = "支付时间")
    private LocalDateTime payTime;
    @Schema(description = "收货人")
    private String receiverName;
    @Schema(description = "收货电话")
    private String receiverPhone;
    @Schema(description = "收货地址")
    private String receiverAddr;
    @Schema(description = "状态（0待支付 1已支付 2已发货 3已完成 4已取消 5退款中 6已退款）")
    private String status;
}
