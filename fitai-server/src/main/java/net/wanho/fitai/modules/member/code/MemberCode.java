package net.wanho.fitai.modules.member.code;

import net.wanho.fitai.infra.exception.ExceptionResult;

/**
 * 会员/用户模块错误码 3001-3999
 */
public enum MemberCode implements ExceptionResult {
    USER_NOT_FOUND(3001, "用户不存在"),
    USER_DISABLED(3002, "用户已禁用"),
    BODY_METRIC_KPI_CALC_ERROR(3003, "体测KPI计算异常：除数或被除数为0");

    private final int code;
    private final String msg;

    MemberCode(int code, String msg) {
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
