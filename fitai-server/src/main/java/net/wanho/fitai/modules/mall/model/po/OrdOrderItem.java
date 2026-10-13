package net.wanho.fitai.modules.mall.model.po;


import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.math.BigDecimal;
import java.io.Serializable;

@Data
public class OrdOrderItem implements Serializable {

    @TableId
    private Long itemId;
    private Long orderId;
    private String orderNo;
    private Long spuId;
    private Long skuId;
    private String skuName;
    private String skuSn;
    private String skuSpecs;
    private String coverUrl;
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal subtotal;
}
