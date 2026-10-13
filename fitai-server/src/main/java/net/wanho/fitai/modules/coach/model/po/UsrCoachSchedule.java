package net.wanho.fitai.modules.coach.model.po;


import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.time.LocalDateTime;
import java.io.Serializable;

@Data
public class UsrCoachSchedule implements Serializable {

    @TableId
    private Long scheduleId;
    private Long coachId;
    private String workDays;
    private String workSlots;
    private String leaveDates;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;
}
