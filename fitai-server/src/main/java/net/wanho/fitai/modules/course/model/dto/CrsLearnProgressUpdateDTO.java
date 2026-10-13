package net.wanho.fitai.modules.course.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;
import java.io.Serializable;
import net.wanho.fitai.modules.course.model.po.CrsLearnProgress;

@Data
@Schema(description = "课程学习进度修改参数")
@AutoMapper(target = CrsLearnProgress.class)
public class CrsLearnProgressUpdateDTO implements Serializable {

    @Schema(description = "进度ID")
    @NotNull(message = "进度ID不能为空")
    private Long progressId;
    @Schema(description = "用户ID")
    private Long customerId;
    @Schema(description = "课程ID")
    private Long courseId;
    @Schema(description = "课程名(冗余)")
    private String courseTitle;
    @Schema(description = "当前课时ID")
    private Long videoId;
    @Schema(description = "课时名(冗余)")
    private String videoTitle;
    @Schema(description = "进度%")
    private Integer progressPct;
    @Schema(description = "已学秒数")
    private Integer learnedSec;
    @Schema(description = "总秒数(冗余)")
    private Integer totalSec;
    @Schema(description = "完课（0否 1是）")
    private String isFinished;
    @Schema(description = "最后学习时间")
    private LocalDateTime lastLearnTime;
}
