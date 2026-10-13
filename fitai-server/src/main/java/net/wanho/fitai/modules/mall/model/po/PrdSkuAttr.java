package net.wanho.fitai.modules.mall.model.po;


import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.io.Serializable;

@Data
public class PrdSkuAttr implements Serializable {

    @TableId
    private Long skuId;
    private Long attrId;
    private Long valueId;
}
