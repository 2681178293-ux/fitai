package net.wanho.fitai.modules.system.log;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 操作日志注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OperLog {

    /** 模块标题 */
    String title() default "";
    /** 0其它 1新增 2修改 3删除 */
    int businessType() default 0;
    /** 是否保存请求参数 */
    boolean saveRequestData() default true;
}
