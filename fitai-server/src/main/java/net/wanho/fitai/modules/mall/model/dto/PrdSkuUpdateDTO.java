package net.wanho.fitai.modules.mall.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;
import java.io.Serializable;
import net.wanho.fitai.modules.mall.model.po.PrdSku;

@Data
@Schema(description = "商品SKU修改参数")
@AutoMapper(target = PrdSku.class)
public class PrdSkuUpdateDTO implements Serializable {

    @Schema(description = "SKU ID")
    @NotNull(message = "SKU不能为空")
    private Long skuId;
    @Schema(description = "SPU ID")
    private Long spuId;
    @Schema(description = "SKU名称")
    private String skuName;
    @Schema(description = "SKU货号")
    private String skuSn;
    @Schema(description = "规格描述(冗余,如口味:巧克力)")
    private String skuSpecs;
    @Schema(description = "售价")
    private BigDecimal price;
    @Schema(description = "市场价")
    private BigDecimal marketPrice;
    @Schema(description = "成本价")
    private BigDecimal costPrice;
    @Schema(description = "重量(克)")
    private Integer weight;
    @Schema(description = "销量(冗余)")
    private Integer salesCount;
    @Schema(description = "排序")
    private Integer sort;
    @Schema(description = "状态（0上架 1下架）")
    private String status;
}
