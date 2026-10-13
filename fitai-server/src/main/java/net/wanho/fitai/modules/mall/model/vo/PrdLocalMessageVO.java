package net.wanho.fitai.modules.mall.model.vo;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;
import java.io.Serializable;
import net.wanho.fitai.modules.mall.model.po.PrdLocalMessage;

@Data
@Schema(description = "本地消息表(商品ES同步可靠消息)视图")
@AutoMapper(target = PrdLocalMessage.class)
public class PrdLocalMessageVO implements Serializable {

    @Schema(description = "消息ID(UUID，兼做消费幂等键)")
    private String messageId;
    @Schema(description = "业务类型(如 PRD_ES_SYNC)")
    private String bizType;
    @Schema(description = "业务键(spuId 等)")
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
