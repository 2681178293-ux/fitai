package net.wanho.fitai.modules.marketing.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.io.Serializable;
import net.wanho.fitai.modules.marketing.model.po.SeckillOrder;

@Data
@Schema(description = "新增秒杀订单表")
@AutoMapper(target = SeckillOrder.class)
public class SeckillOrderAddDTO implements Serializable {

    @Schema(description = "")
    @NotBlank(message = "orderNo不能为空")
    private String orderNo;
    @Schema(description = "")
    @NotNull(message = "userId不能为空")
    private Long userId;
    @Schema(description = "")
    @NotNull(message = "activityId不能为空")
    private Long activityId;
    @Schema(description = "")
    @NotNull(message = "skuId不能为空")
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
