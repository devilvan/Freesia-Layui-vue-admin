package com.freesia.vo;

import cn.hutool.http.HttpStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;

/**
 * @author Evad.Wu
 * @Description 统一结果返回类
 * @date 2022-07-16
 */
@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Schema(description = "统一结果返回类")
@SuppressWarnings(value = "all")
public class R<T> {
    @Schema(description = "返回编码")
    private Integer code;
    @Schema(description = "返回消息")
    private String msg;
    @Schema(description = "是否成功")
    private boolean success = false;
    @Schema(description = "返回数据")
    private T data;
    @Schema(description = "操作行数")
    private Integer handleCount = 0;

    public static <T> R<T> ok() {
        return ok(HttpStatus.HTTP_OK, "操作成功");
    }

    public static <T> R<T> ok(T data) {
        return ok(HttpStatus.HTTP_OK, "操作成功", data);
    }

    public static <T> R<T> ok(T data, int handleCount) {
        return ok(HttpStatus.HTTP_OK, "操作成功", data, handleCount);
    }

    public static <T> R<T> ok(Integer code, String msg) {
        return ok(code, msg, null);
    }

    public static <T> R<T> ok(Integer code, String msg, T data) {
        return ok(code, msg, data, 0);
    }

    public static <T> R<T> ok(Integer code, String msg, T data, int handleCount) {
        R<T> r = new R<T>();
        r.setSuccess(true);
        r.setCode(code);
        r.setMsg(msg);
        r.setData(data);
        if (handleCount > 0) {
            r.setHandleCount(handleCount);
        } else if (data != null && data instanceof Collection) {
            r.setHandleCount(((Collection<?>) data).size());
        }
        return r;
    }

    public static <T> R<T> failed() {
        return failed(HttpStatus.HTTP_INTERNAL_ERROR, "操作失败");
    }

    public static <T> R<T> failed(T data) {
        return failed(HttpStatus.HTTP_INTERNAL_ERROR, "操作失败", data);
    }

    public static <T> R<T> failed(T data, int handleCount) {
        return failed(HttpStatus.HTTP_INTERNAL_ERROR, "操作失败", data, handleCount);
    }

    public static <T> R<T> failed(Integer code, String msg) {
        return failed(code, msg, null);
    }

    public static <T> R<T> failed(Integer code, String msg, T data) {
        return failed(code, msg, data, 0);
    }

    public static <T> R<T> failed(Integer code, String msg, T data, int handleCount) {
        R<T> r = new R<T>();
        r.setSuccess(false);
        r.setCode(code);
        r.setMsg(msg);
        r.setData(data);
        if (handleCount > 0) {
            r.setHandleCount(handleCount);
        } else if (data != null && data instanceof Collection) {
            r.setHandleCount(((Collection<?>) data).size());
        }
        return r;
    }
}
