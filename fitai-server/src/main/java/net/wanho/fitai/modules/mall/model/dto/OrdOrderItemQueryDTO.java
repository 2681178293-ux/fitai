package net.wanho.fitai.modules.mall.model.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.wanho.fitai.infra.page.PageQuery;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "订单明细(SKU快照)查询参数")
public class OrdOrderItemQueryDTO extends PageQuery {

    @Schema(description = "订单号(冗余)")
    private String orderNo;
    @Schema(description = "SKU名称(冗余)")
    private String skuName;
}
