package net.wanho.fitai.modules.system.model.dto;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import net.wanho.fitai.modules.system.model.po.SysConfig;

import java.io.Serializable;

@Data
@Schema(description = "系统参数修改参数")
@AutoMapper(target = SysConfig.class)
public class SysConfigUpdateDTO implements Serializable  {
    @Schema(description = "参数ID")
    @NotNull(message = "参数ID不能为空")
    private Long configId;
    @Schema(description = "参数名称")
    private String configName;
    @Schema(description = "参数键名")
    private String configKey;
    @Schema(description = "参数键值")
    private String configValue;
    @Schema(description = "系统内置")
    private String configType;
    @Schema(description = "状态")
    private String status;
    @Schema(description = "备注")
    private String remark;

}
