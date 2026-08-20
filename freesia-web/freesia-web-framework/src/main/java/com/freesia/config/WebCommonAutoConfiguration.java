package com.freesia.config;

import com.freesia.properties.GiteeProperties;
import com.freesia.properties.LoginPasswordProperties;
import com.freesia.properties.MenuProperties;
import com.freesia.properties.OAuthProperties;
import com.freesia.properties.WebCommonProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * @author Evad.Wu
 * @Description Web通用属性 自动配置类
 * @date 2026-08-19
 */
@AutoConfiguration
@EnableConfigurationProperties({
        GiteeProperties.class,
        LoginPasswordProperties.class,
        MenuProperties.class,
        OAuthProperties.class,
        WebCommonProperties.class
})
public class WebCommonAutoConfiguration {
}
