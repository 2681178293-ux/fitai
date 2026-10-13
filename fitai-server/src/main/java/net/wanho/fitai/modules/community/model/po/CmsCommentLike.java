package net.wanho.fitai.modules.community.model.po;


import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.time.LocalDateTime;
import java.io.Serializable;

@Data
public class CmsCommentLike implements Serializable {

    @TableId
    private Long likeId;
    private Long commentId;
    private Long customerId;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
