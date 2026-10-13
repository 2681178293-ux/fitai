package net.wanho.fitai.modules.marketing.model.vo;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;
import java.io.Serializable;
import net.wanho.fitai.modules.marketing.model.po.MktChallenge;

@Data
@Schema(description = "挑战赛视图")
@AutoMapper(target = MktChallenge.class)
public class MktChallengeVO implements Serializable {

    @Schema(description = "挑战ID")
    private Long id;
    @Schema(description = "挑战名称")
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
