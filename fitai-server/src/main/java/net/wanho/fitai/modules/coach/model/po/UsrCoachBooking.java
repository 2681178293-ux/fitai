package net.wanho.fitai.modules.coach.model.po;


import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.io.Serializable;

@Data
public class UsrCoachBooking implements Serializable {

    @TableId
    private Long bookingId;
    private String bookingNo;
    private Long coachId;
    private String coachName;
    private Long customerId;
    private String customerName;
    private LocalDate bookingDate;
    private String timeSlot;
    private String remark;
    private String status;
    private String cancelReason;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;
}
