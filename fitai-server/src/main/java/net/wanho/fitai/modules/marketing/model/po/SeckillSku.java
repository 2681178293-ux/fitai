package net.wanho.fitai.modules.marketing.model.po;


import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.math.BigDecimal;
import java.io.Serializable;

@Data
public class SeckillSku implements Serializable {

    @TableId
    private Long id;
    private Long activityId;
    private Long skuId;
    private BigDecimal seckillPrice;
    private Integer totalStock;
    private Integer availableStock;
    private Integer limitPerUser;
    private Integer sortOrder;
    private Integer status;
}
