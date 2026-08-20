package com.freesia.satoken.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description Sa-Token 属性类
 * @date 2023-08-25
 */
@Data
@ConfigurationProperties(prefix = "security")
public class SecurityProperties {
    /**
     * 白名单
     */
    private List<String> excludes;

}
