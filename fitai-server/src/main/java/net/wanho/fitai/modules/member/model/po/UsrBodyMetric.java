package net.wanho.fitai.modules.member.model.po;


import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.io.Serializable;

@Data
public class UsrBodyMetric implements Serializable {

    @TableId
    private Long metricId;
    private Long customerId;
    private LocalDate recordDate;
    private BigDecimal heightCm;
    private BigDecimal weightKg;
    private BigDecimal bmi;
    private BigDecimal bodyFatPct;
    private BigDecimal muscleKg;
    private BigDecimal waistCm;
    private BigDecimal chestCm;
    private BigDecimal hipCm;
    private BigDecimal armCm;
    private BigDecimal thighCm;
    private String source;
    private Long deviceId;
    private String remark;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
