package net.wanho.fitai.modules.marketing.model.po;


import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.io.Serializable;

@Data
public class SeckillOrder implements Serializable {

    @TableId
    private Long id;
    private String orderNo;
    private Long userId;
    private Long activityId;
    private Long skuId;
    private BigDecimal seckillPrice;
    private Integer quantity;
    private Integer status;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    private LocalDateTime payTime;
    private LocalDateTime cancelTime;
}
