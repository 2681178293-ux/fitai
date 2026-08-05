package net.wanho.fitai.modules.system.model.dto;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import net.wanho.fitai.modules.system.model.po.SysDictData;
import java.io.Serializable;

@Data
@Schema(description = "新增SysDictData参数")
@AutoMapper(target = SysDictData.class)
public class SysDictDataAddDTO implements Serializable {
    @Schema(description = "排序")
    private Integer dictSort;
    @Schema(description = "字典标签")
    @NotBlank(message = "字典标签不能为空")
    private String dictLabel;
    @Schema(description = "字典键值")
    @NotBlank(message = "字典键值不能为空")
    private String dictValue;
    @Schema(description = "字典类型")
    @NotBlank(message = "字典类型不能为空")
    private String dictType;
    @Schema(description = "样式")
    private String cssClass;
    @Schema(description = "状态")
    @NotBlank(message = "状态不能为空")
    private String status;
    @Schema(description = "备注")
    private String remark;

}
