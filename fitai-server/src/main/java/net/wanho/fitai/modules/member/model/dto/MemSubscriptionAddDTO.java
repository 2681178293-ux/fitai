package net.wanho.fitai.modules.member.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.io.Serializable;
import net.wanho.fitai.modules.member.model.po.MemSubscription;

@Data
@Schema(description = "新增用户会员订阅")
@AutoMapper(target = MemSubscription.class)
public class MemSubscriptionAddDTO implements Serializable {

    @Schema(description = "用户ID")
    @NotNull(message = "用户ID不能为空")
    private Long customerId;
    @Schema(description = "套餐ID")
    @NotNull(message = "套餐ID不能为空")
    private Long packageId;
    @Schema(description = "套餐名(冗余)")
    private String packageName;
    @Schema(description = "来源会员订单ID")
    private Long memberOrderId;
    @Schema(description = "生效时间")
    @NotNull(message = "生效时间不能为空")
    private LocalDateTime startTime;
    @Schema(description = "到期时间")
    @NotNull(message = "到期时间不能为空")
    private LocalDateTime expireTime;
    @Schema(description = "自动续费（0否 1是）")
    private String autoRenew;
    @Schema(description = "状态（0有效 1已过期 2已取消）")
    private String status;
}
