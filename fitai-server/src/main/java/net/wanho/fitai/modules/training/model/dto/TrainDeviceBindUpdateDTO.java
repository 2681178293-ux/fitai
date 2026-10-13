package net.wanho.fitai.modules.training.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;
import java.io.Serializable;
import net.wanho.fitai.modules.training.model.po.TrainDeviceBind;

@Data
@Schema(description = "设备绑定修改参数")
@AutoMapper(target = TrainDeviceBind.class)
public class TrainDeviceBindUpdateDTO implements Serializable {

    @Schema(description = "绑定ID")
    @NotNull(message = "绑定ID不能为空")
    private Long bindId;
    @Schema(description = "用户ID")
    private Long customerId;
    @Schema(description = "设备ID")
    private Long deviceId;
    @Schema(description = "设备序列号")
    private String deviceSn;
    @Schema(description = "状态（0已绑定 1已解绑）")
    private String bindStatus;
    @Schema(description = "最后同步")
    private LocalDateTime lastSyncTime;
}
