package net.wanho.fitai.modules.mall.model.vo;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import net.wanho.fitai.modules.mall.model.po.PrdSkuAttr;

@Data
@Schema(description = "SKU规格关联（复合主键）视图")
@AutoMapper(target = PrdSkuAttr.class)
public class PrdSkuAttrVO implements Serializable {

    @Schema(description = "SKU ID")
    private Long skuId;
    @Schema(description = "属性ID")
    private Long attrId;
    @Schema(description = "属性值ID")
    private Long valueId;
}
