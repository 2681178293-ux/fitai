package net.wanho.fitai.modules.community.model.po;


import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.time.LocalDateTime;
import java.io.Serializable;

@Data
public class CmsComment implements Serializable {

    @TableId
    private Long commentId;
    private Long postId;
    private Long customerId;
    private String customerName;
    private Long parentId;
    private String content;
    private Integer likeCount;
    private String auditStatus;
    private String status;
    private String delFlag;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
