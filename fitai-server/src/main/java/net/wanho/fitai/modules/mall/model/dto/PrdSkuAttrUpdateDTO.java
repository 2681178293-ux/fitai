package net.wanho.fitai.modules.mall.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.io.Serializable;
import net.wanho.fitai.modules.mall.model.po.PrdSkuAttr;

@Data
@Schema(description = "SKU规格关联（复合主键）修改参数")
@AutoMapper(target = PrdSkuAttr.class)
public class PrdSkuAttrUpdateDTO implements Serializable {

    @Schema(description = "SKU ID")
    @NotNull(message = "SKU不能为空")
    private Long skuId;
    @Schema(description = "属性ID")
    private Long attrId;
    @Schema(description = "属性值ID")
    private Long valueId;
}
