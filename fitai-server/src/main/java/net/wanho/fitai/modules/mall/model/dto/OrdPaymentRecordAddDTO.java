package net.wanho.fitai.modules.mall.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.io.Serializable;
import net.wanho.fitai.modules.mall.model.po.OrdPaymentRecord;

@Data
@Schema(description = "新增支付流水")
@AutoMapper(target = OrdPaymentRecord.class)
public class OrdPaymentRecordAddDTO implements Serializable {

    @Schema(description = "支付流水号")
    @NotBlank(message = "支付流水号不能为空")
    private String paymentNo;
    @Schema(description = "合并支付批次号")
    private String mergeNo;
    @Schema(description = "订单类型（1商品 2会员）")
    @NotBlank(message = "订单类型不能为空")
    private String orderType;
    @Schema(description = "业务订单ID")
    @NotNull(message = "业务订单ID不能为空")
    private Long orderId;
    @Schema(description = "业务订单号(冗余)")
    @NotBlank(message = "业务订单号不能为空")
    private String orderNo;
    @Schema(description = "用户ID")
    @NotNull(message = "用户ID不能为空")
    private Long customerId;
    @Schema(description = "支付渠道 wechat/alipay")
    @NotBlank(message = "支付渠道 wechat不能为空")
    private String channelCode;
    @Schema(description = "支付金额")
    @NotNull(message = "支付金额不能为空")
    private BigDecimal payAmount;
    @Schema(description = "第三方交易号")
    private String transactionId;
    @Schema(description = "状态（0待支付 1成功 2失败 3已退款）")
    private String payStatus;
    @Schema(description = "支付时间")
    private LocalDateTime payTime;
    @Schema(description = "回调原文")
    private String callbackRaw;
}
