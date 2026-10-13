package net.wanho.fitai.modules.mall.model.vo;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.io.Serializable;
import net.wanho.fitai.modules.mall.model.po.OrdMemberOrder;

@Data
@Schema(description = "会员订单视图")
@AutoMapper(target = OrdMemberOrder.class)
public class OrdMemberOrderVO implements Serializable {

    @Schema(description = "订单ID")
    private Long orderId;
    @Schema(description = "订单号")
    private String orderNo;
    @Schema(description = "用户ID")
    private Long customerId;
    @Schema(description = "套餐ID")
    private Long packageId;
    @Schema(description = "套餐名(冗余)")
    private String packageName;
    @Schema(description = "天数(冗余)")
    private Integer durationDays;
    @Schema(description = "实付")
    private BigDecimal payAmount;
    @Schema(description = "支付渠道")
    private String payChannel;
    @Schema(description = "支付时间")
    private LocalDateTime payTime;
    @Schema(description = "到期时间")
    private LocalDateTime expireTime;
    @Schema(description = "状态（0待支付 1已支付 2已取消 3已退款）")
    private String status;
}
