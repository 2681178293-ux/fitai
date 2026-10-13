package net.wanho.fitai.modules.mall.model.vo;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import net.wanho.fitai.modules.mall.model.po.PrdStock;

@Data
@Schema(description = "SKU库存视图")
@AutoMapper(target = PrdStock.class)
public class PrdStockVO implements Serializable {

    @Schema(description = "库存ID")
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
