package net.wanho.fitai.modules.member.model.vo;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import net.wanho.fitai.modules.member.model.po.MemSubscription;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Schema(description = "用户会员订阅视图")
@AutoMapper(target = MemSubscription.class)
public class MemSubscriptionVO implements Serializable {

    @Schema(description = "订阅ID")
    private Long subscriptionId;
    @Schema(description = "来源会员订单ID")
    private Long memberOrderId;
    @Schema(description = "用户ID")
    private Long customerId;
    @Schema(description = "用户昵称")
    private String nickname;

    @Schema(description = "套餐名(冗余)")
    private String packageName;
    @Schema(description = "金额")
    private Double price;

    @Schema(description = "生效时间")
    private LocalDateTime startTime;
    @Schema(description = "到期时间")
    private LocalDateTime expireTime;
    @Schema(description = "状态（0有效 1已过期 2已取消）")
    private String status;

    @Schema(description = "自动续费（0否 1是）")
    private String autoRenew;
}
