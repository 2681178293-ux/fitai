package net.wanho.fitai.modules.member.model.po;


import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.time.LocalDateTime;
import java.io.Serializable;

@Data
public class MemSubscription implements Serializable {

    @TableId
    private Long subscriptionId;
    private Long customerId;
    private Long packageId;
    private String packageName;
    private Long memberOrderId;
    private LocalDateTime startTime;
    private LocalDateTime expireTime;
    private String autoRenew;
    private String status;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;
}
