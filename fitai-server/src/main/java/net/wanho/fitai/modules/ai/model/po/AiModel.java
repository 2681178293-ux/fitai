package net.wanho.fitai.modules.ai.model.po;


import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.io.Serializable;

@Data
public class AiModel implements Serializable {

    @TableId
    private Long modelId;
    private String modelName;
    private String modelCode;
    private String provider;
    private String apiModel;
    private String modelType;
    private BigDecimal temperature;
    private BigDecimal topP;
    private Integer maxTokens;
    private BigDecimal inputPrice;
    private BigDecimal outputPrice;
    private String baseUrl;
    private String apiKeyRef;
    private Integer sortOrder;
    private String isDefault;
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
