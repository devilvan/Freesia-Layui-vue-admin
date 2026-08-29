package com.freesia.fusebean.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
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

    /**
     * 解析 skill 安装目录。user.dir 随启动方式变化（IDE 默认指向模块目录），
     * 仅凭配置的 root 无法稳定定位仓库内置 skill，因此依次尝试：
     * 1. 配置的 root；2. 从 user.dir 向上查找仓库内置目录；3. Codex 默认安装目录。
     */
    public Path resolveSkillRoot() {
        Path configured = Path.of(skill.getRoot()).toAbsolutePath().normalize();
        if (Files.isDirectory(configured)) {
            return configured;
        }
        Path repo = findUp(System.getProperty("user.dir"), "freesia-web/freesia-web-fusebean/skills/image-to-pindou");
        if (repo != null) {
            return repo;
        }
        Path codex = Path.of(System.getProperty("user.home"), ".codex", "skills", "image-to-pindou").toAbsolutePath().normalize();
        return Files.isDirectory(codex) ? codex : configured;
    }

    private static Path findUp(String start, String relPath) {
        Path dir = Path.of(start).toAbsolutePath().normalize();
        Path rel = Path.of(relPath);
        for (int i = 0; i < 6 && dir != null; i++) {
            Path candidate = dir.resolve(rel).normalize();
            if (Files.isDirectory(candidate)) {
                return candidate;
            }
            dir = dir.getParent();
        }
        return null;
    }
}
