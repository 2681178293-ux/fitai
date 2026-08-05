package net.wanho.fitai.infra.exception;

/**
 * 通用错误码：标准码（200 / 500）+ 1001 起的跨模块码。
 * 业务错误码请放在各模块 XxxCode（system=2001 起，其余模块=3001 / 4001 / …）。
 */
public enum CommonCode implements ExceptionResult {
    // 标准
    SUCCESS(200, "操作成功"),
    SYSTEM_ERROR(500, "系统错误"),

    // 通用 1001-1099
    PARAM_ERROR(1001, "参数错误"),
    DATA_NOT_FOUND(1002, "数据不存在"),
    DATA_ALREADY_EXISTS(1003, "数据已存在"),
    OPERATION_FAILED(1004, "操作失败"),

    // 认证 1101-1199
    NOT_LOGIN(1101, "请先登录"),
    LOGIN_EXPIRED(1102, "登录已过期"),
    TOKEN_INVALID(1103, "Token无效"),
    NO_PERMISSION(1104, "没有权限"),
    ACCOUNT_DISABLED(1105, "账号已禁用"),
    PASSWORD_ERROR(1106, "密码错误"),
    PHONE_EXISTS(1107, "手机号已注册"),
    PHONE_NOT_EXISTS(1108, "手机号未注册"),
    SMS_CODE_ERROR(1109, "验证码错误"),
    SMS_CODE_EXPIRED(1110, "验证码已过期"),
    SMS_SEND_LIMIT(1111, "验证码发送过于频繁"),
    CAPTCHA_REQUIRED(1112, "请先完成安全验证"),
    CAPTCHA_INVALID(1113, "安全验证未通过或已失效，请重试"),

    // 文件 1201-1299
    FILE_UPLOAD_FAILED(1201, "文件上传失败"),
    FILE_TYPE_NOT_ALLOWED(1202, "不支持的文件类型"),
    FILE_SIZE_EXCEEDED(1203, "文件大小超过限制");

    private final int code;
    private final String msg;

    CommonCode(int code, String msg) {
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
