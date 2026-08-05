package net.wanho.fitai.modules.system.code;

import net.wanho.fitai.infra.exception.ExceptionResult;

/**
 * 系统模块错误码 2001-2999
 */
public enum SystemCode implements ExceptionResult {
    USER_NOT_FOUND(2001, "用户不存在"),
    USER_DISABLED(2002, "用户已禁用"),
    PASSWORD_ERROR(2003, "密码错误"),
    USERNAME_EXISTS(2004, "用户名已存在"),
    SUPER_ADMIN_FORBIDDEN(2005, "不允许操作超级管理员"),
    ROLE_NOT_FOUND(2101, "角色不存在"),
    ROLE_KEY_EXISTS(2102, "角色标识已存在"),
    SUPER_ROLE_FORBIDDEN(2103, "不允许操作超级管理员角色"),
    MENU_NOT_FOUND(2201, "菜单不存在"),
    MENU_HAS_CHILDREN(2202, "存在子菜单，不允许删除"),
    DEPT_NOT_FOUND(2301, "部门不存在"),
    DEPT_HAS_CHILDREN(2302, "存在子部门，不允许删除"),
    DICT_TYPE_EXISTS(2401, "字典类型已存在"),
    CONFIG_KEY_EXISTS(2501, "配置键名已存在"),
    JOB_NOT_FOUND(2601, "定时任务不存在"),
    JOB_CRON_INVALID(2602, "Cron 表达式无效"),
    NOTICE_NOT_FOUND(2701, "公告不存在"),
    NOTICE_REVOKE_FORBIDDEN(2702, "仅已发布的公告可以撤回");

    private final int code;
    private final String msg;

    SystemCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    @Override
    public int code() {
        return this.code;
    }

    @Override
    public String msg() {
        return this.msg;
    }
}
