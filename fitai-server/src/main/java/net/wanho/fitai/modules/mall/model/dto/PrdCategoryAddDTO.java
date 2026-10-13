package net.wanho.fitai.modules.mall.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.List;
import net.wanho.fitai.modules.mall.model.po.PrdCategory;

@Data
@Schema(description = "新增商品分类(树)")
@AutoMapper(target = PrdCategory.class)
public class PrdCategoryAddDTO implements Serializable {

    @Schema(description = "父分类ID")
    private Long parentId;
    @Schema(description = "祖级列表")
    private String ancestors;
    @Schema(description = "分类名称")
    @NotBlank(message = "分类名称不能为空")
    private String categoryName;
    @Schema(description = "层级(1起)")
    private Integer level;
    @Schema(description = "图标")
    private String icon;
    @Schema(description = "排序")
    private Integer orderNum;
    @Schema(description = "状态（0正常 1停用）")
    private String status;

    @Schema(description = "关联属性ID列表")
    private List<Long> attrIds;
}
