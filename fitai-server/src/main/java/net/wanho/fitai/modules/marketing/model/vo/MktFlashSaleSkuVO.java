package net.wanho.fitai.modules.marketing.model.vo;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.io.Serializable;
import net.wanho.fitai.modules.marketing.model.po.MktFlashSaleSku;

@Data
@Schema(description = "秒杀SKU视图")
@AutoMapper(target = MktFlashSaleSku.class)
public class MktFlashSaleSkuVO implements Serializable {

    @Schema(description = "秒杀明细ID")
    private Long flashSkuId;
    @Schema(description = "活动ID")
    private Long activityId;
    @Schema(description = "SPU ID")
    private Long spuId;
    @Schema(description = "SKU ID")
    private Long skuId;
    @Schema(description = "SKU名称(冗余)")
    private String skuName;
    @Schema(description = "秒杀价")
    private BigDecimal flashPrice;
    @Schema(description = "秒杀库存")
    private Integer flashStock;
    @Schema(description = "已售(冗余)")
    private Integer soldCount;
}
