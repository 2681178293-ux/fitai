package net.wanho.fitai.modules.mall.model.po;


import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.time.LocalDateTime;
import java.io.Serializable;

@Data
public class PrdMqConsumeLog implements Serializable {

    @TableId
    private String messageId;
    private String bizType;
    private LocalDateTime consumeTime;
}
