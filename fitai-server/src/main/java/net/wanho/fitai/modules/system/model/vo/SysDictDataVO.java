package net.wanho.fitai.modules.system.model.vo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import net.wanho.fitai.modules.system.model.po.SysDictData;
import java.io.Serializable;

@Data
@Schema(description = "SysDictData视图")
@AutoMapper(target = SysDictData.class)
public class SysDictDataVO implements Serializable {
    @Schema(description = "字典编码")
    private Long dictCode;
    @Schema(description = "排序")
    private Integer dictSort;
    @Schema(description = "字典标签")
    private String dictLabel;
    @Schema(description = "字典键值")
    private String dictValue;
    @Schema(description = "字典类型")
    private String dictType;
    @Schema(description = "样式")
    private String cssClass;
    @Schema(description = "状态")
    private String status;
    @Schema(description = "备注")
    private String remark;

}
