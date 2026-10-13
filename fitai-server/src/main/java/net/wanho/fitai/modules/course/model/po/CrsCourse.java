package net.wanho.fitai.modules.course.model.po;


import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.io.Serializable;

@Data
public class CrsCourse implements Serializable {

    @TableId
    private Long courseId;
    private Long categoryId;
    private String categoryName;
    private String courseTitle;
    private Long coverOssId;
    private String coverUrl;
    private Long coachId;
    private String coachName;
    private String difficulty;
    private Integer durationMin;
    private Integer lessonCount;
    private BigDecimal price;
    private String isFree;
    private String summary;
    private Long videoId;
    private Integer calories;
    private String equipment;

    private Integer viewCount;
    private Integer likeCount;
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
