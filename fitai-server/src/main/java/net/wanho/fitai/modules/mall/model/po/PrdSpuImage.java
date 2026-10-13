package net.wanho.fitai.modules.mall.model.po;


import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.time.LocalDateTime;
import java.io.Serializable;

@Data
public class PrdSpuImage implements Serializable {

    @TableId
    private Long imageId;
    private Long spuId;
    private Long ossId;
    private String imageUrl;
    private Integer orderNum;
    /** 库字段为 is_main（历史 main_flag 已迁移） */
    @TableField("is_main")
    private String mainFlag;
    private String delFlag;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
