package net.wanho.fitai.modules.mall.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.io.Serializable;
import net.wanho.fitai.modules.mall.model.po.PrdCart;

@Data
@Schema(description = "新增用户购物车(SKU)")
@AutoMapper(target = PrdCart.class)
public class PrdCartAddDTO implements Serializable {

    @Schema(description = "用户ID")
    @NotNull(message = "用户ID不能为空")
    private Long customerId;
    @Schema(description = "SPU ID(冗余)")
    @NotNull(message = "SPU ID不能为空")
    private Long spuId;
    @Schema(description = "SKU ID")
    @NotNull(message = "SKU不能为空")
    private Long skuId;
    @Schema(description = "SKU名称(冗余)")
    private String skuName;
    @Schema(description = "SKU货号(冗余)")
    private String skuSn;
    @Schema(description = "封面(冗余)")
    private String coverUrl;
    @Schema(description = "单价(冗余,加购时快照)")
    @NotNull(message = "单价不能为空")
    private BigDecimal price;
    @Schema(description = "数量")
    private Integer quantity;
    @Schema(description = "选中（0否 1是）")
    private String selected;
}
