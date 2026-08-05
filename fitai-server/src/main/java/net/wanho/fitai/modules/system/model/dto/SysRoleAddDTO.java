package net.wanho.fitai.modules.system.model.dto;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import net.wanho.fitai.modules.system.model.po.SysRole;

import java.io.Serializable;
import java.util.List;

@Data
@Schema(description = "新增角色参数")
@AutoMapper(target = SysRole.class)
public class SysRoleAddDTO implements Serializable {
    @Schema(description = "角色名称")
    @NotBlank(message = "角色名称不能为空")
    private String roleName;
    @Schema(description = "权限字符")
    @NotBlank(message = "权限字符不能为空")
    private String roleKey;
    @Schema(description = "显示顺序")
    private Integer roleSort;
    @Schema(description = "数据范围")
    private String dataScope;
    @Schema(description = "状态")
    @NotBlank(message = "状态不能为空")
    private String status;
    @Schema(description = "备注")
    private String remark;
    @Schema(description = "菜单ID列表")
    private List<Long> menuIds;
    @Schema(description = "部门ID列表")
    private List<Long> deptIds;
}
