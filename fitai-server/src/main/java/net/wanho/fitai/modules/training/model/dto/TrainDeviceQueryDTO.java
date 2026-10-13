package net.wanho.fitai.modules.training.model.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.wanho.fitai.infra.page.PageQuery;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "穿戴设备查询参数")
public class TrainDeviceQueryDTO extends PageQuery {

    @Schema(description = "设备名称")
    private String deviceName;
    @Schema(description = "状态（0正常 1停用）")
    private String status;
}
