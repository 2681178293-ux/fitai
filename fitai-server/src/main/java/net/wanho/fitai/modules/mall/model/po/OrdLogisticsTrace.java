package net.wanho.fitai.modules.mall.model.po;


import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.time.LocalDateTime;
import java.io.Serializable;

@Data
public class OrdLogisticsTrace implements Serializable {

    @TableId
    private Long traceId;
    private Long logisticsId;
    private LocalDateTime traceTime;
    private String traceDesc;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
