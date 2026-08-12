package net.wanho.fitai.modules.system.model.dto;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import net.wanho.fitai.modules.system.model.po.SysRole;

import java.io.Serializable;
import java.util.List;

@Data
@Schema(description = "修改角色参数")
@AutoMapper(target = SysRole.class)
public class SysRoleUpdateDTO implements Serializable {
    @Schema(description = "角色ID")
    @NotNull(message = "角色ID不能为空")
    private Long roleId;
    @Schema(description = "角色名称")
    private String roleName;
    @Schema(description = "权限字符")
    private String roleKey;
    @Schema(description = "显示顺序")
    private Integer roleSort;
    @Schema(description = "数据范围")
    private String dataScope;
    @Schema(description = "状态")
    private String status;
    @Schema(description = "备注")
    private String remark;
    @Schema(description = "菜单列表")
    private List<Long> menuIds;

}
