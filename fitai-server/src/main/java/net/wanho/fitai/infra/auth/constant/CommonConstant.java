package net.wanho.fitai.infra.auth.constant;

/**
 * 通用常量（状态值与业务约定，对齐库表 char 语义）。
 */
public final class CommonConstant {

    /** 超级管理员用户 ID（拥有全部角色/权限） */
    public static final Long SUPER_ADMIN_ID = 1L;

    /** 正常状态（sys_user / usr_customer：0 正常） */
    public static final String STATUS_NORMAL = "0";

    /** 停用/冻结状态 */
    public static final String STATUS_DISABLED = "1";

    /** 用户类型：管理端 */
    public static final String USER_TYPE_ADMIN = "admin";

    /** 用户类型：C 端客户 */
    public static final String USER_TYPE_CUSTOMER = "customer";

    /** 客户端类型：管理端 */
    public static final String CLIENT_TYPE_ADMIN = "admin";

    /** 客户端类型：App */
    public static final String CLIENT_TYPE_APP = "app";

    /** 客户端类型：PC 用户端 */
    public static final String CLIENT_TYPE_PC = "pc";

    /** 客户端类型：移动端 */
    public static final String CLIENT_TYPE_MOBILE = "mobile";

    /** 短信验证码有效期（秒） */
    public static final long SMS_CODE_EXPIRE = 300;

    /** 短信发送间隔（秒） */
    public static final long SMS_SEND_INTERVAL = 60;

    private CommonConstant() {
    }
}
