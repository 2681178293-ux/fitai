package net.wanho.fitai.modules.mall.model.vo;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.io.Serializable;
import net.wanho.fitai.modules.mall.model.po.PrdSpu;

@Data
@Schema(description = "商品SPU视图")
@AutoMapper(target = PrdSpu.class)
public class PrdSpuVO implements Serializable {

    @Schema(description = "SPU ID")
    private Long spuId;
    @Schema(description = "分类ID")
    private Long categoryId;
    @Schema(description = "品牌ID")
    private Long brandId;
    @Schema(description = "SPU名称")
    private String spuName;
    @Schema(description = "SPU货号")
    private String spuSn;
    @Schema(description = "副标题/卖点")
    private String subtitle;
    @Schema(description = "计量单位")
    private String unit;
    @Schema(description = "最低售价(冗余,来自SKU)")
    private BigDecimal minPrice;
    @Schema(description = "最高售价(冗余,来自SKU)")
    private BigDecimal maxPrice;
    @Schema(description = "总库存(冗余,来自SKU)")
    private Integer totalStock;
    @Schema(description = "总销量(冗余)")
    private Integer salesCount;
    @Schema(description = "排序(越大越靠前)")
    private Integer sort;
    @Schema(description = "状态（0上架 1下架）")
    private String status;
}
