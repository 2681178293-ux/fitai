package net.wanho.fitai.modules.mall.model.vo;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import net.wanho.fitai.modules.mall.model.po.PrdCategoryAttr;

@Data
@Schema(description = "分类关联属性（复合主键）视图")
@AutoMapper(target = PrdCategoryAttr.class)
public class PrdCategoryAttrVO implements Serializable {

    @Schema(description = "分类ID")
    private Long categoryId;
    @Schema(description = "属性ID")
    private Long attrId;
}
