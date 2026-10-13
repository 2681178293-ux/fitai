package net.wanho.fitai.modules.member.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;
import java.io.Serializable;
import net.wanho.fitai.modules.member.model.po.MemCheckin;

@Data
@Schema(description = "每日签到修改参数")
@AutoMapper(target = MemCheckin.class)
public class MemCheckinUpdateDTO implements Serializable {

    @Schema(description = "签到ID")
    @NotNull(message = "签到ID不能为空")
    private Long checkinId;
    @Schema(description = "用户ID")
    private Long customerId;
    @Schema(description = "签到日期")
    private LocalDate checkinDate;
    @Schema(description = "连续签到天数")
    private Integer continuousDays;
    @Schema(description = "获得积分")
    private Integer pointsEarned;
}
