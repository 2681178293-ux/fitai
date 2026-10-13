package net.wanho.fitai.modules.mall.model.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.wanho.fitai.infra.page.PageQuery;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "商品SKU查询参数")
public class PrdSkuQueryDTO extends PageQuery {

    @Schema(description = "SKU名称")
    private String skuName;
    @Schema(description = "状态（0上架 1下架）")
    private String status;
}
