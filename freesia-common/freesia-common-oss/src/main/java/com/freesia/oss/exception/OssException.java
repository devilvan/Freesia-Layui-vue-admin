package com.freesia.oss.exception;

import com.freesia.exception.ServiceException;
import com.freesia.oss.constant.OssModule;

import java.io.Serial;

/**
 * @author Evad.Wu
 * @Description OSS对象存储 异常类
 * @date 2024-02-27
 */
public class OssException extends ServiceException {
    @Serial
    private static final long serialVersionUID = 8261360291365883992L;

    public OssException(String defaultMessage) {
        super(defaultMessage);
    }

    public OssException(String code, Object... args) {
        super(OssModule.OSS_MANAGEMENT, code, args, null);
    }
}
