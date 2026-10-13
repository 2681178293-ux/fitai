package net.wanho.fitai.modules.marketing.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.io.Serializable;
import net.wanho.fitai.modules.marketing.model.po.MktChallenge;

@Data
@Schema(description = "新增挑战赛")
@AutoMapper(target = MktChallenge.class)
public class MktChallengeAddDTO implements Serializable {

    @Schema(description = "挑战名称")
    @NotBlank(message = "挑战名称不能为空")
    private String name;
    @Schema(description = "描述")
    private String description;
    @Schema(description = "封面图")
    private String coverImage;
    @Schema(description = "类型：plank/pushup/running/checkin")
    private String type;
    @Schema(description = "目标天数")
    private Integer targetDays;
    @Schema(description = "目标值")
    private Integer targetValue;
    @Schema(description = "开始时间")
    private LocalDateTime startTime;
    @Schema(description = "结束时间")
    private LocalDateTime endTime;
    @Schema(description = "参与人数")
    private Integer participantCount;
    @Schema(description = "状态：0下线 1上线")
    private Integer status;
}
