package net.wanho.fitai.modules.system.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "SysMenu查询参数")
public class SysMenuQueryDTO  {
    @Schema(description = "菜单名称")
    private String menuName;
    @Schema(description = "状态")
    private String status;
    @Schema(description = "菜单类型")
    private String menuType;

}
