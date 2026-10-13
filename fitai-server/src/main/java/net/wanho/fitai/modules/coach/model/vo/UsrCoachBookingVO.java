package net.wanho.fitai.modules.coach.model.vo;


import com.fasterxml.jackson.annotation.JsonFormat;
import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import net.wanho.fitai.modules.coach.model.po.UsrCoachBooking;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Schema(description = "教练约课预约单视图")
@AutoMapper(target = UsrCoachBooking.class)
public class UsrCoachBookingVO implements Serializable {

    @Schema(description = "预约ID")
    private Long bookingId;
    @Schema(description = "预约单号")
    private String bookingNo;
    @Schema(description = "教练ID")
    private Long coachId;
    @Schema(description = "教练名(冗余)")
    private String coachName;
    @Schema(description = "用户ID")
    private Long customerId;
    @Schema(description = "昵称(冗余)")
    private String customerName;
    @Schema(description = "预约日期")
    private LocalDate bookingDate;
    @Schema(description = "时段(如 09:00-10:00)")
    private String timeSlot;
    @Schema(description = "备注(训练目标等)")
    private String remark;
    @Schema(description = "状态（0待确认 1已确认 2已完成 3已取消）")
    private String status;
    @Schema(description = "取消原因")
    private String cancelReason;
    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
