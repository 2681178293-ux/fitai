package net.wanho.fitai.modules.mall.model.vo;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.io.Serializable;
import net.wanho.fitai.modules.mall.model.po.OrdPaymentRecord;

@Data
@Schema(description = "支付流水视图")
@AutoMapper(target = OrdPaymentRecord.class)
public class OrdPaymentRecordVO implements Serializable {

    @Schema(description = "支付ID")
    private Long paymentId;
    @Schema(description = "支付流水号")
    private String paymentNo;
    @Schema(description = "合并支付批次号")
    private String mergeNo;
    @Schema(description = "订单类型（1商品 2会员）")
    private String orderType;
    @Schema(description = "业务订单ID")
    private Long orderId;
    @Schema(description = "业务订单号(冗余)")
    private String orderNo;
    @Schema(description = "用户ID")
    private Long customerId;
    @Schema(description = "支付渠道 wechat/alipay")
    private String channelCode;
    @Schema(description = "支付金额")
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
