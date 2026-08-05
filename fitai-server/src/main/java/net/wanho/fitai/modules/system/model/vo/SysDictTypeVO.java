package net.wanho.fitai.modules.system.model.vo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import net.wanho.fitai.modules.system.model.po.SysDictType;
import java.io.Serializable;

@Data
@Schema(description = "SysDictType视图")
@AutoMapper(target = SysDictType.class)
public class SysDictTypeVO implements Serializable {
    @Schema(description = "字典主键")
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
