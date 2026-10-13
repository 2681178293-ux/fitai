package net.wanho.fitai.modules.marketing.model.po;


import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.math.BigDecimal;
import java.io.Serializable;

@Data
public class MktFlashSaleSku implements Serializable {

    @TableId
    private Long flashSkuId;
    private Long activityId;
    private Long spuId;
    private Long skuId;
    private String skuName;
    private BigDecimal flashPrice;
    private Integer flashStock;
    private Integer soldCount;
}
