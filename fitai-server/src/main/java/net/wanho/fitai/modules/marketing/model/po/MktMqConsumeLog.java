package net.wanho.fitai.modules.marketing.model.po;


import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.time.LocalDateTime;
import java.io.Serializable;

@Data
public class MktMqConsumeLog implements Serializable {

    @TableId
    private String messageId;
    private String bizType;
    private LocalDateTime consumeTime;
}
