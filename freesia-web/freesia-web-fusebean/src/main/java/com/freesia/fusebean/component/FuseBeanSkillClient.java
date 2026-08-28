package com.freesia.fusebean.component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.freesia.fusebean.config.FuseBeanProperties;
import com.freesia.fusebean.vo.FuseBeanColorVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Local image-to-pindou skill executor.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FuseBeanSkillClient {

    private final ObjectMapper objectMapper;
    private final FuseBeanProperties properties;

    public SkillResult generate(BufferedImage source, int gridSize, int maxColors) {
        if (!properties.getSkill().isEnabled()) {
            throw new IllegalStateException("本地 image-to-pindou skill 未启用");
        }
        Path root = resolveSkillRoot();
        Path workDir = null;
        long startedAt = System.nanoTime();
        try {
            log.info("image-to-pindou skill start: root={}, gridSize={}, maxColors={}, style={}, background={}, autoInstall={}",
                    root, gridSize, maxColors, properties.getSkill().getStyle(), properties.getSkill().getBackground(), properties.getSkill().isAutoInstall());
            ensureDependencies(root);
            workDir = Files.createTempDirectory("fusebean-skill-");
            Path input = workDir.resolve("source.png");
            Path outputBase = workDir.resolve("result");
            writePng(source, input);
            runSkill(root, input, outputBase, gridSize, maxColors);
            SkillResult result = parseResult(outputBase);
            long elapsedMs = (System.nanoTime() - startedAt) / 1_000_000L;
            log.info("image-to-pindou skill success: grid={}x{}, colors={}, elapsedMs={}, outputBase={}",
                    result.gridWidth(), result.gridHeight(), result.palette().size(), elapsedMs, outputBase);
            return result;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("本地 image-to-pindou skill 执行被中断", e);
        } catch (IOException e) {
            throw new IllegalStateException("本地 image-to-pindou skill 执行失败", e);
        } finally {
            if (workDir != null) {
                deleteQuietly(workDir);
            }
        }
    }

    public boolean isReady() {
        if (!properties.getSkill().isEnabled()) {
            return false;
        }
        try {
            Path root = resolveSkillRoot();
            return hasSkillEntrypoints(root) && (properties.getSkill().isAutoInstall() || hasSharpDependency(root));
        } catch (Exception e) {
            log.debug("image-to-pindou skill 不可用: {}", e.getMessage());
            return false;
        }
    }

    private Path resolveSkillRoot() {
        Path root = Path.of(properties.getSkill().getRoot()).toAbsolutePath().normalize();
        if (!Files.isDirectory(root)) {
            throw new IllegalStateException("未找到 image-to-pindou skill 目录: " + root);
        }
        return root;
    }

    private boolean hasSkillEntrypoints(Path root) {
        return Files.isRegularFile(root.resolve("SKILL.md")) && Files.isRegularFile(root.resolve("scripts/generate.mjs"));
    }

    private boolean hasSharpDependency(Path root) {
        return Files.isRegularFile(root.resolve("node_modules/sharp/package.json")) || Files.isDirectory(root.resolve("node_modules/sharp"));
    }

    private void ensureDependencies(Path root) throws IOException, InterruptedException {
        if (hasSharpDependency(root)) {
            return;
        }
        if (!properties.getSkill().isAutoInstall()) {
            throw new IllegalStateException("image-to-pindou skill 缺少依赖 sharp，请先在 skill 目录执行 npm install: " + root);
        }
        log.info("image-to-pindou skill dependency sharp missing, running npm install in {}", root);
        runNpmInstall(root);
        if (!hasSharpDependency(root)) {
            throw new IllegalStateException("npm install 完成后 sharp 依赖仍不可用，请在 skill 目录手动执行 npm install: " + root);
        }
        log.info("image-to-pindou skill dependency sharp ready in {}", root);
    }

    private void runNpmInstall(Path root) throws IOException, InterruptedException {
        List<String> command = new ArrayList<>();
        command.add(properties.getSkill().getNpmCommand());
        command.add("install");

        log.info("image-to-pindou npm install command: {}", String.join(" ", command));
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.directory(root.toFile());
        pb.redirectErrorStream(true);
        Process process = pb.start();

        String output;
        try (InputStream in = process.getInputStream()) {
            output = new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new IllegalStateException("image-to-pindou skill 依赖安装失败，exitCode=" + exitCode + ", output=" + output);
        }
        if (log.isDebugEnabled()) {
            log.debug("image-to-pindou npm install output: {}", output.trim());
        }
    }

    private void runSkill(Path root, Path inputFile, Path outputBase, int gridSize, int maxColors) throws IOException, InterruptedException {
        List<String> command = new ArrayList<>();
        command.add(properties.getSkill().getNodeCommand());
        command.add(root.resolve("scripts/generate.mjs").toString());
        command.add(inputFile.toString());
        command.add("--style");
        command.add(properties.getSkill().getStyle());
        command.add("--size");
        command.add(String.valueOf(gridSize));
        command.add("--max-colors");
        command.add(String.valueOf(maxColors));
        command.add("--background");
        command.add(properties.getSkill().getBackground());
        command.add("--cell-px");
        command.add(String.valueOf(properties.getSkill().getCellPx()));
        command.add("--out");
        command.add(outputBase.toString());

        log.info("image-to-pindou skill command: {}", String.join(" ", command));
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.directory(root.toFile());
        pb.redirectErrorStream(true);
        Process process = pb.start();

        String output;
        try (InputStream in = process.getInputStream()) {
            output = new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            if (shouldAutoInstallAndRetry(output) && ensureDependenciesAndRetry(root, inputFile, outputBase, gridSize, maxColors)) {
                return;
            }
            throw new IllegalStateException("image-to-pindou skill 执行失败，exitCode=" + exitCode + ", output=" + output);
        }
        if (log.isDebugEnabled()) {
            log.debug("image-to-pindou skill output: {}", output.trim());
        }
    }

    private boolean shouldAutoInstallAndRetry(String output) {
        return properties.getSkill().isAutoInstall() && output != null && output.contains("ERR_MODULE_NOT_FOUND");
    }

    private boolean ensureDependenciesAndRetry(Path root, Path inputFile, Path outputBase, int gridSize, int maxColors) throws IOException, InterruptedException {
        ensureDependencies(root);
        log.info("image-to-pindou skill retry after dependency installation");
        runSkillOnce(root, inputFile, outputBase, gridSize, maxColors);
        return true;
    }

    private void runSkillOnce(Path root, Path inputFile, Path outputBase, int gridSize, int maxColors) throws IOException, InterruptedException {
        List<String> command = new ArrayList<>();
        command.add(properties.getSkill().getNodeCommand());
        command.add(root.resolve("scripts/generate.mjs").toString());
        command.add(inputFile.toString());
        command.add("--style");
        command.add(properties.getSkill().getStyle());
        command.add("--size");
        command.add(String.valueOf(gridSize));
        command.add("--max-colors");
        command.add(String.valueOf(maxColors));
        command.add("--background");
        command.add(properties.getSkill().getBackground());
        command.add("--cell-px");
        command.add(String.valueOf(properties.getSkill().getCellPx()));
        command.add("--out");
        command.add(outputBase.toString());

        log.info("image-to-pindou skill retry command: {}", String.join(" ", command));
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.directory(root.toFile());
        pb.redirectErrorStream(true);
        Process process = pb.start();

        String output;
        try (InputStream in = process.getInputStream()) {
            output = new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new IllegalStateException("image-to-pindou skill 执行失败，exitCode=" + exitCode + ", output=" + output);
        }
        if (log.isDebugEnabled()) {
            log.debug("image-to-pindou skill output: {}", output.trim());
        }
    }

    private SkillResult parseResult(Path outputBase) throws IOException {
        Path projectFile = outputBase.resolve(outputBase.getFileName().toString() + ".pindou.json");
        Path previewFile = outputBase.resolve("pixel-preview.png");
        Path patternPngFile = outputBase.resolve("pattern.png");
        Path patternSvgFile = outputBase.resolve("pattern.svg");
        Path bomFile = outputBase.resolve("bom.csv");

        if (!Files.isRegularFile(projectFile)) {
            throw new IllegalStateException("未生成 skill 项目文件: " + projectFile);
        }
        JsonNode root = objectMapper.readTree(Files.readString(projectFile));
        JsonNode snapshot = root.path("projects").path(0).path("pattern").path("snapshot");
        int width = snapshot.path("w").asInt();
        int height = snapshot.path("h").asInt();
        JsonNode cellsNode = snapshot.path("c");
        List<String> codes = readCodes(cellsNode.path("p"));
        Map<String, BomEntry> bom = readBom(bomFile);
        List<List<Integer>> grid = decodeGrid(cellsNode.path("d").asText(""), cellsNode.path("s").asInt(1), width, height);
        List<FuseBeanColorVo> palette = buildPalette(codes, bom);
        String previewBase64 = toBase64Png(previewFile);
        String patternPngBase64 = toBase64Png(patternPngFile);
        String patternSvg = Files.readString(patternSvgFile);
        String message = "由本地 image-to-pindou skill 生成";
        return new SkillResult(previewBase64, patternPngBase64, patternSvg, palette, grid, message, width, height);
    }

    private List<String> readCodes(JsonNode node) {
        List<String> codes = new ArrayList<>();
        if (node != null && node.isArray()) {
            for (JsonNode item : node) {
                codes.add(item.asText());
            }
        }
        return codes;
    }

    private Map<String, BomEntry> readBom(Path bomFile) throws IOException {
        Map<String, BomEntry> map = new LinkedHashMap<>();
        if (!Files.isRegularFile(bomFile)) {
            return map;
        }
        List<String> lines = Files.readAllLines(bomFile, StandardCharsets.UTF_8);
        if (lines.isEmpty()) {
            return map;
        }
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (line.isEmpty()) {
                continue;
            }
            String[] parts = line.split(",");
            if (parts.length < 3) {
                continue;
            }
            BomEntry entry = new BomEntry(parts[0].trim(), parts[1].trim(), parseInt(parts[2].trim()));
            map.put(entry.code(), entry);
        }
        return map;
    }

    private List<FuseBeanColorVo> buildPalette(List<String> codes, Map<String, BomEntry> bom) {
        List<FuseBeanColorVo> palette = new ArrayList<>(codes.size());
        for (int i = 0; i < codes.size(); i++) {
            String code = codes.get(i);
            BomEntry entry = bom.get(code);
            FuseBeanColorVo vo = new FuseBeanColorVo();
            vo.setIndex(i + 1);
            vo.setCode(code);
            vo.setHex(entry != null ? entry.hex() : null);
            palette.add(vo);
        }
        return palette;
    }

    private List<List<Integer>> decodeGrid(String base64, int bytesPerCell, int width, int height) {
        byte[] bytes = Base64.getDecoder().decode(base64);
        List<List<Integer>> grid = new ArrayList<>(height);
        int index = 0;
        for (int y = 0; y < height; y++) {
            List<Integer> row = new ArrayList<>(width);
            for (int x = 0; x < width; x++) {
                int value;
                if (bytesPerCell <= 1) {
                    value = Byte.toUnsignedInt(bytes[index++]);
                } else {
                    int low = Byte.toUnsignedInt(bytes[index++]);
                    int high = Byte.toUnsignedInt(bytes[index++]);
                    value = low | (high << 8);
                }
                row.add(value <= 0 ? null : value - 1);
            }
            grid.add(row);
        }
        return grid;
    }

    private String toBase64Png(Path file) throws IOException {
        BufferedImage image = ImageIO.read(file.toFile());
        if (image == null) {
            throw new IllegalStateException("无法读取 skill 输出图片: " + file);
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        return "data:image/png;base64," + Base64.getEncoder().encodeToString(baos.toByteArray());
    }

    private int parseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private void writePng(BufferedImage source, Path input) throws IOException {
        if (!ImageIO.write(source, "png", input.toFile())) {
            throw new IllegalStateException("无法写入 skill 输入图片: " + input);
        }
    }

    private void deleteQuietly(Path root) {
        try {
            if (!Files.exists(root)) {
                return;
            }
            Files.walkFileTree(root, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.deleteIfExists(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    Files.deleteIfExists(dir);
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            log.debug("清理 skill 临时目录失败: {}", root, e);
        }
    }

    private record BomEntry(String code, String hex, int count) {
    }

    public record SkillResult(
            String previewBase64,
            String patternPngBase64,
            String patternSvg,
            List<FuseBeanColorVo> palette,
            List<List<Integer>> grid,
            String message,
            int gridWidth,
            int gridHeight
    ) {
    }
}
