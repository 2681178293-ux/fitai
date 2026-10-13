package net.wanho.fitai.modules.coach.model.po;


import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.time.LocalDateTime;
import java.io.Serializable;

@Data
public class UsrCoachBind implements Serializable {

    @TableId
    private Long bindId;
    private Long customerId;
    private Long coachId;
    private String coachName;
    private String bindType;
    /** 状态（0有效 1已解绑），对应库列 status */
    private String status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;
}
