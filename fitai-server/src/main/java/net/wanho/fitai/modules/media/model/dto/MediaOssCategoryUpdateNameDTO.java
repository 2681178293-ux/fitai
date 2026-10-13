package net.wanho.fitai.modules.media.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import net.wanho.fitai.modules.media.model.po.MediaOssCategory;

import java.io.Serializable;

@Data
@Schema(description = "OSS图片分类修改参数")
@AutoMapper(target = MediaOssCategory.class)
public class MediaOssCategoryUpdateNameDTO implements Serializable {

    @Schema(description = "分类ID")
    @NotNull(message = "分类ID不能为空")
    private Long categoryId;

    @Schema(description = "分类名称")
    private String categoryName;

}
