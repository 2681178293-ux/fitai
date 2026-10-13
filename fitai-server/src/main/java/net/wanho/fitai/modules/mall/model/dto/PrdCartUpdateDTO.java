package net.wanho.fitai.modules.mall.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;
import java.io.Serializable;
import net.wanho.fitai.modules.mall.model.po.PrdCart;

@Data
@Schema(description = "用户购物车(SKU)修改参数")
@AutoMapper(target = PrdCart.class)
public class PrdCartUpdateDTO implements Serializable {

    @Schema(description = "购物车ID")
    @NotNull(message = "购物车ID不能为空")
    private Long cartId;
    @Schema(description = "用户ID")
    private Long customerId;
    @Schema(description = "SPU ID(冗余)")
    private Long spuId;
    @Schema(description = "SKU ID")
    private Long skuId;
    @Schema(description = "SKU名称(冗余)")
    private String skuName;
    @Schema(description = "SKU货号(冗余)")
    private String skuSn;
    @Schema(description = "封面(冗余)")
    private String coverUrl;
    @Schema(description = "单价(冗余,加购时快照)")
    private BigDecimal price;
    @Schema(description = "数量")
    private Integer quantity;
    @Schema(description = "选中（0否 1是）")
    private String selected;
}
