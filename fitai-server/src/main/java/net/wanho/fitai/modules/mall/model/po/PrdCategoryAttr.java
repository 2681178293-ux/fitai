package net.wanho.fitai.modules.mall.model.po;


import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.io.Serializable;

@Data
public class PrdCategoryAttr implements Serializable {

    @TableId
    private Long categoryId;
    private Long attrId;
}
