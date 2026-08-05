package net.wanho.fitai.modules.system.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.wanho.fitai.infra.page.PageQuery;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "SysUser查询参数")
public class SysUserQueryDTO extends PageQuery {
    @Schema(description = "登录账号")
    private String userName;
    @Schema(description = "用户昵称")
    private String nickName;
    @Schema(description = "手机号")
    private String phonenumber;
    @Schema(description = "状态")
    private String status;
    @Schema(description = "部门ID")
    private Long deptId;

}
