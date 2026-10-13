package net.wanho.fitai.modules.training.model.po;


import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.io.Serializable;

@Data
public class TrainAction implements Serializable {

    @TableId
    private Long actionId;
    private String actionName;
    private String actionCode;
    private String muscleGroup;
    private String equipment;
    private Integer difficulty;
    private BigDecimal caloriesPerMin;
    private String tips;
    private String commonMistakes;
    private Integer sortOrder;
    private Long vodAssetId;
    private String vodVideoId;
    private String vodCoverUrl;
    private BigDecimal vodDuration;
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
