package net.wanho.fitai.modules.media.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import net.wanho.fitai.modules.media.model.po.MediaOssCategory;

import java.io.Serializable;

@Data
@Schema(description = "新增OSS图片分类")
@AutoMapper(target = MediaOssCategory.class)
public class MediaOssCategoryAddDTO implements Serializable {

    @Schema(description = "父分类ID")
    private Long parentId;
    @Schema(description = "祖级列表")
    private String ancestors;
    @Schema(description = "分类名称")
    @NotBlank(message = "分类名称不能为空")
    private String categoryName;
    @Schema(description = "排序")
    private Integer orderNum;
    @Schema(description = "状态（0正常 1停用）")
    private String status;
}
