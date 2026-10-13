package net.wanho.fitai.modules.course.model.po;


import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.time.LocalDateTime;
import java.io.Serializable;

@Data
public class CrsLearnProgress implements Serializable {

    @TableId
    private Long progressId;
    private Long customerId;
    private Long courseId;
    private String courseTitle;
    private Long videoId;
    private String videoTitle;
    private Integer progressPct;
    private Integer learnedSec;
    private Integer totalSec;
    private String isFinished;
    private LocalDateTime lastLearnTime;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;
}
