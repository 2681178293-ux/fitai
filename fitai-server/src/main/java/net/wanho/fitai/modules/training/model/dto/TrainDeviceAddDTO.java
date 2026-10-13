package net.wanho.fitai.modules.training.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import net.wanho.fitai.modules.training.model.po.TrainDevice;

@Data
@Schema(description = "新增穿戴设备")
@AutoMapper(target = TrainDevice.class)
public class TrainDeviceAddDTO implements Serializable {

    @Schema(description = "设备名称")
    @NotBlank(message = "设备名称不能为空")
    private String deviceName;
    @Schema(description = "品牌 apple/huawei/xiaomi")
    @NotBlank(message = "品牌 apple不能为空")
    private String deviceBrand;
    @Schema(description = "型号")
    private String deviceModel;
    @Schema(description = "连接方式 ble/api")
    private String connectType;
    @Schema(description = "状态（0正常 1停用）")
    private String status;
}
