package net.wanho.fitai.modules.system.model.dto;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import net.wanho.fitai.modules.system.model.po.SysDictType;
import java.io.Serializable;

@Data
@Schema(description = "修改SysDictType参数")
@AutoMapper(target = SysDictType.class)
public class SysDictTypeUpdateDTO implements Serializable {
    @Schema(description = "dictId")
    @NotNull(message = "dictId不能为空")
    private Long dictId;
    @Schema(description = "字典名称")
    private String dictName;
    @Schema(description = "字典类型")
    private String dictType;
    @Schema(description = "状态")
    private String status;
    @Schema(description = "备注")
    private String remark;

}
