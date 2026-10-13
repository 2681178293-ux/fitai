package net.wanho.fitai.modules.marketing.model.po;


import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.time.LocalDateTime;
import java.io.Serializable;

@Data
public class MktCouponUser implements Serializable {

    @TableId
    private Long id;
    private Long couponId;
    private Long userId;
    private Integer status;
    private String orderNo;
    private LocalDateTime useTime;
    private LocalDateTime expireTime;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
