package net.wanho.fitai.modules.mall.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import net.wanho.fitai.modules.mall.model.po.PrdCategoryAttr;

@Data
@Schema(description = "新增分类关联属性（复合主键）")
@AutoMapper(target = PrdCategoryAttr.class)
public class PrdCategoryAttrAddDTO implements Serializable {

    @Schema(description = "属性ID")
    @NotNull(message = "属性ID不能为空")
    private Long attrId;
}
