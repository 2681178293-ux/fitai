package net.wanho.fitai.modules.mall.model.po;


import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.io.Serializable;

@Data
public class PrdSpuDetail implements Serializable {

    @TableId
    private Long detailId;
    private Long spuId;
    private String description;
    private String detailHtml;
}
