package net.wanho.fitai.modules.course.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.io.Serializable;
import net.wanho.fitai.modules.course.model.po.CrsCategory;

@Data
@Schema(description = "课程分类修改参数")
@AutoMapper(target = CrsCategory.class)
public class CrsCategoryUpdateDTO implements Serializable {

    @Schema(description = "分类ID")
    @NotNull(message = "分类ID不能为空")
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
}
