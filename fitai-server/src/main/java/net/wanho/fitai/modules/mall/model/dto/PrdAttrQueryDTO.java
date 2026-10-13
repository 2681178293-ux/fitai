package net.wanho.fitai.modules.mall.model.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.wanho.fitai.infra.page.PageQuery;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "商品属性查询参数")
public class PrdAttrQueryDTO extends PageQuery {

    @Schema(description = "属性名(如口味/尺码)")
    private String attrName;
    @Schema(description = "属性类型")
    private String attrType;
    @Schema(description = "状态（0正常 1停用）")
    private String status;
}
