package net.wanho.fitai.modules.mall.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;
import java.io.Serializable;
import net.wanho.fitai.modules.mall.model.po.OrdOrderItem;

@Data
@Schema(description = "订单明细(SKU快照)修改参数")
@AutoMapper(target = OrdOrderItem.class)
public class OrdOrderItemUpdateDTO implements Serializable {

    @Schema(description = "明细ID")
    @NotNull(message = "明细ID不能为空")
    private Long itemId;
    @Schema(description = "订单ID")
    private Long orderId;
    @Schema(description = "订单号(冗余)")
    private String orderNo;
    @Schema(description = "SPU ID")
    private Long spuId;
    @Schema(description = "SKU ID")
    private Long skuId;
    @Schema(description = "SKU名称(冗余)")
    private String skuName;
    @Schema(description = "SKU货号(冗余)")
    private String skuSn;
    @Schema(description = "规格(冗余)")
    private String skuSpecs;
    @Schema(description = "封面(冗余)")
    private String coverUrl;
    @Schema(description = "单价(下单快照)")
    private BigDecimal price;
    @Schema(description = "数量")
    private Integer quantity;
    @Schema(description = "小计")
    private BigDecimal subtotal;
}
