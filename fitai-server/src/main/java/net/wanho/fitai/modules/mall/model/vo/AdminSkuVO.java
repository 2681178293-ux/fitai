package net.wanho.fitai.modules.mall.model.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class AdminSkuVO {

    private Long skuId;
    private Long spuId;
    private String skuName;
    private String skuSn;
    private String skuSpecs;
    private BigDecimal price;
    private BigDecimal marketPrice;
    private BigDecimal costPrice;
    private Integer weight;
    private Integer salesCount;
    /** 对应 prd_sku.sort */
    private Integer sort;
    /** 0上架 1下架 */
    private String status;
    /** 解析 skuSpecs */
    private List<SkuSpecItemVO> specs;
    /** 来自 prd_stock.quantity */
    private Integer quantity;
    /** SPU 主图 */
    private String coverUrl;
}
