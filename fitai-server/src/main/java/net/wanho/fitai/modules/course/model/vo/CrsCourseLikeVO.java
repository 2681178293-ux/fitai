package net.wanho.fitai.modules.course.model.vo;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import net.wanho.fitai.modules.course.model.po.CrsCourseLike;

@Data
@Schema(description = "课程点赞视图")
@AutoMapper(target = CrsCourseLike.class)
public class CrsCourseLikeVO implements Serializable {

    @Schema(description = "点赞记录ID")
    private Long likeId;
    @Schema(description = "课程ID")
    private Long courseId;
    @Schema(description = "会员ID")
    private Long customerId;
}
