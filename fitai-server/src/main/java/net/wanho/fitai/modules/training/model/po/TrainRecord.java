package net.wanho.fitai.modules.training.model.po;


import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.io.Serializable;

@Data
public class TrainRecord implements Serializable {

    @TableId
    private Long recordId;
    private Long customerId;
    private Long planId;
    private LocalDate trainDate;
    private Integer durationMin;
    private BigDecimal calories;
    private Integer completionPct;
    private Integer feeling;
    private String remark;
    private String source;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
