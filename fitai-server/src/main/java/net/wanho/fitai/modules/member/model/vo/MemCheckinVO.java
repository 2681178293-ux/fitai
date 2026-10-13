package net.wanho.fitai.modules.member.model.vo;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import net.wanho.fitai.modules.member.model.po.MemCheckin;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Schema(description = "每日签到视图")
@AutoMapper(target = MemCheckin.class)
public class MemCheckinVO implements Serializable {

    @Schema(description = "签到ID")
    private Long checkinId;
    @Schema(description = "用户ID")
    private Long customerId;
    @Schema(description = "签到日期")
    private LocalDate checkinDate;
    @Schema(description = "连续签到天数")
    private Integer continuousDays;
    @Schema(description = "获得积分")
    private Integer pointsEarned;
    @Schema(description = "签到时间")
    private LocalDateTime createTime;

    @Schema(description = "用户昵称")
    private String nickName;
}
