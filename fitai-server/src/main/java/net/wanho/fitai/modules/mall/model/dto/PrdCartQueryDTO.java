package net.wanho.fitai.modules.mall.model.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.wanho.fitai.infra.page.PageQuery;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "用户购物车(SKU)查询参数")
public class PrdCartQueryDTO extends PageQuery {

    @Schema(description = "SKU名称(冗余)")
    private String skuName;
}
