package net.wanho.fitai.modules.media.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import net.wanho.fitai.modules.media.model.po.MediaVodCategory;

import java.io.Serializable;

@Data
@Schema(description = "新增OSS图片分类")
@AutoMapper(target = MediaVodCategory.class)
public class MediaVodCategorySimpleAddDTO implements Serializable {

    @Schema(description = "父分类ID")
    private Long parentId;

    @Schema(description = "分类名称")
    @NotBlank(message = "分类名称不能为空")
    private String categoryName;


}
