package net.wanho.fitai.modules.media.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import net.wanho.fitai.modules.media.model.po.MediaVodCategory;

import java.io.Serializable;

@Data
@Schema(description = "VOD视频分类修改参数")
@AutoMapper(target = MediaVodCategory.class)
public class MediaVodCategoryUpdateDTO implements Serializable {

    @Schema(description = "分类ID")
    @NotNull(message = "分类ID不能为空")
    private Long categoryId;
    @Schema(description = "父分类ID")
    private Long parentId;
    @Schema(description = "祖级列表")
    private String ancestors;
    @Schema(description = "分类名称")
    private String categoryName;
    @Schema(description = "阿里云VOD分类ID(同步)")
    private Long vodCateId;
    @Schema(description = "排序")
    private Integer orderNum;
    @Schema(description = "状态（0正常 1停用）")
    private String status;
}
