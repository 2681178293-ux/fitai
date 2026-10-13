package net.wanho.fitai.modules.mall.model.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.wanho.fitai.infra.page.PageQuery;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "本地消息表(商品ES同步可靠消息)查询参数")
public class PrdLocalMessageQueryDTO extends PageQuery {

    @Schema(description = "业务键(spuId 等)")
    private String bizKey;
    @Schema(description = "目标交换机")
    private String exchangeName;
    @Schema(description = "路由键")
    private String routingKey;
    @Schema(description = "状态(0待投递确认 1已确认 2重试耗尽)")
    private String status;
}
