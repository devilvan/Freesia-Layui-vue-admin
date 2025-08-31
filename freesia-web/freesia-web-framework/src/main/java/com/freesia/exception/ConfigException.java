package com.freesia.exception;

import com.freesia.constant.ConfigModule;

import java.io.Serial;

/**
 * @author Evad.Wu
 * @Description 系统配置 异常类
 * @date 2025-08-31
 */
public class ConfigException extends ServiceException {
    @Serial
    private static final long serialVersionUID = -2626363894568717960L;

    public ConfigException(String code, Object[] args) {
        super(ConfigModule.CONFIG_MANAGEMENT, code, args);
    }
}
