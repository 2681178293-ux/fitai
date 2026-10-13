package net.wanho.fitai.modules.marketing.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.io.Serializable;
import net.wanho.fitai.modules.marketing.model.po.SeckillSku;

@Data
@Schema(description = "新增秒杀商品表")
@AutoMapper(target = SeckillSku.class)
public class SeckillSkuAddDTO implements Serializable {

    @Schema(description = "")
    @NotNull(message = "activityId不能为空")
    private Long activityId;
    @Schema(description = "")
    @NotNull(message = "skuId不能为空")
    private Long skuId;
    @Schema(description = "备注")
    @NotNull(message = "备注不能为空")
    private BigDecimal seckillPrice;
    @Schema(description = "备注")
    @NotNull(message = "备注不能为空")
    private Integer totalStock;
    @Schema(description = "备注")
    @NotNull(message = "备注不能为空")
    private Integer availableStock;
    @Schema(description = "备注")
    private Integer limitPerUser;
    @Schema(description = "")
    private Integer sortOrder;
    @Schema(description = "")
    private Integer status;
}
