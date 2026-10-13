package net.wanho.fitai.modules.mall.model.vo;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;
import java.io.Serializable;
import net.wanho.fitai.modules.mall.model.po.OrdLogisticsTrace;

@Data
@Schema(description = "物流轨迹视图")
@AutoMapper(target = OrdLogisticsTrace.class)
public class OrdLogisticsTraceVO implements Serializable {

    @Schema(description = "轨迹ID")
    private Long traceId;
    @Schema(description = "物流ID")
    private Long logisticsId;
    @Schema(description = "轨迹时间")
    private LocalDateTime traceTime;
    @Schema(description = "轨迹描述")
    private String traceDesc;
}
