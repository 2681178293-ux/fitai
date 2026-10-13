package net.wanho.fitai.modules.community.model.po;


import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.time.LocalDateTime;
import java.io.Serializable;

@Data
public class CmsModeration implements Serializable {

    @TableId
    private Long moderationId;
    private String targetType;
    private Long targetId;
    private String contentSnapshot;
    private String auditStatus;
    private String auditReason;
    private Long auditorId;
    private LocalDateTime auditTime;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
