package net.wanho.fitai.modules.training.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.io.Serializable;
import net.wanho.fitai.modules.training.model.po.TrainDeviceBind;

@Data
@Schema(description = "新增设备绑定")
@AutoMapper(target = TrainDeviceBind.class)
public class TrainDeviceBindAddDTO implements Serializable {

    @Schema(description = "用户ID")
    @NotNull(message = "用户ID不能为空")
    private Long customerId;
    @Schema(description = "设备ID")
    @NotNull(message = "设备ID不能为空")
    private Long deviceId;
    @Schema(description = "设备序列号")
    private String deviceSn;
    @Schema(description = "状态（0已绑定 1已解绑）")
    private String bindStatus;
    @Schema(description = "最后同步")
    private LocalDateTime lastSyncTime;
}
