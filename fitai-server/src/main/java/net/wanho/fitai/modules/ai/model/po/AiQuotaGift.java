package net.wanho.fitai.modules.ai.model.po;


import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.time.LocalDateTime;
import java.io.Serializable;

@Data
public class AiQuotaGift implements Serializable {

    @TableId
    private Long giftId;
    private Long customerId;
    private String quotaMonth;
    private Integer giftTokens;
    private Long benefitId;
    private String reason;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
