package net.wanho.fitai.modules.course.model.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.wanho.fitai.infra.page.PageQuery;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "课程点赞查询参数")
public class CrsCourseLikeQueryDTO extends PageQuery {

}
