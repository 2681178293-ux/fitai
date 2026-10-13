package net.wanho.fitai.modules.coach.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.io.Serializable;
import net.wanho.fitai.modules.coach.model.po.UsrCoachBooking;

@Data
@Schema(description = "新增教练约课预约单")
@AutoMapper(target = UsrCoachBooking.class)
public class UsrCoachBookingAddDTO implements Serializable {

    @Schema(description = "预约单号")
    @NotBlank(message = "预约单号不能为空")
    private String bookingNo;
    @Schema(description = "教练ID")
    @NotNull(message = "教练ID不能为空")
    private Long coachId;
    @Schema(description = "教练名(冗余)")
    private String coachName;
    @Schema(description = "用户ID")
    @NotNull(message = "用户ID不能为空")
    private Long customerId;
    @Schema(description = "昵称(冗余)")
    private String customerName;
    @Schema(description = "预约日期")
    @NotNull(message = "预约日期不能为空")
    private LocalDate bookingDate;
    @Schema(description = "时段(如 09:00-10:00)")
    @NotBlank(message = "时段不能为空")
    private String timeSlot;
    @Schema(description = "备注(训练目标等)")
    private String remark;
    @Schema(description = "状态（0待确认 1已确认 2已完成 3已取消）")
    private String status;
    @Schema(description = "取消原因")
    private String cancelReason;
}
