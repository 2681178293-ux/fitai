package net.wanho.fitai.modules.system.model.dto;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import net.wanho.fitai.modules.system.model.po.SysConfig;

import java.io.Serializable;

@Data
@Schema(description = "新增系统配置参数")
@AutoMapper(target = SysConfig.class)
public class SysConfigAddDTO implements Serializable {
    @Schema(description = "系统配置名称")
    @NotBlank(message = "系统配置名称不能为空")
    private String configName;
    @Schema(description = "系统配置键")
    @NotBlank(message = "系统配置键不能为空")
    private String configKey;
    @Schema(description = "系统配置值")
    @NotBlank(message = "系统配置值不能为空")
    private String configValue;
    @Schema(description = "系统配置类型")
    @NotBlank(message = "系统配置类型不能为空")
    private String configType;
    @Schema(description = "系统配置状态")
    @NotBlank(message = "系统配置状态不能为空")
    private String status;
    @Schema(description = "系统配置备注")
    private String remark;

}
