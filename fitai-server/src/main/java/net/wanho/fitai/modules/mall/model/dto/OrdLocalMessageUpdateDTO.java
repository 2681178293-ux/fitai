package net.wanho.fitai.modules.mall.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;
import java.io.Serializable;
import net.wanho.fitai.modules.mall.model.po.OrdLocalMessage;

@Data
@Schema(description = "本地消息表(可靠消息最终一致性)修改参数")
@AutoMapper(target = OrdLocalMessage.class)
public class OrdLocalMessageUpdateDTO implements Serializable {

    @Schema(description = "消息ID(UUID，兼做消费幂等键)")
    @NotNull(message = "消息ID不能为空")
    private String messageId;
    @Schema(description = "业务类型(如 SECKILL_STOCK_RESTORE)")
    private String bizType;
    @Schema(description = "业务键(订单号等)")
    private String bizKey;
    @Schema(description = "目标交换机")
    private String exchangeName;
    @Schema(description = "路由键")
    private String routingKey;
    @Schema(description = "JSON 消息体")
    private String payload;
    @Schema(description = "状态(0待投递确认 1已确认 2重试耗尽)")
    private String status;
    @Schema(description = "已重试次数")
    private Integer retryCount;
    @Schema(description = "下次补偿重发时间")
    private LocalDateTime nextRetryTime;
}
