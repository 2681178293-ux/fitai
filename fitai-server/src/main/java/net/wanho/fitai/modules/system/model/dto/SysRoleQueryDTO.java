package net.wanho.fitai.modules.system.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.wanho.fitai.infra.page.PageQuery;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "SysRole查询参数")
public class SysRoleQueryDTO extends PageQuery {
    @Schema(description = "角色名称")
    private String roleName;
    @Schema(description = "权限字符")
    private String roleKey;
    @Schema(description = "状态")
    private String status;

}
