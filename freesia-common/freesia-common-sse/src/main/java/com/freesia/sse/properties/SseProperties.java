package com.freesia.sse.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Evad.Wu
 * @Description SSE (Server-Sent Events) 属性类
 * @date 2024-10-21
 */
@Data
@ConfigurationProperties(prefix = "sse")
public class SseProperties {
    /**
     * 启用/禁用
     */
    private Boolean enabled;
    /**
     * 路径
     */
    private String path;

}
