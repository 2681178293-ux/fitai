package net.wanho.fitai.modules.mall.model.po;


import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.io.Serializable;

@Data
public class PrdCart implements Serializable {

    @TableId
    private Long cartId;
    private Long customerId;
    private Long spuId;
    private Long skuId;
    private String skuName;
    private String skuSn;
    private String coverUrl;
    private BigDecimal price;
    private Integer quantity;
    private String selected;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;
}
