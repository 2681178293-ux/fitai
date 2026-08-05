package net.wanho.fitai.modules.system.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.wanho.fitai.infra.page.PageQuery;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "SysDictData查询参数")
public class SysDictDataQueryDTO extends PageQuery {
    @Schema(description = "字典标签")
    private String dictLabel;
    @Schema(description = "字典类型")
    private String dictType;
    @Schema(description = "状态")
    private String status;

}
