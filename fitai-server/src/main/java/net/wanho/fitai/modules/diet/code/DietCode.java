package net.wanho.fitai.modules.diet.code;

import net.wanho.fitai.infra.exception.ExceptionResult;

/**
 * 饮食模块错误码 7001-7999
 */
public enum DietCode implements ExceptionResult {
    DIET_FOOD_CATEGORY_IN_USE(7001, "该分类下仍有食物，无法删除"),
    DIET_FOOD_CATEGORY_NAME_DUP(7002, "分类名称已存在"),
    DIET_FOOD_LIBRARY_EMPTY(7003, "食物库暂无数据，无法生成 AI 饮食计划"),
    DIET_AI_PLAN_NO_VALID_ITEMS(7004, "AI 未选出食物库中的有效食物，请稍后重试或扩充食物库");

    private final int code;
    private final String msg;

    DietCode(int code, String msg) {
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
