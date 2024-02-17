package com.freesia.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;

/**
 * @author Evad.Wu
 * @Description 业务逻辑 异常类
 * @date 2023-08-14
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ServiceException extends BaseException {
    @Serial
    private static final long serialVersionUID = -7135264851224603091L;

    public ServiceException(String defaultMessage) {
        super(defaultMessage);
    }

    public ServiceException(String module, String code, Object... args) {
        super(module, code, args, null);
    }
}
