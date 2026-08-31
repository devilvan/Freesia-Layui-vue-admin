package com.freesia.fusebean.service.impl;

import com.freesia.fusebean.component.FuseBeanExternalClient;
import com.freesia.fusebean.component.FuseBeanSkillClient;
import com.freesia.fusebean.component.FuseBeanSkillClient.SkillResult;
import com.freesia.fusebean.config.FuseBeanProperties;
import com.freesia.fusebean.dto.FuseBeanConfirmReqDto;
import com.freesia.fusebean.service.FuseBeanService;
import com.freesia.fusebean.util.FuseBeanPixelArtUtil;
import com.freesia.fusebean.util.FuseBeanPixelArtUtil.GridResult;
import com.freesia.fusebean.vo.FuseBeanColorStatVo;
import com.freesia.fusebean.vo.FuseBeanColorVo;
import com.freesia.fusebean.vo.FuseBeanConfirmRespVo;
import com.freesia.fusebean.vo.FuseBeanGenerateRespVo;
import com.freesia.util.UEmpty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 拼豆业务逻辑实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FuseBeanServiceImpl implements FuseBeanService {

    private final FuseBeanExternalClient externalClient;
    private final FuseBeanSkillClient skillClient;
    private final FuseBeanProperties properties;

    private volatile List<MardColor> mardColors;

    @Override
    public FuseBeanGenerateRespVo generateImage(MultipartFile file,
                                                String prompt,
                                                Integer gridSize,
                                                Integer maxColors,
                                                String processingMode,
                                                Boolean removeBackground,
                                                Boolean flipHorizontal,
                                                String aiStylePrompt) {
        int targetGrid = clamp(gridSize, 16, 192, properties.getGridSize());
        int targetColors = clamp(maxColors, 1, 291, properties.getMaxColors());
        boolean removeBg = Boolean.TRUE.equals(removeBackground);
        boolean flip = Boolean.TRUE.equals(flipHorizontal);
        String normalizedMode = normalizeProcessingMode(processingMode);
        String skillStyle = mapProcessingModeToStyle(normalizedMode);
        String skillBackground = removeBg ? "remove" : "keep";
        boolean useAiStyle = properties.getAi().isEnabled() && UEmpty.isNotEmpty(aiStylePrompt);
        String aiPrompt = useAiStyle ? aiStylePrompt.trim() : null;

        BufferedImage source = null;
        byte[] imageBytes = null;
        if (file != null && !file.isEmpty()) {
            try {
                source = ImageIO.read(file.getInputStream());
                imageBytes = file.getBytes();
            } catch (IOException e) {
                throw new IllegalArgumentException("读取上传图片失败，请确认图片格式正确", e);
            }
            if (source == null) {
                throw new IllegalArgumentException("无法识别的图片格式，请上传 JPG/PNG 等常见图片");
            }
        }
        if (useAiStyle && source == null) {
            throw new IllegalArgumentException("AI 风格重绘需要先上传图片");
        }

        BufferedImage pixelSource = source;
        String message = null;
        if (useAiStyle) {
            pixelSource = preprocessSource(source, removeBg, flip);
            message = "由 " + properties.getAi().getModel() + " 按提示词重绘后生成";
        } else if (source != null || UEmpty.isNotEmpty(prompt)) {
            byte[] externalInput = imageBytes == null ? new byte[0] : imageBytes;
            if (source != null && (flip || removeBg)) {
                BufferedImage transformed = preprocessSource(source, removeBg, flip);
                externalInput = toPngBytes(transformed);
            }
            BufferedImage externalSource = externalClient.generate(externalInput, prompt);
            if (externalSource != null) {
                pixelSource = externalSource;
                message = "由外部拼豆生成接口生成";
            }
        }

        if (pixelSource == null) {
            if (source == null) {
                throw new IllegalArgumentException("本地像素化需要上传图片，或请在配置中启用外部生成接口");
            }
            pixelSource = source;
            message = "由本地像素化算法生成";
        }

        if (!useAiStyle) {
            pixelSource = preprocessSource(pixelSource, removeBg, flip);
        }

        if (skillClient.isReady()) {
            try {
                log.info("FuseBean generate: using local image-to-pindou skill, gridSize={}, maxColors={}, mode={}, background={}, aiStyle={}, sourceType={}",
                        targetGrid, targetColors, normalizedMode, skillBackground, useAiStyle, describeSourceType(source, prompt));
                SkillResult skillResult = skillClient.generate(pixelSource, targetGrid, targetColors, skillStyle, skillBackground, aiPrompt);
                log.info("FuseBean generate: local skill completed, grid={}x{}, colors={}",
                        skillResult.gridWidth(), skillResult.gridHeight(), skillResult.palette().size());
                return buildSkillGenerateResp(skillResult, message);
            } catch (Exception e) {
                if (useAiStyle) {
                    throw new IllegalStateException("AI 风格重绘失败：" + e.getMessage(), e);
                }
                log.warn("FuseBean generate: local skill failed, fallback to Java pixelization", e);
            }
        } else {
            log.debug("FuseBean generate: local skill not ready, fallback to Java pixelization");
            if (useAiStyle) {
                throw new IllegalStateException("本地 image-to-pindou skill 不可用，无法执行 AI 风格重绘");
            }
        }

        GridResult result = FuseBeanPixelArtUtil.toGrid(pixelSource, targetGrid, targetColors);
        List<List<Integer>> grid = toGridList(result.getGrid());
        List<FuseBeanColorVo> palette = buildPalette(result.getPalette());
        BufferedImage preview = FuseBeanPixelArtUtil.renderPreview(grid, palette, properties.getPreviewCellSize());

        FuseBeanGenerateRespVo vo = new FuseBeanGenerateRespVo();
        vo.setPreviewBase64(FuseBeanPixelArtUtil.toBase64Png(preview));
        vo.setGridWidth(result.getWidth());
        vo.setGridHeight(result.getHeight());
        vo.setPalette(palette);
        vo.setGrid(grid);
        vo.setMessage(message);
        return vo;
    }

    @Override
    public FuseBeanConfirmRespVo confirmGenerate(FuseBeanConfirmReqDto reqDto) {
        List<List<Integer>> grid = reqDto.getGrid();
        List<FuseBeanColorVo> paletteVo = normalizePalette(reqDto.getPalette());
        if (grid == null || grid.isEmpty() || paletteVo.isEmpty()) {
            throw new IllegalArgumentException("图纸数据缺失，请先生成拼豆像素风图片");
        }
        int height = grid.size();
        int width = grid.get(0).size();
        int cellSize = resolvePatternCellSize(width, height, reqDto.getCellSize());

        BufferedImage patternImage = FuseBeanPixelArtUtil.renderPattern(grid, paletteVo, cellSize);
        BufferedImage cleanImage = FuseBeanPixelArtUtil.renderPreview(grid, paletteVo, cellSize);
        String svg = FuseBeanPixelArtUtil.buildSvg(grid, paletteVo, cellSize, reqDto.getName());
        int[] stats = FuseBeanPixelArtUtil.colorStats(grid, paletteVo.size());

        log.info("FuseBean confirmGenerate: name={}, grid={}x{}, cellSize={}, colors={}",
                reqDto.getName(), width, height, cellSize, paletteVo.size());

        FuseBeanConfirmRespVo vo = new FuseBeanConfirmRespVo();
        vo.setName(reqDto.getName());
        vo.setGridWidth(width);
        vo.setGridHeight(height);
        vo.setCellSize(cellSize);
        vo.setPatternPngBase64(FuseBeanPixelArtUtil.toBase64Png(patternImage));
        vo.setPatternPngCleanBase64(FuseBeanPixelArtUtil.toBase64Png(cleanImage));
        vo.setPatternSvg(svg);
        vo.setColorStats(buildColorStats(stats, paletteVo));
        return vo;
    }

    /**
     * 按图纸尺寸自适应放大豆格像素，保证色码清晰可读，同时避免超大画布。
     * 目标长边约 3600px，豆格像素限制在 [16, 40]；前端传入的格像素作为下限保留。
     */
    private int resolvePatternCellSize(int width, int height, Integer requestedCellSize) {
        int requested = requestedCellSize != null && requestedCellSize > 0 ? requestedCellSize : properties.getCellSize();
        int maxSide = Math.max(width, height);
        int fit = (int) Math.floor(3600.0 / Math.max(1, maxSide));
        int adaptive = Math.max(16, Math.min(40, fit));
        return Math.max(requested, adaptive);
    }

    private FuseBeanGenerateRespVo buildSkillGenerateResp(SkillResult skillResult, String fallbackMessage) {
        List<List<Integer>> grid = skillResult.grid();
        List<FuseBeanColorVo> palette = skillResult.palette();
        BufferedImage preview = FuseBeanPixelArtUtil.renderPreview(grid, palette, properties.getPreviewCellSize());

        FuseBeanGenerateRespVo vo = new FuseBeanGenerateRespVo();
        vo.setPreviewBase64(FuseBeanPixelArtUtil.toBase64Png(preview));
        vo.setGridWidth(skillResult.gridWidth());
        vo.setGridHeight(skillResult.gridHeight());
        vo.setPalette(palette);
        vo.setGrid(grid);
        if (UEmpty.isNotEmpty(fallbackMessage)) {
            vo.setMessage(fallbackMessage + "，并经本地 image-to-pindou skill 标准化");
        } else {
            vo.setMessage(skillResult.message());
        }
        return vo;
    }

    private String describeSourceType(BufferedImage source, String prompt) {
        if (source != null && UEmpty.isNotEmpty(prompt)) {
            return "upload+prompt";
        }
        if (source != null) {
            return "upload";
        }
        return "prompt-only";
    }

    private List<FuseBeanColorVo> buildPalette(int[] palette) {
        List<FuseBeanColorVo> list = new ArrayList<>(palette.length);
        for (int i = 0; i < palette.length; i++) {
            MardColor color = nearestMardColor(palette[i]);
            FuseBeanColorVo vo = new FuseBeanColorVo();
            vo.setIndex(i + 1);
            vo.setCode(color.code());
            vo.setHex(color.hex());
            list.add(vo);
        }
        return list;
    }

    private List<FuseBeanColorVo> normalizePalette(List<FuseBeanColorVo> paletteVo) {
        List<FuseBeanColorVo> list = new ArrayList<>(paletteVo.size());
        for (int i = 0; i < paletteVo.size(); i++) {
            FuseBeanColorVo input = paletteVo.get(i);
            if (input == null) {
                continue;
            }
            FuseBeanColorVo color = new FuseBeanColorVo();
            color.setIndex(input.getIndex() != null ? input.getIndex() : i + 1);
            MardColor nearest = null;
            if (UEmpty.isNotEmpty(input.getHex())) {
                nearest = nearestMardColor(parseHex(input.getHex()));
            } else if (UEmpty.isNotEmpty(input.getCode())) {
                nearest = findMardColorByCode(input.getCode());
            }
            if (nearest != null) {
                color.setCode(UEmpty.isNotEmpty(input.getCode()) ? input.getCode() : nearest.code());
                color.setHex(UEmpty.isNotEmpty(input.getHex()) ? normalizeHex(input.getHex()) : nearest.hex());
            } else {
                color.setCode(input.getCode());
                color.setHex(input.getHex());
            }
            list.add(color);
        }
        return list;
    }

    private List<FuseBeanColorStatVo> buildColorStats(int[] stats, List<FuseBeanColorVo> palette) {
        List<FuseBeanColorStatVo> list = new ArrayList<>();
        for (int i = 0; i < stats.length && i < palette.size(); i++) {
            if (stats[i] <= 0) {
                continue;
            }
            FuseBeanColorStatVo stat = new FuseBeanColorStatVo();
            stat.setIndex(i + 1);
            stat.setCode(palette.get(i).getCode());
            stat.setHex(palette.get(i).getHex());
            stat.setCount(stats[i]);
            list.add(stat);
        }
        return list;
    }

    private List<List<Integer>> toGridList(int[][] grid) {
        List<List<Integer>> list = new ArrayList<>(grid.length);
        for (int[] row : grid) {
            List<Integer> rowList = new ArrayList<>(row.length);
            for (int cell : row) {
                rowList.add(cell);
            }
            list.add(rowList);
        }
        return list;
    }

    private BufferedImage preprocessSource(BufferedImage source, boolean removeBackground, boolean flipHorizontal) {
        BufferedImage image = toArgbImage(source);
        if (removeBackground) {
            image = removeOuterBackground(image);
        }
        if (flipHorizontal) {
            image = flipHorizontal(image);
        }
        return image;
    }

    private BufferedImage toArgbImage(BufferedImage source) {
        if (source.getType() == BufferedImage.TYPE_INT_ARGB) {
            return source;
        }
        BufferedImage image = new BufferedImage(source.getWidth(), source.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        g.setComposite(AlphaComposite.Src);
        g.drawImage(source, 0, 0, null);
        g.dispose();
        return image;
    }

    private BufferedImage flipHorizontal(BufferedImage source) {
        BufferedImage flipped = new BufferedImage(source.getWidth(), source.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = flipped.createGraphics();
        AffineTransform transform = new AffineTransform();
        transform.translate(source.getWidth(), 0);
        transform.scale(-1, 1);
        g.drawImage(source, transform, null);
        g.dispose();
        return flipped;
    }

    private BufferedImage removeOuterBackground(BufferedImage source) {
        int width = source.getWidth();
        int height = source.getHeight();
        int[] pixels = source.getRGB(0, 0, width, height, null, 0, width);

        Map<String, EdgeBucket> buckets = new LinkedHashMap<>();
        int edgeCount = 0;
        for (int x = 0; x < width; x++) {
            edgeCount += addBackgroundSample(pixels, width, x, 0, buckets, true, cornerMask(x, 0, width, height));
            edgeCount += addBackgroundSample(pixels, width, x, height - 1, buckets, true, cornerMask(x, height - 1, width, height));
        }
        for (int y = 1; y < height - 1; y++) {
            edgeCount += addBackgroundSample(pixels, width, 0, y, buckets, true, cornerMask(0, y, width, height));
            edgeCount += addBackgroundSample(pixels, width, width - 1, y, buckets, true, cornerMask(width - 1, y, width, height));
        }

        final int totalEdgeCount = edgeCount;
        EdgeBucket background = buckets.values().stream()
                .filter(bucket -> Integer.bitCount(bucket.cornersMask) >= 3 || (totalEdgeCount > 0 && bucket.edgeCount * 1.0 / totalEdgeCount >= 0.55))
                .max((a, b) -> {
                    int cornerCompare = Integer.compare(Integer.bitCount(a.cornersMask), Integer.bitCount(b.cornersMask));
                    if (cornerCompare != 0) {
                        return cornerCompare;
                    }
                    return Integer.compare(a.edgeCount, b.edgeCount);
                })
                .orElse(null);
        if (background == null) {
            return source;
        }

        int[] bgRgb = background.averageRgb();
        boolean neutralLight = Math.max(bgRgb[0], Math.max(bgRgb[1], bgRgb[2])) - Math.min(bgRgb[0], Math.min(bgRgb[1], bgRgb[2])) < 12
                && luminance(bgRgb) > 225;
        int tolerance = neutralLight ? 96 : 44;
        // 背景与主体颜色相近时降低容差，避免过拟合把主体相近色块也一并清除
        int similarCount = 0;
        for (int i = 0; i < pixels.length; i++) {
            if (((pixels[i] >>> 24) & 0xFF) < 24) {
                continue;
            }
            if (colorDistanceSq(pixels[i], bgRgb) <= tolerance * tolerance) {
                similarCount++;
            }
        }
        double similarRatio = similarCount * 1.0 / Math.max(1, pixels.length);
        if (similarRatio > 0.6) {
            tolerance = Math.min(tolerance, 18);
        } else if (similarRatio > 0.4) {
            tolerance = Math.min(tolerance, 26);
        }
        boolean[] seen = new boolean[width * height];
        ArrayDeque<Integer> queue = new ArrayDeque<>();

        for (int x = 0; x < width; x++) {
            enqueueBackgroundPixel(pixels, width, height, x, 0, seen, queue, bgRgb, tolerance);
            enqueueBackgroundPixel(pixels, width, height, x, height - 1, seen, queue, bgRgb, tolerance);
        }
        for (int y = 1; y < height - 1; y++) {
            enqueueBackgroundPixel(pixels, width, height, 0, y, seen, queue, bgRgb, tolerance);
            enqueueBackgroundPixel(pixels, width, height, width - 1, y, seen, queue, bgRgb, tolerance);
        }

        int cleared = 0;
        while (!queue.isEmpty()) {
            int index = queue.removeFirst();
            int x = index % width;
            int y = index / width;
            pixels[index] = 0x00000000;
            cleared++;
            if (x > 0) enqueueBackgroundPixel(pixels, width, height, x - 1, y, seen, queue, bgRgb, tolerance);
            if (x + 1 < width) enqueueBackgroundPixel(pixels, width, height, x + 1, y, seen, queue, bgRgb, tolerance);
            if (y > 0) enqueueBackgroundPixel(pixels, width, height, x, y - 1, seen, queue, bgRgb, tolerance);
            if (y + 1 < height) enqueueBackgroundPixel(pixels, width, height, x, y + 1, seen, queue, bgRgb, tolerance);
        }

        if (cleared < width * height * 0.01) {
            return source;
        }

        BufferedImage target = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        target.setRGB(0, 0, width, height, pixels, 0, width);
        return target;
    }

    private int addBackgroundSample(int[] pixels,
                                    int width,
                                    int x,
                                    int y,
                                    Map<String, EdgeBucket> buckets,
                                    boolean isEdge,
                                    int cornerMask) {
        int index = y * width + x;
        int argb = pixels[index];
        int alpha = (argb >>> 24) & 0xFF;
        if (alpha < 24) {
            return 0;
        }
        int r = (argb >> 16) & 0xFF;
        int g = (argb >> 8) & 0xFF;
        int b = argb & 0xFF;
        String key = (r >> 5) + "," + (g >> 5) + "," + (b >> 5);
        EdgeBucket bucket = buckets.computeIfAbsent(key, k -> new EdgeBucket());
        bucket.count++;
        bucket.sum[0] += r;
        bucket.sum[1] += g;
        bucket.sum[2] += b;
        if (isEdge) {
            bucket.edgeCount++;
        }
        if (cornerMask >= 0) {
            bucket.cornersMask |= (1 << cornerMask);
        }
        return 1;
    }

    private int cornerMask(int x, int y, int width, int height) {
        if (x == 0 && y == 0) {
            return 0;
        }
        if (x == width - 1 && y == 0) {
            return 1;
        }
        if (x == 0 && y == height - 1) {
            return 2;
        }
        if (x == width - 1 && y == height - 1) {
            return 3;
        }
        return -1;
    }

    private void enqueueBackgroundPixel(int[] pixels,
                                        int width,
                                        int height,
                                        int x,
                                        int y,
                                        boolean[] seen,
                                        ArrayDeque<Integer> queue,
                                        int[] bgRgb,
                                        int tolerance) {
        int index = y * width + x;
        if (seen[index]) {
            return;
        }
        int argb = pixels[index];
        int alpha = (argb >>> 24) & 0xFF;
        if (alpha < 24 || colorDistanceSq(argb, bgRgb) <= tolerance * tolerance) {
            seen[index] = true;
            queue.add(index);
        }
    }

    private int colorDistanceSq(int argb, int[] rgb) {
        int r = (argb >> 16) & 0xFF;
        int g = (argb >> 8) & 0xFF;
        int b = argb & 0xFF;
        int dr = r - rgb[0];
        int dg = g - rgb[1];
        int db = b - rgb[2];
        return dr * dr + dg * dg + db * db;
    }

    private int luminance(int[] rgb) {
        return (int) Math.round(rgb[0] * 0.299 + rgb[1] * 0.587 + rgb[2] * 0.114);
    }

    private byte[] toPngBytes(BufferedImage image) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            if (!ImageIO.write(image, "png", baos)) {
                throw new IllegalStateException("无法编码图片为 PNG");
            }
            return baos.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException("编码 PNG 失败", e);
        }
    }

    private String normalizeProcessingMode(String processingMode) {
        if (UEmpty.isEmpty(processingMode)) {
            return "edge";
        }
        String value = processingMode.trim().toLowerCase();
        return switch (value) {
            case "average", "dominant", "edge" -> value;
            case "faithful" -> "average";
            case "cartoon" -> "dominant";
            case "bead" -> "edge";
            default -> "edge";
        };
    }

    private String mapProcessingModeToStyle(String processingMode) {
        return switch (normalizeProcessingMode(processingMode)) {
            case "average" -> "faithful";
            case "dominant" -> "cartoon";
            default -> "bead";
        };
    }

    private List<MardColor> getMardColors() {
        if (mardColors == null) {
            synchronized (this) {
                if (mardColors == null) {
                    mardColors = loadMardColors();
                }
            }
        }
        return mardColors;
    }

    private List<MardColor> loadMardColors() {
        List<Path> candidates = List.of(
                properties.resolveSkillRoot().resolve("palettes/mard-291.csv"),
                Path.of(System.getProperty("user.home"), ".codex", "skills", "image-to-pindou", "palettes", "mard-291.csv")
        );
        Path file = candidates.stream().filter(Files::isRegularFile).findFirst()
                .orElseThrow(() -> new IllegalStateException("未找到 MARD 291 调色板文件"));
        try {
            List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
            List<MardColor> list = new ArrayList<>();
            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i).trim();
                if (line.isEmpty()) {
                    continue;
                }
                String[] parts = line.split(",");
                if (parts.length < 4) {
                    continue;
                }
                String code = parts[0].trim();
                int r = Integer.parseInt(parts[1].trim());
                int g = Integer.parseInt(parts[2].trim());
                int b = Integer.parseInt(parts[3].trim());
                list.add(new MardColor(code, (r << 16) | (g << 8) | b, String.format("#%02X%02X%02X", r, g, b)));
            }
            if (list.isEmpty()) {
                throw new IllegalStateException("MARD 291 调色板为空");
            }
            return list;
        } catch (IOException e) {
            throw new IllegalStateException("读取 MARD 291 调色板失败", e);
        }
    }

    private MardColor nearestMardColor(int argb) {
        int rgb = argb & 0x00FFFFFF;
        int bestDistance = Integer.MAX_VALUE;
        MardColor best = null;
        for (MardColor color : getMardColors()) {
            int dr = ((rgb >> 16) & 0xFF) - ((color.rgb() >> 16) & 0xFF);
            int dg = ((rgb >> 8) & 0xFF) - ((color.rgb() >> 8) & 0xFF);
            int db = (rgb & 0xFF) - (color.rgb() & 0xFF);
            int distance = dr * dr + dg * dg + db * db;
            if (distance < bestDistance) {
                bestDistance = distance;
                best = color;
            }
        }
        return best;
    }

    private MardColor findMardColorByCode(String code) {
        if (UEmpty.isEmpty(code)) {
            return null;
        }
        return getMardColors().stream()
                .filter(color -> code.equalsIgnoreCase(color.code()))
                .findFirst()
                .orElse(null);
    }

    private String normalizeHex(String hex) {
        if (UEmpty.isEmpty(hex)) {
            return hex;
        }
        String value = hex.trim();
        return value.startsWith("#") ? value.toUpperCase() : ("#" + value).toUpperCase();
    }

    private int parseHex(String hex) {
        if (UEmpty.isEmpty(hex)) {
            throw new IllegalArgumentException("颜色值不能为空");
        }
        String value = hex.trim().startsWith("#") ? hex.trim().substring(1) : hex.trim();
        try {
            return Integer.parseInt(value, 16) | 0xFF000000;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("颜色值非法: " + hex, e);
        }
    }

    private int clamp(Integer value, int min, int max, int fallback) {
        if (value == null) {
            return fallback;
        }
        return Math.min(max, Math.max(min, value));
    }

    private record MardColor(String code, int rgb, String hex) {
    }

    private static final class EdgeBucket {
        private int count;
        private int edgeCount;
        private final long[] sum = new long[3];
        private int cornersMask;

        private int[] averageRgb() {
            return new int[]{
                    (int) Math.round(sum[0] * 1.0 / Math.max(1, count)),
                    (int) Math.round(sum[1] * 1.0 / Math.max(1, count)),
                    (int) Math.round(sum[2] * 1.0 / Math.max(1, count))
            };
        }
    }
}
