package net.wanho.fitai.modules.course.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.io.Serializable;
import net.wanho.fitai.modules.course.model.po.CrsCourseLike;

@Data
@Schema(description = "课程点赞修改参数")
@AutoMapper(target = CrsCourseLike.class)
public class CrsCourseLikeUpdateDTO implements Serializable {

    @Schema(description = "点赞记录ID")
    @NotNull(message = "点赞记录ID不能为空")
    private Long likeId;
    @Schema(description = "课程ID")
    private Long courseId;
    @Schema(description = "会员ID")
    private Long customerId;
}
