package net.wanho.fitai.modules.mall.model.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.wanho.fitai.infra.page.PageQuery;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "SKU库存查询参数")
public class PrdStockQueryDTO extends PageQuery {

    @Schema(description = "仓库编码")
    private String warehouseCode;
}
