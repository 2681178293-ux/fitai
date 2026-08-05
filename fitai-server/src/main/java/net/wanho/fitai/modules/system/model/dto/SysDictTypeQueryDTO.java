package net.wanho.fitai.modules.system.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.wanho.fitai.infra.page.PageQuery;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "SysDictType查询参数")
public class SysDictTypeQueryDTO extends PageQuery {
    @Schema(description = "字典名称")
    private String dictName;
    @Schema(description = "字典类型")
    private String dictType;
    @Schema(description = "状态")
    private String status;

}
