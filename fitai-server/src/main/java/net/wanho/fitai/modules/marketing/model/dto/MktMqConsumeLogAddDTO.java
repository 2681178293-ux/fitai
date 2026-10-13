package net.wanho.fitai.modules.marketing.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;
import java.io.Serializable;
import net.wanho.fitai.modules.marketing.model.po.MktMqConsumeLog;

@Data
@Schema(description = "新增MQ消费幂等日志")
@AutoMapper(target = MktMqConsumeLog.class)
public class MktMqConsumeLogAddDTO implements Serializable {

    @Schema(description = "业务类型")
    private String bizType;
    @Schema(description = "消费时间")
    private LocalDateTime consumeTime;
}
