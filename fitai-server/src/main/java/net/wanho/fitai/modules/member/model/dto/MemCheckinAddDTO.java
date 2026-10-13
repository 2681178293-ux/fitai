package net.wanho.fitai.modules.member.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.io.Serializable;
import net.wanho.fitai.modules.member.model.po.MemCheckin;

@Data
@Schema(description = "新增每日签到")
@AutoMapper(target = MemCheckin.class)
public class MemCheckinAddDTO implements Serializable {

    @Schema(description = "用户ID")
    @NotNull(message = "用户ID不能为空")
    private Long customerId;
    @Schema(description = "签到日期")
    @NotNull(message = "签到日期不能为空")
    private LocalDate checkinDate;
    @Schema(description = "连续签到天数")
    private Integer continuousDays;
    @Schema(description = "获得积分")
    private Integer pointsEarned;
}
