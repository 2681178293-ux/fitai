package net.wanho.fitai.modules.mall.model.po;


import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.time.LocalDateTime;
import java.io.Serializable;

@Data
public class PrdStock implements Serializable {

    @TableId
    private Long stockId;
    private Long skuId;
    private String warehouseCode;
    private Integer quantity;
    private Integer lockedQty;
    private Integer lowStock;
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;
}
