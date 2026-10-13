package net.wanho.fitai.modules.mall.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.io.Serializable;
import net.wanho.fitai.modules.mall.model.po.OrdLogisticsTrace;

@Data
@Schema(description = "新增物流轨迹")
@AutoMapper(target = OrdLogisticsTrace.class)
public class OrdLogisticsTraceAddDTO implements Serializable {

    @Schema(description = "物流ID")
    @NotNull(message = "物流ID不能为空")
    private Long logisticsId;
    @Schema(description = "轨迹时间")
    @NotNull(message = "轨迹时间不能为空")
    private LocalDateTime traceTime;
    @Schema(description = "轨迹描述")
    @NotBlank(message = "轨迹描述不能为空")
    private String traceDesc;
}
