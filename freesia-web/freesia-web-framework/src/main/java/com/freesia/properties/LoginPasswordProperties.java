package com.freesia.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * @author Evad.Wu
 * @Description 密码登录时的配置信息 属性类
 * @date 2023-08-16
 */
@Data
@ConfigurationProperties(prefix = "login.password")
public class LoginPasswordProperties {
    /**
     * 密码最大错误次数
     */
    private Integer maxRetryCount = 3;
    /**
     * 密码锁定时间（默认10分钟）
     */
    private Duration lockTime = Duration.ofMinutes(10);
    /**
     * 初始化密码
     */
    private String initPassword = "Yjl1048596";
}
