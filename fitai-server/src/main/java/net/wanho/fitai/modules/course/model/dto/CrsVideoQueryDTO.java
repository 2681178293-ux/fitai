package net.wanho.fitai.modules.course.model.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.wanho.fitai.infra.page.PageQuery;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "课程视频查询参数")
public class CrsVideoQueryDTO extends PageQuery {

    @Schema(description = "课程名(冗余)")
    private String courseTitle;
    @Schema(description = "视频标题")
    private String videoTitle;
    @Schema(description = "VOD分类名(冗余)")
    private String vodCategoryName;
    @Schema(description = "状态（0正常 1停用）")
    private String status;
}
