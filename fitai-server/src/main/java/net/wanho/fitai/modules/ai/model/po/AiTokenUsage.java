package net.wanho.fitai.modules.ai.model.po;


import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.time.LocalDateTime;
import java.io.Serializable;

@Data
public class AiTokenUsage implements Serializable {

    @TableId
    private Long usageId;
    private Long customerId;
    private String scene;
    private String modelCode;
    private Integer promptTokens;
    private Integer completionTokens;
    private Integer totalTokens;
    private Long bizId;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
