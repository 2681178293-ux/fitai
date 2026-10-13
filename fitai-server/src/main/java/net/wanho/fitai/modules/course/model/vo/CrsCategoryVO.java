package net.wanho.fitai.modules.course.model.vo;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import net.wanho.fitai.modules.course.model.po.CrsCategory;

import java.io.Serializable;
import java.util.List;

@Data
@Schema(description = "课程分类视图")
@AutoMapper(target = CrsCategory.class)
public class CrsCategoryVO implements Serializable {

    @Schema(description = "分类ID")
    private Long categoryId;
    @Schema(description = "父分类ID")
    private Long parentId;
    @Schema(description = "祖级列表")
    private String ancestors;
    @Schema(description = "分类名称")
    private String categoryName;
    @Schema(description = "图标OSS图片ID")
    private Long iconOssId;
    @Schema(description = "图标URL(冗余)")
    private String icon;
    @Schema(description = "排序")
    private Integer orderNum;
    @Schema(description = "状态（0正常 1停用）")
    private String status;

    @Schema(description = "子部门")
    private List<CrsCategoryVO> children;
}
