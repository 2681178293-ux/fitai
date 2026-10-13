package net.wanho.fitai.modules.mall.model.dto;

import lombok.Data;
import net.wanho.fitai.modules.mall.model.vo.SkuSpecItemVO;

import java.math.BigDecimal;
import java.util.List;

@Data
public class SkuUpdateDTO {

    private String costPrice;
    private String coverUrl;
    private String marketPrice;
    private int orderNum;
    private BigDecimal price;
    private int quantity;
    private int salesCount;
    private Long skuId;
    private String skuName;
    private String skuSn;
    private String skuSpecs;
    private List<SkuSpecItemVO> specs;
    private Long spuId;
    private String status;
    private String weight;
}
