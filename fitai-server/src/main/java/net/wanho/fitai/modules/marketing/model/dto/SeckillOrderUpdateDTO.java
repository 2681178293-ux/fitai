package net.wanho.fitai.modules.marketing.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.io.Serializable;
import net.wanho.fitai.modules.marketing.model.po.SeckillOrder;

@Data
@Schema(description = "秒杀订单表修改参数")
@AutoMapper(target = SeckillOrder.class)
public class SeckillOrderUpdateDTO implements Serializable {

    @Schema(description = "")
    @NotNull(message = "id不能为空")
    private Long id;
    @Schema(description = "")
    private String orderNo;
    @Schema(description = "")
    private Long userId;
    @Schema(description = "")
    private Long activityId;
    @Schema(description = "")
    private Long skuId;
    @Schema(description = "")
    private BigDecimal seckillPrice;
    @Schema(description = "")
    private Integer quantity;
    @Schema(description = "状态：0-待开始 1-进行中 2-已结束")
    private Integer status;
    @Schema(description = "")
    private LocalDateTime payTime;
    @Schema(description = "")
    private LocalDateTime cancelTime;
}
