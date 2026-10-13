package net.wanho.fitai.modules.mall.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.io.Serializable;
import net.wanho.fitai.modules.mall.model.po.OrdLocalMessage;

@Data
@Schema(description = "新增本地消息表(可靠消息最终一致性)")
@AutoMapper(target = OrdLocalMessage.class)
public class OrdLocalMessageAddDTO implements Serializable {

    @Schema(description = "业务类型(如 SECKILL_STOCK_RESTORE)")
    @NotBlank(message = "业务类型不能为空")
    private String bizType;
    @Schema(description = "业务键(订单号等)")
    private String bizKey;
    @Schema(description = "目标交换机")
    @NotBlank(message = "目标交换机不能为空")
    private String exchangeName;
    @Schema(description = "路由键")
    @NotBlank(message = "路由键不能为空")
    private String routingKey;
    @Schema(description = "JSON 消息体")
    @NotBlank(message = "JSON不能为空")
    private String payload;
    @Schema(description = "状态(0待投递确认 1已确认 2重试耗尽)")
    private String status;
    @Schema(description = "已重试次数")
    private Integer retryCount;
    @Schema(description = "下次补偿重发时间")
    private LocalDateTime nextRetryTime;
}
