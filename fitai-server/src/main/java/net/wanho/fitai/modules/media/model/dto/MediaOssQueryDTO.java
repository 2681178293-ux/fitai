package net.wanho.fitai.modules.media.model.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.wanho.fitai.infra.page.PageQuery;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "OSS图片查询参数")
public class MediaOssQueryDTO extends PageQuery {

    @Schema(description = "分类id")
    private String categoryId;

    @Schema(description = "关键词")
    private String keyword;

}
