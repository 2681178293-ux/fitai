package net.wanho.fitai.modules.member.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.io.Serializable;
import net.wanho.fitai.modules.member.model.po.MemberOrder;

@Data
@Schema(description = "会员订单表修改参数")
@AutoMapper(target = MemberOrder.class)
public class MemberOrderUpdateDTO implements Serializable {

    @Schema(description = "订单ID")
    @NotNull(message = "订单ID不能为空")
    private Long id;
    @Schema(description = "订单号")
    private String orderNo;
    @Schema(description = "用户ID（usr_customer.customer_id）")
    private Long userId;
    @Schema(description = "套餐ID")
    private Long planId;
    @Schema(description = "套餐名称快照")
    private String planName;
    @Schema(description = "支付金额")
    private BigDecimal amount;
    @Schema(description = "支付方式：alipay/wxpay")
    private String payType;
    @Schema(description = "支付时间")
    private LocalDateTime payTime;
    @Schema(description = "会员开始时间")
    private LocalDateTime startTime;
    @Schema(description = "会员结束时间")
    private LocalDateTime endTime;
    @Schema(description = "状态：0-待支付 1-已支付 2-已取消 3-已退款")
    private Integer status;
}
