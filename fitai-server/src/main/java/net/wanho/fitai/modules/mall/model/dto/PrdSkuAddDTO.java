package net.wanho.fitai.modules.mall.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.io.Serializable;
import net.wanho.fitai.modules.mall.model.po.PrdSku;

@Data
@Schema(description = "新增商品SKU")
@AutoMapper(target = PrdSku.class)
public class PrdSkuAddDTO implements Serializable {

    @Schema(description = "SPU ID")
    @NotNull(message = "SPU不能为空")
    private Long spuId;
    @Schema(description = "SKU名称")
    @NotBlank(message = "SKU名称不能为空")
    private String skuName;
    @Schema(description = "SKU货号")
    @NotBlank(message = "SKU货号不能为空")
    private String skuSn;
    @Schema(description = "规格描述(冗余,如口味:巧克力)")
    private String skuSpecs;
    @Schema(description = "售价")
    @NotNull(message = "售价不能为空")
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
