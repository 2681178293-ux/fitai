package net.wanho.fitai.modules.media.model.vo;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import net.wanho.fitai.modules.media.model.po.MediaOssCategory;

import java.io.Serializable;
import java.util.List;

@Data
@Schema(description = "OSS图片分类视图")
@AutoMapper(target = MediaOssCategory.class)
public class MediaOssCategoryVO implements Serializable {

    @Schema(description = "分类ID")
    private Long categoryId;
    @Schema(description = "父分类ID")
    private Long parentId;
    @Schema(description = "祖级列表")
    private String ancestors;
    @Schema(description = "分类名称")
    private String categoryName;
    @Schema(description = "排序")
    private Integer orderNum;
    @Schema(description = "状态（0正常 1停用）")
    private String status;
    @Schema(description = "分类下图片总数")
    private Long imageCount;
    @Schema(description = "子分类")
    private List<MediaOssCategoryVO> children;
}
