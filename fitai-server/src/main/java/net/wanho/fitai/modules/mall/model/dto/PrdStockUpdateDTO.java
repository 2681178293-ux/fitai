package net.wanho.fitai.modules.mall.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.io.Serializable;
import net.wanho.fitai.modules.mall.model.po.PrdStock;

@Data
@Schema(description = "SKU库存修改参数")
@AutoMapper(target = PrdStock.class)
public class PrdStockUpdateDTO implements Serializable {

    @Schema(description = "库存ID")
    @NotNull(message = "库存ID不能为空")
    private Long stockId;
    @Schema(description = "SKU ID")
    private Long skuId;
    @Schema(description = "仓库编码")
    private String warehouseCode;
    @Schema(description = "可用库存")
    private Integer quantity;
    @Schema(description = "锁定库存")
    private Integer lockedQty;
    @Schema(description = "预警库存")
    private Integer lowStock;
}
