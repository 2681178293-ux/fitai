package net.wanho.fitai.modules.system.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.wanho.fitai.infra.page.PageQuery;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "系统参数查询参数")
public class SysConfigQueryDTO extends PageQuery  {
    @Schema(description = "参数名称")
    private String configName;
    @Schema(description = "参数键名")
    private String configKey;
    @Schema(description = "参数状态")
    private String status;

}
