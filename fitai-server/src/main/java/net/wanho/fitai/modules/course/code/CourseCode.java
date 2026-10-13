package net.wanho.fitai.modules.course.code;

import net.wanho.fitai.infra.exception.ExceptionResult;

/**
 * 课程模块错误码 5001-5999
 */
public enum CourseCode implements ExceptionResult {
    COURSE_NOT_FOUND(5001, "课程不存在"),
    VIDEO_NOT_FOUND(5002, "视频不存在"),
    //存在子类不能删除
    COURSE_HAS_CHILDRREN(5003, "课程存在子类不能删除");

    private final int code;
    private final String msg;

    CourseCode(int code, String msg) {
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
