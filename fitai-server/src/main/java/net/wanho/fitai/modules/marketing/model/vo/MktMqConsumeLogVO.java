package net.wanho.fitai.modules.marketing.model.vo;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;
import java.io.Serializable;
import net.wanho.fitai.modules.marketing.model.po.MktMqConsumeLog;

@Data
@Schema(description = "MQ消费幂等日志视图")
@AutoMapper(target = MktMqConsumeLog.class)
public class MktMqConsumeLogVO implements Serializable {

    @Schema(description = "消息ID(幂等键)")
    private String messageId;
    @Schema(description = "业务类型")
    private String bizType;
    @Schema(description = "消费时间")
    private LocalDateTime consumeTime;
}
