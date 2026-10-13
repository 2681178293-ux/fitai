package net.wanho.fitai.modules.mall.model.po;


import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.io.Serializable;

@Data
public class OrdRefund implements Serializable {

    @TableId
    private Long refundId;
    private String refundNo;
    private String orderType;
    private Long orderId;
    private String orderNo;
    private Long customerId;
    private BigDecimal refundAmount;
    private String reason;
    private String status;
    private String orderPrevStatus;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;
}
