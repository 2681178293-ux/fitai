package net.wanho.fitai.modules.mall.model.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.wanho.fitai.infra.page.PageQuery;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "商品SPU查询参数")
public class PrdSpuQueryDTO extends PageQuery {

    @Schema(description = "SPU名称")
    private String spuName;
    @Schema(description = "品牌名称")
    private String brandName;
    @Schema(description = "副标题/卖点")
    private String subtitle;
    @Schema(description = "状态（0上架 1下架）")
    private String status;
}
