package net.wanho.fitai.modules.mall.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;
import java.io.Serializable;
import net.wanho.fitai.modules.mall.model.po.PrdMqConsumeLog;

@Data
@Schema(description = "新增MQ消费幂等日志")
@AutoMapper(target = PrdMqConsumeLog.class)
public class PrdMqConsumeLogAddDTO implements Serializable {

    @Schema(description = "业务类型(STOCK_RELEASE/STOCK_DEDUCT/STOCK_RESTORE/PRD_ES_SYNC)")
    private String bizType;
    @Schema(description = "消费时间")
    private LocalDateTime consumeTime;
}
