package net.wanho.fitai.modules.marketing.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;
import java.io.Serializable;
import net.wanho.fitai.modules.marketing.model.po.MktCouponUser;

@Data
@Schema(description = "用户优惠券领取记录修改参数")
@AutoMapper(target = MktCouponUser.class)
public class MktCouponUserUpdateDTO implements Serializable {

    @Schema(description = "领取ID")
    @NotNull(message = "领取ID不能为空")
    private Long id;
    @Schema(description = "优惠券ID")
    private Long couponId;
    @Schema(description = "用户ID（usr_customer.customer_id）")
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
