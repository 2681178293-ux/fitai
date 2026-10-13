package net.wanho.fitai.modules.training.model.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.wanho.fitai.infra.page.PageQuery;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "设备绑定查询参数")
public class TrainDeviceBindQueryDTO extends PageQuery {

}
