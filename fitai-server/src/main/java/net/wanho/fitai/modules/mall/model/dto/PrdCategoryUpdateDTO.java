package net.wanho.fitai.modules.mall.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import net.wanho.fitai.modules.mall.model.po.PrdCategory;

import java.io.Serializable;
import java.util.List;

@Data
@Schema(description = "商品分类(树)修改参数")
@AutoMapper(target = PrdCategory.class)
public class PrdCategoryUpdateDTO implements Serializable {

    @Schema(description = "分类ID")
    @NotNull(message = "分类ID不能为空")
    private Long categoryId;
    @Schema(description = "父分类ID")
    private Long parentId;
    @Schema(description = "祖级列表")
    private String ancestors;
    @Schema(description = "分类名称")
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
    //这里的属性指的是商品属性的id
}
