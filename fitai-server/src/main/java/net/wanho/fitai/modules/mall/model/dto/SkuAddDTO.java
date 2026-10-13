package net.wanho.fitai.modules.mall.model.dto;

import lombok.Data;
import net.wanho.fitai.modules.mall.model.vo.SkuSpecItemVO;

import java.math.BigDecimal;
import java.util.List;

@Data
public class SkuAddDTO {

    private Long skuId;
    private Long spuId;
    private String skuName;
    private String coverUrl;
    private String skuSpecs;
    private BigDecimal price;
    private int quantity;
    private String status;

    //prd_sku没有用到    prd_sku_attr用到
    private List<SkuSpecItemVO> specs;
}
