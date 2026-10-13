package net.wanho.fitai.modules.member.model.po;


import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.time.LocalDateTime;
import java.io.Serializable;

@Data
public class UsrSegmentRel implements Serializable {

    @TableId
    private Long segmentRelId;
    private Long segmentId;
    private Long customerId;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
