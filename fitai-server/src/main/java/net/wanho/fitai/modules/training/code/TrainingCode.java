package net.wanho.fitai.modules.training.code;

import net.wanho.fitai.infra.exception.ExceptionResult;

/**
 * 训练模块错误码 4001-4999
 */
public enum TrainingCode implements ExceptionResult {
    PLAN_NOT_FOUND(4001, "训练计划不存在"),
    EXERCISE_NOT_FOUND(4002, "动作不存在");

    private final int code;
    private final String msg;

    TrainingCode(int code, String msg) {
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
