package net.wanho.fitai.infra.result;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 统一 API 响应壳。
 * <p>
 * 成功时 {@code code} 为 200；失败时默认 {@code code} 为 500，也可传入业务错误码。
 *
 * @param <T> 业务数据类型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "统一响应结果")
public class AjaxResult<T> implements Serializable {

    @Schema(description = "是否成功")
    private boolean success;

    @Schema(description = "业务状态码", example = "200")
    private int code;

    @Schema(description = "提示信息")
    private String msg;

    @Schema(description = "业务数据")
    private T data;

    /** 成功（无数据） */
    public static <T> AjaxResult<T> success() {
        return new AjaxResult<>(true, 200, "操作成功", null);
    }

    /** 成功（带数据） */
    public static <T> AjaxResult<T> success(T data) {
        return new AjaxResult<>(true, 200, "操作成功", data);
    }

    /** 失败（默认码 500） */
    public static <T> AjaxResult<T> error() {
        return new AjaxResult<>(false, 500, "操作失败", null);
    }

    /** 失败（默认码 500，自定义消息） */
    public static <T> AjaxResult<T> error(String msg) {
        return new AjaxResult<>(false, 500, msg, null);
    }

    /** 失败（默认码 500，自定义消息与数据） */
    public static <T> AjaxResult<T> error(String msg, T data) {
        return new AjaxResult<>(false, 500, msg, data);
    }

    /** 失败（自定义业务码与消息） */
    public static <T> AjaxResult<T> error(int code, String msg) {
        return new AjaxResult<>(false, code, msg, null);
    }

    /** 失败（自定义业务码、消息与数据） */
    public static <T> AjaxResult<T> error(int code, String msg, T data) {
        return new AjaxResult<>(false, code, msg, data);
    }

    /** 失败（自定义业务码与数据，消息固定为「操作失败」） */
    public static <T> AjaxResult<T> error(int code, T data) {
        return new AjaxResult<>(false, code, "操作失败", data);
    }
}
