package net.wanho.fitai.modules.coach.model.vo;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import net.wanho.fitai.modules.coach.model.po.UsrCoachSchedule;

@Data
@Schema(description = "教练排课表(周模板+请假)视图")
@AutoMapper(target = UsrCoachSchedule.class)
public class UsrCoachScheduleVO implements Serializable {

    @Schema(description = "排课ID")
    private Long scheduleId;
    @Schema(description = "教练ID")
    private Long coachId;
    @Schema(description = "出勤星期(1周一~7周日,逗号分隔)")
    private String workDays;
    @Schema(description = "开放时段(逗号分隔,空=全部默认时段)")
    private String workSlots;
    @Schema(description = "请假日期(yyyy-MM-dd,逗号分隔)")
    private String leaveDates;
}
