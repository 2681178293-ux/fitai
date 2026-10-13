package net.wanho.fitai.modules.course.model.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.wanho.fitai.infra.page.PageQuery;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "课程查询参数")
public class CrsCourseQueryDTO extends PageQuery {

    //@Schema(description = "分类名")
    //private String categoryName;
    //@Schema(description = "课程标题")
    //private String courseTitle;

    @Schema(description = "关键词")
    private String keyword;
    @Schema(description = "状态（0上架 1下架 2草稿）")
    private String status;
}
