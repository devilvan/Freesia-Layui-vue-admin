package com.freesia.exception;

import com.freesia.util.UEmpty;
import com.freesia.util.UMessage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;

/**
 * @author Evad.Wu
 * @Description 自定义异常处理类
 * @date 2022-07-21
 */
@SuppressWarnings(value = "unused")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class BaseException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 6461682216952052835L;

    /**
     * 所属模块
     */
    private String module;

    /**
     * 错误码
     */
    private String code;

    /**
     * 错误码对应的参数
     */
    private Object[] args;

    /**
     * 错误消息
     */
    private String defaultMessage;

    public BaseException(String defaultMessage) {
        this(null, null, null, defaultMessage);
    }

    public BaseException(String code, Object[] args) {
        this(null, code, args, null);
    }

    public BaseException(String module, String defaultMessage) {
        this(module, null, null, defaultMessage);
    }

    public BaseException(String module, String code, Object[] args) {
        this(module, code, args, null);
    }

    @Override
    public String getMessage() {
        String message = null;
        if (!UEmpty.isEmpty(code)) {
            message = UMessage.message(code, args);
        }
        if (message == null) {
            message = defaultMessage;
        }
        return message;
    }
}
