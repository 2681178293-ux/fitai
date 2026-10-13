package net.wanho.fitai.modules.course.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import net.wanho.fitai.modules.course.model.po.CrsCategory;

import java.io.Serializable;

@Data
@Schema(description = "新增课程分类")
@AutoMapper(target = CrsCategory.class)
public class CrsCategoryAddDTO implements Serializable {

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
}
