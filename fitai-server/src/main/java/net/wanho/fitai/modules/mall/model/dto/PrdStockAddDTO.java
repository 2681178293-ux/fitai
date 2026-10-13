package net.wanho.fitai.modules.mall.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import net.wanho.fitai.modules.mall.model.po.PrdStock;

@Data
@Schema(description = "新增SKU库存")
@AutoMapper(target = PrdStock.class)
public class PrdStockAddDTO implements Serializable {

    @Schema(description = "SKU ID")
    @NotNull(message = "SKU不能为空")
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
