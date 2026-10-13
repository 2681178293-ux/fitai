package net.wanho.fitai.modules.training.model.vo;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import net.wanho.fitai.modules.training.model.po.TrainDevice;

@Data
@Schema(description = "穿戴设备视图")
@AutoMapper(target = TrainDevice.class)
public class TrainDeviceVO implements Serializable {

    @Schema(description = "设备ID")
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
