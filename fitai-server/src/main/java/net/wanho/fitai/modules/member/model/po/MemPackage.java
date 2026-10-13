package net.wanho.fitai.modules.member.model.po;


import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.io.Serializable;

@Data
public class MemPackage implements Serializable {

    @TableId
    private Long packageId;
    private String packageName;
    private String packageType;
    private Integer durationDays;
    private BigDecimal price;
    private BigDecimal origPrice;
    private Long levelId;
    private String levelName;
    private Integer aiQuotaMonthly;
    private Integer aiQuotaDailyTokens;
    private Integer aiDailyChatLimit;
    private String description;
    private String status;
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
