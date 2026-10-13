package net.wanho.fitai.modules.course.model.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.wanho.fitai.infra.page.PageQuery;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "课程学习进度查询参数")
public class CrsLearnProgressQueryDTO extends PageQuery {

    @Schema(description = "课程名(冗余)")
    private String courseTitle;
    @Schema(description = "课时名(冗余)")
    private String videoTitle;
}
