package net.wanho.fitai.modules.mall.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import net.wanho.fitai.infra.page.PageQuery;

@Data
public class ProductQueryDTO extends PageQuery {
    @Schema(description = "商品名称")
    private String spuName;

    @Schema(description = "品牌名称")
    private String brandName;

    @Schema(description = "状态")
    private String status;
}
