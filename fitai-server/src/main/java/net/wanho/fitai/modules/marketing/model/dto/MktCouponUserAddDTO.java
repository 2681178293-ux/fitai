package net.wanho.fitai.modules.marketing.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.io.Serializable;
import net.wanho.fitai.modules.marketing.model.po.MktCouponUser;

@Data
@Schema(description = "新增用户优惠券领取记录")
@AutoMapper(target = MktCouponUser.class)
public class MktCouponUserAddDTO implements Serializable {

    @Schema(description = "优惠券ID")
    @NotNull(message = "优惠券ID不能为空")
    private Long couponId;
    @Schema(description = "用户ID（usr_customer.customer_id）")
    @NotNull(message = "用户ID不能为空")
    private Long userId;
    @Schema(description = "状态：0未使用 1已使用 2已过期/作废")
    private Integer status;
    @Schema(description = "使用订单号")
    private String orderNo;
    @Schema(description = "使用时间")
    private LocalDateTime useTime;
    @Schema(description = "过期时间")
    private LocalDateTime expireTime;
}
