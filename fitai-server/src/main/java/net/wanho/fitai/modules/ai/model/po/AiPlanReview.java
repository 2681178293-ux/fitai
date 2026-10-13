package net.wanho.fitai.modules.ai.model.po;


import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.time.LocalDateTime;
import java.io.Serializable;

@Data
public class AiPlanReview implements Serializable {

    @TableId
    private Long reviewId;
    private String planType;
    private Long planId;
    private Long customerId;
    private Long sessionId;
    private String contentSnapshot;
    private String auditStatus;
    private String auditReason;
    private Long auditorId;
    private LocalDateTime auditTime;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
