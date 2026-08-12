package net.wanho.fitai.modules.system.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import net.wanho.fitai.modules.system.model.po.SysUser;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "后台用户视图")
@AutoMapper(target = SysUser.class)
public class SysUserVO implements Serializable {
    @Schema(description = "用户ID")
    private Long userId;
    @Schema(description = "部门ID")
    private Long deptId;
    @Schema(description = "登录账号")
    private String userName;
    @Schema(description = "用户昵称")
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
    @Schema(description = "状态")
    private String status;
    @Schema(description = "最后登录IP")
    private String loginIp;
    @Schema(description = "最后登录时间")
    private LocalDateTime loginDate;
    @Schema(description = "角色ID列表")
    private List<Long> roleIds;
    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;
}
