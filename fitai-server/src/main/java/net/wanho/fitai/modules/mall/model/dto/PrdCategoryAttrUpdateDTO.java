package net.wanho.fitai.modules.mall.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.io.Serializable;
import net.wanho.fitai.modules.mall.model.po.PrdCategoryAttr;

@Data
@Schema(description = "分类关联属性（复合主键）修改参数")
@AutoMapper(target = PrdCategoryAttr.class)
public class PrdCategoryAttrUpdateDTO implements Serializable {

    @Schema(description = "分类ID")
    @NotNull(message = "分类ID不能为空")
    private Long categoryId;
    @Schema(description = "属性ID")
    private Long attrId;
}
