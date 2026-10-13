package net.wanho.fitai.modules.course.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import net.wanho.fitai.modules.course.model.po.CrsCourseLike;

@Data
@Schema(description = "新增课程点赞")
@AutoMapper(target = CrsCourseLike.class)
public class CrsCourseLikeAddDTO implements Serializable {

    @Schema(description = "课程ID")
    @NotNull(message = "课程ID不能为空")
    private Long courseId;
    @Schema(description = "会员ID")
    @NotNull(message = "会员ID不能为空")
    private Long customerId;
}
