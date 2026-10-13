package net.wanho.fitai.modules.mall.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.io.Serializable;
import net.wanho.fitai.modules.mall.model.po.OrdMemberOrder;

@Data
@Schema(description = "新增会员订单")
@AutoMapper(target = OrdMemberOrder.class)
public class OrdMemberOrderAddDTO implements Serializable {

    @Schema(description = "订单号")
    @NotBlank(message = "订单号不能为空")
    private String orderNo;
    @Schema(description = "用户ID")
    @NotNull(message = "用户ID不能为空")
    private Long customerId;
    @Schema(description = "套餐ID")
    @NotNull(message = "套餐ID不能为空")
    private Long packageId;
    @Schema(description = "套餐名(冗余)")
    private String packageName;
    @Schema(description = "天数(冗余)")
    @NotNull(message = "天数不能为空")
    private Integer durationDays;
    @Schema(description = "实付")
    @NotNull(message = "实付不能为空")
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
