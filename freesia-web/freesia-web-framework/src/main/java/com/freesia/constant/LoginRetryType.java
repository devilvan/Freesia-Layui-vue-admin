package com.freesia.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Evad.Wu
 * @Description 登录重试配置项 枚举类
 * @date 2023-08-16
 */
@Getter
@AllArgsConstructor
public enum LoginRetryType {
    /**
     * 登录
     */
    PASSWORD("user.password.retry.limit.exceed", "user.password.retry.limit.count");

    /**
     * 登录重试超出限制提示
     */
    private final String retryLimitExceed;

    /**
     * 登录重试限制计数提示
     */
    private final String retryLimitCount;
}
