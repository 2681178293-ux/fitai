package net.wanho.fitai.modules.mall.model.po;


import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.io.Serializable;

@Data
public class OrdMemberOrder implements Serializable {

    @TableId
    private Long orderId;
    private String orderNo;
    private Long customerId;
    private Long packageId;
    private String packageName;
    private Integer durationDays;
    private BigDecimal payAmount;
    private String payChannel;
    private LocalDateTime payTime;
    private LocalDateTime expireTime;
    private String status;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;
}
