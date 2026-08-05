package net.wanho.fitai.infra.auth.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 管理端登录缓存信息（存入 Sa-Token Session，避免重复查库）。
 */
@Data
public class LoginUser implements Serializable {

    private Long userId;
    private String userName;
    private String nickName;
    private Long deptId;
    private String deptName;
    /** 角色 key 列表（供 {@code @SaCheckRole} 使用） */
    private List<String> roles;
    /** 权限标识列表（供 {@code @SaCheckPermission} 使用） */
    private List<String> permissions;
}
