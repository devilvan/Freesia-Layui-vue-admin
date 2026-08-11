package com.freesia.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Evad.Wu
 * @Description XSS过滤配置属性
 * @date 2026-08-11
 */
@Data
@ConfigurationProperties(prefix = "freesia.xss")
public class XssProperties {
    /**
     * 是否启用XSS过滤
     */
    private boolean enabled = true;

    /**
     * 排除过滤的URL路径（支持Ant风格通配符）
     */
    private List<String> excludeUrls = new ArrayList<>();

    /**
     * 严格模式：移除所有HTML标签（true）或保留安全HTML标签（false）
     */
    private boolean strict = false;

    /**
     * 是否过滤请求体（JSON body）
     */
    private boolean filterBody = true;

    /**
     * 是否过滤请求头
     */
    private boolean filterHeaders = false;

    /**
     * 排除过滤的请求头名称
     */
    private List<String> excludeHeaders = new ArrayList<>();
}
