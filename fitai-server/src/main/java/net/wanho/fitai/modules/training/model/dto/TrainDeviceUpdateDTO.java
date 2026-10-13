package net.wanho.fitai.modules.training.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.io.Serializable;
import net.wanho.fitai.modules.training.model.po.TrainDevice;

@Data
@Schema(description = "穿戴设备修改参数")
@AutoMapper(target = TrainDevice.class)
public class TrainDeviceUpdateDTO implements Serializable {

    @Schema(description = "设备ID")
    @NotNull(message = "设备ID不能为空")
    private Long deviceId;
    @Schema(description = "设备名称")
    private String deviceName;
    @Schema(description = "品牌 apple/huawei/xiaomi")
    private String deviceBrand;
    @Schema(description = "型号")
    private String deviceModel;
    @Schema(description = "连接方式 ble/api")
    private String connectType;
    @Schema(description = "状态（0正常 1停用）")
    private String status;
}
