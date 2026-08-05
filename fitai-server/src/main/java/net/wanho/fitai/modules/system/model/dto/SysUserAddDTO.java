package net.wanho.fitai.modules.system.model.dto;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import net.wanho.fitai.modules.system.model.po.SysUser;

import java.io.Serializable;
import java.util.List;

@Data
@Schema(description = "新增后台用户参数")
@AutoMapper(target = SysUser.class)
public class SysUserAddDTO implements Serializable {
    @Schema(description = "部门ID")
    private Long deptId;
    @Schema(description = "登录账号")
    @NotBlank(message = "登录账号不能为空")
    private String userName;
    @Schema(description = "用户昵称")
    @NotBlank(message = "用户昵称不能为空")
    private String nickName;
    @Schema(description = "用户类型")
    private String userType;
    @Schema(description = "邮箱")
    private String email;
    @Schema(description = "手机号")
    private String phonenumber;
    @Schema(description = "性别")
    private String sex;
    @Schema(description = "头像")
    private String avatar;
    @Schema(description = "密码")
    @NotBlank(message = "密码不能为空")
    private String password;
    @Schema(description = "状态")
    @NotBlank(message = "状态不能为空")
    private String status;
    @Schema(description = "角色ID列表")
    private List<Long> roleIds;
}
