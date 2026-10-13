package net.wanho.fitai.modules.media.model.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.wanho.fitai.infra.page.PageQuery;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "VOD视频分类查询参数")
public class MediaVodCategoryQueryDTO extends PageQuery {

    @Schema(description = "分类名称")
    private String categoryName;
    @Schema(description = "状态（0正常 1停用）")
    private String status;
}
