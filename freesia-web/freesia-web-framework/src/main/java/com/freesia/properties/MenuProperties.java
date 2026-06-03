package com.freesia.properties;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 菜单相关配置 属性类
 * @date 2026-06-03
 */
@Data
@Component
@ConfigurationProperties(prefix = "freesia.web.menu")
public class MenuProperties {
    @Schema(description = "初始化菜单的path")
    private List<String> path;
}
