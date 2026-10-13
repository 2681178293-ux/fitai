package net.wanho.fitai.modules.training.model.po;


import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.time.LocalDateTime;
import java.io.Serializable;

@Data
public class TrainDeviceBind implements Serializable {

    @TableId
    private Long bindId;
    private Long customerId;
    private Long deviceId;
    private String deviceSn;
    private String bindStatus;
    private LocalDateTime lastSyncTime;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;
}
