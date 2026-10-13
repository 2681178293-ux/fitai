package net.wanho.fitai.modules.mall.model.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.wanho.fitai.infra.page.PageQuery;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "商品属性值查询参数")
public class PrdAttrValueQueryDTO extends PageQuery {

    @Schema(description = "状态（0正常 1停用）")
    private String status;
}
