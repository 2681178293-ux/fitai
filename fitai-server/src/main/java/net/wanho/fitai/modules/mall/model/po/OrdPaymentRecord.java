package net.wanho.fitai.modules.mall.model.po;


import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.io.Serializable;

@Data
public class OrdPaymentRecord implements Serializable {

    @TableId
    private Long paymentId;
    private String paymentNo;
    private String mergeNo;
    private String orderType;
    private Long orderId;
    private String orderNo;
    private Long customerId;
    private String channelCode;
    private BigDecimal payAmount;
    private String transactionId;
    private String payStatus;
    private LocalDateTime payTime;
    private String callbackRaw;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;
}
