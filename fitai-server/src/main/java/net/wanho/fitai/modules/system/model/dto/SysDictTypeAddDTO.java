package net.wanho.fitai.modules.system.model.dto;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import net.wanho.fitai.modules.system.model.po.SysDictType;
import java.io.Serializable;

@Data
@Schema(description = "新增SysDictType参数")
@AutoMapper(target = SysDictType.class)
public class SysDictTypeAddDTO implements Serializable {
    @Schema(description = "字典名称")
    @NotBlank(message = "字典名称不能为空")
    private String dictName;
    @Schema(description = "字典类型")
    @NotBlank(message = "字典类型不能为空")
    private String dictType;
    @Schema(description = "状态")
    @NotBlank(message = "状态不能为空")
    private String status;
    @Schema(description = "备注")
    private String remark;

}
