package net.wanho.fitai.modules.ai.code;

import net.wanho.fitai.infra.exception.ExceptionResult;

/**
 * AI 模块错误码 8001-8999
 */
public enum AiCode implements ExceptionResult {
    AI_FEATURE_DISABLED(8001, "AI 功能已关闭，请稍后再试"),
    /** 月度 Token 额度用尽（全场景汇总） */
    AI_QUOTA_EXCEEDED(8002, "本月 AI Token 额度已用尽");

    private final int code;
    private final String msg;

    AiCode(int code, String msg) {
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
