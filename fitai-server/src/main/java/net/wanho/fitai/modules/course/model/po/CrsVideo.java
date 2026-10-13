package net.wanho.fitai.modules.course.model.po;


import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.io.Serializable;

@Data
public class CrsVideo implements Serializable {

    @TableId
    private Long videoId;
    private Long courseId;
    private String courseTitle;
    private String videoTitle;
    private Integer orderNum;
    private Long vodAssetId;
    private Long vodCategoryId;
    private String vodCategoryName;
    private Long coverOssId;
    private String coverUrl;
    private String vodVideoId;
    private String vodCoverUrl;
    private String vodPlayUrl;
    private BigDecimal vodDuration;
    private String vodStatus;
    private String isPreview;
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
