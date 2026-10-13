package net.wanho.fitai.modules.training.model.po;


import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.io.Serializable;

@Data
public class TrainPlan implements Serializable {

    @TableId
    private Long planId;
    private String planName;
    private Long customerId;
    private Long coachId;
    private String planType;
    private String goal;
    private Integer weeks;
    private Integer daysPerWeek;
    private Integer fitnessLevel;
    private LocalDate startDate;
    private LocalDate endDate;
    private String planJson;
    private Long aiSessionId;
    private String auditStatus;
    private String status;
    private String lifecycleStatus;
    private String genStatus;
    private String genErrorMsg;
    private String delFlag;
    @TableField(fill = FieldFill.INSERT)
    private String createBy;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.UPDATE)
    private String updateBy;
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;
}
