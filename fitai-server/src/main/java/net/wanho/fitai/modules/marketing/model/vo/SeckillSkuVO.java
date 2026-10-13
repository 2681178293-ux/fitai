package net.wanho.fitai.modules.marketing.model.vo;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.io.Serializable;
import net.wanho.fitai.modules.marketing.model.po.SeckillSku;

@Data
@Schema(description = "秒杀商品表视图")
@AutoMapper(target = SeckillSku.class)
public class SeckillSkuVO implements Serializable {

    @Schema(description = "")
    private Long id;
    @Schema(description = "")
    private Long activityId;
    @Schema(description = "")
    private Long skuId;
    @Schema(description = "备注")
    private BigDecimal seckillPrice;
    @Schema(description = "备注")
    private Integer totalStock;
    @Schema(description = "备注")
    private Integer availableStock;
    @Schema(description = "备注")
    private Integer limitPerUser;
    @Schema(description = "")
    private Integer sortOrder;
    @Schema(description = "")
    private Integer status;
}
