package com.freesia.fusebean.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

/**
 * 拼豆模块配置统一入口。
 */
@Data
@Component
@ConfigurationProperties(prefix = "freesia.fusebean")
public class FuseBeanProperties {

    private boolean externalEnabled = false;

    private String generateUrl = "";

    private String apiKey = "";

    private int requestTimeoutSeconds = 120;

    private int gridSize = 50;

    private int maxColors = 18;

    private int cellSize = 14;

    private int previewCellSize = 10;

    private final Skill skill = new Skill();

    @Data
    public static class Skill {

        private boolean enabled = true;

        private String root = Path.of(System.getProperty("user.dir"), "freesia-web", "freesia-web-fusebean", "skills", "image-to-pindou").toString();

        private String nodeCommand = "node";

        private String npmCommand = "npm";

        private String style = "bead";

        private String background = "keep";

        private int cellPx = 24;

        private boolean autoInstall = true;
    }
}
