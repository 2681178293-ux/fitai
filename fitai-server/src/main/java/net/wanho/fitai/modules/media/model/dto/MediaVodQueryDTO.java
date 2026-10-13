package net.wanho.fitai.modules.media.model.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.wanho.fitai.infra.page.PageQuery;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "VOD视频查询参数")
public class MediaVodQueryDTO extends PageQuery {

    @Schema(description = "视频名称")
    private String vodName;
    @Schema(description = "分类名(冗余)")
    private String categoryName;
    @Schema(description = "状态（0正常 1处理中 2失败）")
    private String status;
}
