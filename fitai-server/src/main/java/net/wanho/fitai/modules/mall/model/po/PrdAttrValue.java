package net.wanho.fitai.modules.mall.model.po;


import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.io.Serializable;

@Data
public class PrdAttrValue implements Serializable {

    @TableId
    private Long valueId;
    private Long attrId;
    private String attrValue;
    private String orderNum;
    private String status;
    private String delFlag;
}
