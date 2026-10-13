package net.wanho.fitai.modules.mall.model.vo;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import net.wanho.fitai.modules.mall.model.po.PrdCategory;

import java.io.Serializable;
import java.util.List;

@Data
@Schema(description = "商品分类(树)视图")
@AutoMapper(target = PrdCategory.class)
public class PrdCategoryVO implements Serializable {

    @Schema(description = "分类ID")
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

    @Schema(description = "关联属性名称列表")
    private List<String> attrNames;

    private List<PrdCategoryVO> children;
}
