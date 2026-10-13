package net.wanho.fitai.modules.marketing.model.po;


import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.time.LocalDateTime;
import java.io.Serializable;

@Data
public class MktBanner implements Serializable {

    @TableId
    private Long id;
    private String title;
    private String imageUrl;
    private String linkType;
    private String linkValue;
    private String position;
    private Integer sortOrder;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer status;
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
