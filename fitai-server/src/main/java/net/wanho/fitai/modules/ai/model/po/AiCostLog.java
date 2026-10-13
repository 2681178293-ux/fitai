package net.wanho.fitai.modules.ai.model.po;


import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.io.Serializable;

@Data
public class AiCostLog implements Serializable {

    @TableId
    private Long costId;
    private Long customerId;
    private Long sessionId;
    private String modelCode;
    private Integer promptTokens;
    private Integer completionTokens;
    private Integer totalTokens;
    private BigDecimal costAmount;
    private String bizScene;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
