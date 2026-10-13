package net.wanho.fitai.modules.mall.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.io.Serializable;
import net.wanho.fitai.modules.mall.model.po.OrdOrderItem;

@Data
@Schema(description = "新增订单明细(SKU快照)")
@AutoMapper(target = OrdOrderItem.class)
public class OrdOrderItemAddDTO implements Serializable {

    @Schema(description = "订单ID")
    @NotNull(message = "订单ID不能为空")
    private Long orderId;
    @Schema(description = "订单号(冗余)")
    private String orderNo;
    @Schema(description = "SPU ID")
    @NotNull(message = "SPU不能为空")
    private Long spuId;
    @Schema(description = "SKU ID")
    @NotNull(message = "SKU不能为空")
    private Long skuId;
    @Schema(description = "SKU名称(冗余)")
    @NotBlank(message = "SKU名称不能为空")
    private String skuName;
    @Schema(description = "SKU货号(冗余)")
    private String skuSn;
    @Schema(description = "规格(冗余)")
    private String skuSpecs;
    @Schema(description = "封面(冗余)")
    private String coverUrl;
    @Schema(description = "单价(下单快照)")
    @NotNull(message = "单价不能为空")
    private BigDecimal price;
    @Schema(description = "数量")
    @NotNull(message = "数量不能为空")
    private Integer quantity;
    @Schema(description = "小计")
    @NotNull(message = "小计不能为空")
    private BigDecimal subtotal;
}
