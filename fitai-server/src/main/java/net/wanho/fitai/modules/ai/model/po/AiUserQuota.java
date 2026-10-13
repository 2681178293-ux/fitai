package net.wanho.fitai.modules.ai.model.po;


import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.time.LocalDateTime;
import java.io.Serializable;

@Data
public class AiUserQuota implements Serializable {

    @TableId
    private Long quotaId;
    private Long customerId;
    private String quotaMonth;
    private Integer quotaTotal;
    private Integer quotaUsed;
    private Integer quotaRemain;
    private String sourceType;
    private Long sourceId;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;
}
