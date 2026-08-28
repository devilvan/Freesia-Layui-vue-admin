package com.freesia.fusebean.util;

import com.freesia.fusebean.vo.FuseBeanColorVo;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Evad.Wu
 * @Description 拼豆像素化算法工具：将原图转换为指定格数的色块网格，
 * 并提供像素预览图、图纸网格图、SVG 矢量图、色号统计等渲染能力。
 * @date 2026-08-26
 */
public class FuseBeanPixelArtUtil {

    private static final String DATA_URL_PREFIX = "data:image/png;base64,";

    private FuseBeanPixelArtUtil() {
    }

    /**
     * 网格化结果
     */
    public static class GridResult {
        /** grid[y][x] 存储色板索引 */
        private final int[][] grid;
        /** 色板 ARGB 颜色数组 */
        private final int[] palette;
        private final int width;
        private final int height;

        public GridResult(int[][] grid, int[] palette, int width, int height) {
            this.grid = grid;
            this.palette = palette;
            this.width = width;
            this.height = height;
        }

        public int[][] getGrid() {
            return grid;
        }

        public int[] getPalette() {
            return palette;
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }
    }

    /**
     * 将源图片转换为拼豆网格
     *
     * @param source    源图片
     * @param gridSize  网格最大边长（格子数）
     * @param maxColors 最大颜色数
     * @return 网格结果
     */
    public static GridResult toGrid(BufferedImage source, int gridSize, int maxColors) {
        BufferedImage rgb = flattenAlpha(source);
        int sw = rgb.getWidth();
        int sh = rgb.getHeight();
        double scale = Math.min(1.0, (double) gridSize / Math.max(sw, sh));
        int w = Math.max(1, (int) Math.round(sw * scale));
        int h = Math.max(1, (int) Math.round(sh * scale));
        BufferedImage small = resize(rgb, w, h);

        int[] pixels = small.getRGB(0, 0, w, h, null, 0, w);
        int[] palette = buildPalette(pixels, maxColors);
        int[] indices = new int[pixels.length];
        for (int i = 0; i < pixels.length; i++) {
            indices[i] = nearestColor(pixels[i], palette);
        }

        int[][] grid = new int[h][w];
        for (int y = 0; y < h; y++) {
            System.arraycopy(indices, y * w, grid[y], 0, w);
        }
        return new GridResult(grid, palette, w, h);
    }

    /**
     * 渲染拼豆像素风预览图（无网格线，放大后呈像素马赛克效果）
     *
     * @param result   网格结果
     * @param cellSize 每格渲染像素大小
     * @return 预览图
     */
    public static BufferedImage renderPreview(GridResult result, int cellSize) {
        return render(result, cellSize, false);
    }

    /**
     * 渲染拼豆图纸网格图（带网格线）
     *
     * @param result   网格结果
     * @param cellSize 每格渲染像素大小
     * @return 图纸网格图
     */
    public static BufferedImage renderPattern(GridResult result, int cellSize) {
        return renderPatternInternal(result, cellSize, true);
    }

    /**
     * 基于前端回传的网格与色板渲染图纸网格图
     *
     * @param grid     网格数据（行优先，每格为色板索引）
     * @param palette  色板 ARGB 颜色数组
     * @param cellSize 每格渲染像素大小
     * @return 图纸网格图
     */
    public static BufferedImage renderPattern(List<List<Integer>> grid, List<FuseBeanColorVo> palette, int cellSize) {
        int h = grid.size();
        int w = h > 0 ? grid.get(0).size() : 0;
        PatternLayout layout = createLayout(w, h, palette.size(), cellSize);
        BufferedImage image = new BufferedImage(layout.canvasWidth, layout.canvasHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, image.getWidth(), image.getHeight());
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g.setColor(new Color(0x222222));
        g.setFont(g.getFont().deriveFont(Font.BOLD, 12f));
        g.drawString("拼豆图纸 · " + palette.size() + " 色 · " + w + "×" + h, layout.leftMargin, 16);

        Font axisFont = g.getFont().deriveFont(Font.PLAIN, (float) layout.axisFontSize);
        g.setFont(axisFont);
        FontMetrics axisMetrics = g.getFontMetrics();
        for (int x = 0; x < w; x++) {
            String label = String.valueOf(x + 1);
            int centerX = layout.gridX + x * cellSize + cellSize / 2;
            int textX = centerX - axisMetrics.stringWidth(label) / 2;
            int textY = layout.gridY - 6;
            g.setColor(new Color(0x666666));
            g.drawString(label, textX, textY);
        }
        for (int y = 0; y < h; y++) {
            String label = String.valueOf(y + 1);
            int centerY = layout.gridY + y * cellSize + cellSize / 2;
            int textX = layout.gridX - 6 - axisMetrics.stringWidth(label);
            int textY = centerY + axisMetrics.getAscent() / 2 - 1;
            g.setColor(new Color(0x666666));
            g.drawString(label, textX, textY);
        }

        Font cellFont = g.getFont().deriveFont(Font.BOLD, (float) layout.cellFontSize);
        g.setFont(cellFont);
        FontMetrics cellMetrics = g.getFontMetrics();
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                Integer colorIndex = grid.get(y).get(x);
                if (colorIndex == null || colorIndex < 0 || colorIndex >= palette.size()) {
                    continue;
                }
                FuseBeanColorVo color = palette.get(colorIndex);
                String fill = safeHex(color.getHex());
                Color fillColor = new Color(hexToArgb(fill));
                int drawX = layout.gridX + x * cellSize;
                int drawY = layout.gridY + y * cellSize;
                g.setColor(fillColor);
                g.fillRect(drawX, drawY, cellSize, cellSize);
                g.setColor(contrastColor(fillColor));
                String text = paletteLabel(color, colorIndex + 1);
                int textX = drawX + (cellSize - cellMetrics.stringWidth(text)) / 2;
                int textY = drawY + (cellSize - cellMetrics.getHeight()) / 2 + cellMetrics.getAscent();
                g.drawString(text, textX, textY);
            }
        }

        g.setColor(new Color(0xD9D9D9));
        for (int x = 0; x <= w; x++) {
            g.drawLine(layout.gridX + x * cellSize, layout.gridY, layout.gridX + x * cellSize, layout.gridY + h * cellSize);
        }
        for (int y = 0; y <= h; y++) {
            g.drawLine(layout.gridX, layout.gridY + y * cellSize, layout.gridX + w * cellSize, layout.gridY + y * cellSize);
        }

        g.setFont(g.getFont().deriveFont(Font.PLAIN, (float) layout.legendFontSize));
        for (int i = 0; i < palette.size(); i++) {
            int column = i % layout.legendColumns;
            int row = i / layout.legendColumns;
            int itemX = layout.leftMargin + column * layout.legendItemWidth;
            int itemY = layout.legendTop + row * layout.legendItemHeight;
            FuseBeanColorVo color = palette.get(i);
            Color fillColor = new Color(hexToArgb(safeHex(color.getHex())));
            g.setColor(fillColor);
            g.fillRect(itemX, itemY, 12, 12);
            g.setColor(new Color(0x999999));
            g.drawRect(itemX, itemY, 12, 12);
            g.setColor(new Color(0x333333));
            String text = legendLabel(color, i + 1);
            g.drawString(text, itemX + 18, itemY + 10);
        }

        g.dispose();
        return image;
    }

    /**
     * 生成图纸 SVG 矢量图
     *
     * @param grid     网格数据（行优先）
     * @param palette  色板 ARGB 颜色数组
     * @param cellSize 每格渲染大小（SVG 单位）
     * @param name     作品名称
     * @return SVG 文本
     */
    public static String buildSvg(List<List<Integer>> grid, List<FuseBeanColorVo> palette, int cellSize, String name) {
        int h = grid.size();
        int w = h > 0 ? grid.get(0).size() : 0;
        PatternLayout layout = createLayout(w, h, palette.size(), cellSize);

        StringBuilder sb = new StringBuilder(8192);
        sb.append("<svg xmlns=\"http://www.w3.org/2000/svg\"")
                .append(" width=\"").append(layout.canvasWidth).append("\" height=\"").append(layout.canvasHeight).append("\"")
                .append(" viewBox=\"0 0 ").append(layout.canvasWidth).append(" ").append(layout.canvasHeight).append("\"")
                .append(" font-family=\"Arial, sans-serif\">\n");
        sb.append("  <rect width=\"100%\" height=\"100%\" fill=\"#ffffff\"/>\n");
        sb.append("  <text x=\"").append(layout.leftMargin).append("\" y=\"16\" font-size=\"12\" font-weight=\"700\" fill=\"#222\">")
                .append(escapeXml(name == null || name.isEmpty() ? "拼豆图纸" : name))
                .append(" · ").append(palette.size()).append(" 色 · ").append(w).append("×").append(h)
                .append("</text>\n");

        for (int x = 0; x < w; x++) {
            int centerX = layout.gridX + x * cellSize + cellSize / 2;
            sb.append(textElement(String.valueOf(x + 1), centerX, layout.gridY - 6, layout.axisFontSize, "#666", "middle", "middle"));
        }
        for (int y = 0; y < h; y++) {
            int centerY = layout.gridY + y * cellSize + cellSize / 2;
            sb.append(textElement(String.valueOf(y + 1), layout.gridX - 6, centerY, layout.axisFontSize, "#666", "end", "middle"));
        }

        for (int y = 0; y < h; y++) {
            int x = 0;
            while (x < w) {
                Integer startCell = grid.get(y).get(x);
                if (startCell == null || startCell < 0 || startCell >= palette.size()) {
                    x++;
                    continue;
                }
                int colorIndex = startCell;
                int runEnd = x + 1;
                while (runEnd < w) {
                    Integer nextCell = grid.get(y).get(runEnd);
                    if (nextCell == null || nextCell < 0 || nextCell != colorIndex) {
                        break;
                    }
                    runEnd++;
                }
                int runLength = runEnd - x;
                int cellX = layout.gridX + x * cellSize;
                int cellY = layout.gridY + y * cellSize;
                FuseBeanColorVo color = palette.get(colorIndex);
                String fill = safeHex(color.getHex());
                if (runLength >= 4) {
                    sb.append("  <rect x=\"").append(cellX + 0.5)
                            .append("\" y=\"").append(cellY + 0.5)
                            .append("\" width=\"").append(runLength * cellSize - 1)
                            .append("\" height=\"").append(cellSize - 1)
                            .append("\" fill=\"").append(fill).append("\"/>\n");
                    for (int k = x; k < runEnd; k++) {
                        int centerX = layout.gridX + k * cellSize + cellSize / 2;
                        int centerY = cellY + cellSize / 2;
                        sb.append(textElement(paletteLabel(color, colorIndex + 1), centerX, centerY,
                                layout.cellFontSize, textColorHex(fill), "middle", "middle"));
                    }
                    x = runEnd;
                } else {
                    for (int k = x; k < runEnd; k++) {
                        int drawX = layout.gridX + k * cellSize;
                        sb.append("  <rect x=\"").append(drawX + 0.5)
                                .append("\" y=\"").append(cellY + 0.5)
                                .append("\" width=\"").append(cellSize - 1)
                                .append("\" height=\"").append(cellSize - 1)
                                .append("\" fill=\"").append(fill).append("\"/>\n");
                        int centerX = drawX + cellSize / 2;
                        int centerY = cellY + cellSize / 2;
                        sb.append(textElement(paletteLabel(color, colorIndex + 1), centerX, centerY,
                                layout.cellFontSize, textColorHex(fill), "middle", "middle"));
                    }
                    x = runEnd;
                }
            }
        }

        sb.append("  <g stroke=\"#d9d9d9\" stroke-width=\"1\">\n");
        for (int x = 0; x <= w; x++) {
            double px = layout.gridX + x * cellSize + 0.5;
            sb.append("    <line x1=\"").append(px).append("\" y1=\"").append(layout.gridY + 0.5)
                    .append("\" x2=\"").append(px).append("\" y2=\"").append(layout.gridY + h * cellSize + 0.5)
                    .append("\"/>\n");
        }
        for (int y = 0; y <= h; y++) {
            double py = layout.gridY + y * cellSize + 0.5;
            sb.append("    <line x1=\"").append(layout.gridX + 0.5).append("\" y1=\"").append(py)
                    .append("\" x2=\"").append(layout.gridX + w * cellSize + 0.5).append("\" y2=\"").append(py)
                    .append("\"/>\n");
        }
        sb.append("  </g>\n");

        for (int i = 0; i < palette.size(); i++) {
            int column = i % layout.legendColumns;
            int row = i / layout.legendColumns;
            int itemX = layout.leftMargin + column * layout.legendItemWidth;
            int itemY = layout.legendTop + row * layout.legendItemHeight;
            FuseBeanColorVo color = palette.get(i);
            sb.append("  <rect x=\"").append(itemX).append("\" y=\"").append(itemY)
                    .append("\" width=\"12\" height=\"12\" fill=\"").append(safeHex(color.getHex()))
                    .append("\" stroke=\"#999\"/>\n");
            sb.append(textElement(legendLabel(color, i + 1), itemX + 18, itemY + 10,
                    layout.legendFontSize, "#333", "start", "middle"));
        }

        sb.append("</svg>");
        return sb.toString();
    }

    /**
     * 统计每个色号的使用数量
     *
     * @param grid    网格数据
     * @param palette 色板
     * @return index -> count
     */
    public static int[] colorStats(List<List<Integer>> grid, int paletteSize) {
        int[] stats = new int[paletteSize];
        for (List<Integer> row : grid) {
            for (Integer cell : row) {
                if (cell != null && cell >= 0 && cell < paletteSize) {
                    stats[cell]++;
                }
            }
        }
        return stats;
    }

    /**
     * 图片转 base64 PNG（带 data:image/png;base64, 前缀）
     */
    public static String toBase64Png(BufferedImage image) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            return DATA_URL_PREFIX + Base64.getEncoder().encodeToString(baos.toByteArray());
        } catch (IOException e) {
            throw new IllegalStateException("图片编码失败", e);
        }
    }

    /**
     * ARGB 颜色转十六进制
     */
    public static String toHex(int argb) {
        return String.format("#%02X%02X%02X", (argb >> 16) & 0xFF, (argb >> 8) & 0xFF, argb & 0xFF);
    }

    private static String safeHex(String hex) {
        return hex == null || hex.isBlank() ? "#FFFFFF" : (hex.startsWith("#") ? hex : "#" + hex).toUpperCase();
    }

    private static int hexToArgb(String hex) {
        String value = safeHex(hex).substring(1);
        return Integer.parseInt(value, 16) | 0xFF000000;
    }

    private static String paletteLabel(FuseBeanColorVo color, int fallbackIndex) {
        if (color != null && color.getCode() != null && !color.getCode().isBlank()) {
            return color.getCode();
        }
        return "#" + fallbackIndex;
    }

    private static String legendLabel(FuseBeanColorVo color, int fallbackIndex) {
        String code = paletteLabel(color, fallbackIndex);
        String hex = color != null && color.getHex() != null && !color.getHex().isBlank() ? safeHex(color.getHex()) : "#FFFFFF";
        return code + " · " + hex;
    }

    private static String textColorHex(String hex) {
        return textColorHex(hexToArgb(hex));
    }

    private static BufferedImage flattenAlpha(BufferedImage source) {
        BufferedImage rgb = new BufferedImage(source.getWidth(), source.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g = rgb.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, rgb.getWidth(), rgb.getHeight());
        g.drawImage(source, 0, 0, null);
        g.dispose();
        return rgb;
    }

    private static BufferedImage resize(BufferedImage source, int w, int h) {
        BufferedImage small = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = small.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(source, 0, 0, w, h, null);
        g.dispose();
        return small;
    }

    private static BufferedImage render(GridResult result, int cellSize, boolean drawGridLine) {
        int w = result.getWidth();
        int h = result.getHeight();
        BufferedImage image = new BufferedImage(w * cellSize, h * cellSize, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, image.getWidth(), image.getHeight());
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int colorIndex = result.getGrid()[y][x];
                if (colorIndex < 0 || colorIndex >= result.getPalette().length) {
                    continue;
                }
                g.setColor(new Color(result.getPalette()[colorIndex]));
                if (drawGridLine) {
                    g.fillRect(x * cellSize + 1, y * cellSize + 1, cellSize - 2, cellSize - 2);
                } else {
                    g.fillRect(x * cellSize, y * cellSize, cellSize, cellSize);
                }
            }
        }
        if (drawGridLine) {
            g.setColor(new Color(0xDDDDDD));
            for (int x = 0; x <= w; x++) {
                g.drawLine(x * cellSize, 0, x * cellSize, h * cellSize);
            }
            for (int y = 0; y <= h; y++) {
                g.drawLine(0, y * cellSize, w * cellSize, y * cellSize);
            }
        }
        g.dispose();
        return image;
    }

    private static BufferedImage renderPatternInternal(GridResult result, int cellSize, boolean drawAnnotations) {
        if (!drawAnnotations) {
            return render(result, cellSize, true);
        }

        int w = result.getWidth();
        int h = result.getHeight();
        PatternLayout layout = createLayout(w, h, result.getPalette().length, cellSize);
        BufferedImage image = new BufferedImage(layout.canvasWidth, layout.canvasHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, image.getWidth(), image.getHeight());
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g.setColor(new Color(0x222222));
        g.setFont(g.getFont().deriveFont(Font.BOLD, 12f));
        g.drawString("拼豆图纸 · " + result.getPalette().length + " 色 · " + w + "×" + h, layout.leftMargin, 16);

        Font axisFont = g.getFont().deriveFont(Font.PLAIN, (float) layout.axisFontSize);
        g.setFont(axisFont);
        FontMetrics axisMetrics = g.getFontMetrics();
        for (int x = 0; x < w; x++) {
            String label = String.valueOf(x + 1);
            int centerX = layout.gridX + x * cellSize + cellSize / 2;
            int textX = centerX - axisMetrics.stringWidth(label) / 2;
            int textY = layout.gridY - 6;
            g.setColor(new Color(0x666666));
            g.drawString(label, textX, textY);
        }
        for (int y = 0; y < h; y++) {
            String label = String.valueOf(y + 1);
            int centerY = layout.gridY + y * cellSize + cellSize / 2;
            int textX = layout.gridX - 6 - axisMetrics.stringWidth(label);
            int textY = centerY + axisMetrics.getAscent() / 2 - 1;
            g.setColor(new Color(0x666666));
            g.drawString(label, textX, textY);
        }

        Font cellFont = g.getFont().deriveFont(Font.BOLD, (float) layout.cellFontSize);
        g.setFont(cellFont);
        FontMetrics cellMetrics = g.getFontMetrics();
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int colorIndex = result.getGrid()[y][x];
                if (colorIndex < 0 || colorIndex >= result.getPalette().length) {
                    continue;
                }
                Color fill = new Color(result.getPalette()[colorIndex]);
                int drawX = layout.gridX + x * cellSize;
                int drawY = layout.gridY + y * cellSize;
                g.setColor(fill);
                g.fillRect(drawX, drawY, cellSize, cellSize);
                g.setColor(contrastColor(fill));
                String text = String.valueOf(colorIndex + 1);
                int textX = drawX + (cellSize - cellMetrics.stringWidth(text)) / 2;
                int textY = drawY + (cellSize - cellMetrics.getHeight()) / 2 + cellMetrics.getAscent();
                g.drawString(text, textX, textY);
            }
        }

        g.setColor(new Color(0xD9D9D9));
        for (int x = 0; x <= w; x++) {
            g.drawLine(layout.gridX + x * cellSize, layout.gridY, layout.gridX + x * cellSize, layout.gridY + h * cellSize);
        }
        for (int y = 0; y <= h; y++) {
            g.drawLine(layout.gridX, layout.gridY + y * cellSize, layout.gridX + w * cellSize, layout.gridY + y * cellSize);
        }

        g.setFont(g.getFont().deriveFont(Font.PLAIN, (float) layout.legendFontSize));
        for (int i = 0; i < result.getPalette().length; i++) {
            int column = i % layout.legendColumns;
            int row = i / layout.legendColumns;
            int itemX = layout.leftMargin + column * layout.legendItemWidth;
            int itemY = layout.legendTop + row * layout.legendItemHeight;
            Color fill = new Color(result.getPalette()[i]);
            g.setColor(fill);
            g.fillRect(itemX, itemY, 12, 12);
            g.setColor(new Color(0x999999));
            g.drawRect(itemX, itemY, 12, 12);
            g.setColor(new Color(0x333333));
            String text = (i + 1) + ". " + toHex(result.getPalette()[i]);
            g.drawString(text, itemX + 18, itemY + 10);
        }

        g.dispose();
        return image;
    }

    private static PatternLayout createLayout(int gridWidth, int gridHeight, int paletteSize, int cellSize) {
        int axisFontSize = Math.max(9, Math.min(12, cellSize - 3));
        int cellFontSize = Math.max(8, Math.min(12, cellSize - 3));
        int legendFontSize = 10;
        int leftMargin = Math.max(28, String.valueOf(Math.max(1, gridHeight)).length() * 8 + 10);
        int topMargin = Math.max(28, axisFontSize + 16);
        int legendColumns = Math.min(3, Math.max(1, paletteSize));
        int legendRows = (paletteSize + legendColumns - 1) / legendColumns;
        int legendItemWidth = 150;
        int legendItemHeight = 22;
        int gridPixelWidth = gridWidth * cellSize;
        int gridPixelHeight = gridHeight * cellSize;
        int legendWidth = legendColumns * legendItemWidth;
        int canvasWidth = Math.max(leftMargin + gridPixelWidth + 12, leftMargin + legendWidth + 12);
        int legendTop = topMargin + gridPixelHeight + 18;
        int canvasHeight = legendTop + legendRows * legendItemHeight + 16;
        return new PatternLayout(leftMargin, topMargin, leftMargin, topMargin, legendTop,
                legendColumns, legendItemWidth, legendItemHeight, cellFontSize, axisFontSize, legendFontSize,
                canvasWidth, canvasHeight);
    }

    private static Color contrastColor(Color fill) {
        int brightness = (fill.getRed() * 299 + fill.getGreen() * 587 + fill.getBlue() * 114) / 1000;
        return brightness >= 160 ? new Color(0x222222) : Color.WHITE;
    }

    private static String textColorHex(int argb) {
        int r = (argb >> 16) & 0xFF;
        int g = (argb >> 8) & 0xFF;
        int b = argb & 0xFF;
        int brightness = (r * 299 + g * 587 + b * 114) / 1000;
        return brightness >= 160 ? "#222222" : "#FFFFFF";
    }

    private static String textElement(String text, int x, int y, int fontSize, String fill, String anchor, String baseline) {
        return new StringBuilder(128)
                .append("  <text x=\"").append(x)
                .append("\" y=\"").append(y)
                .append("\" font-size=\"").append(fontSize)
                .append("\" fill=\"").append(fill)
                .append("\" text-anchor=\"").append(anchor)
                .append("\" dominant-baseline=\"").append(baseline)
                .append("\">")
                .append(escapeXml(text))
                .append("</text>\n")
                .toString();
    }

    private record PatternLayout(
            int leftMargin,
            int topMargin,
            int gridX,
            int gridY,
            int legendTop,
            int legendColumns,
            int legendItemWidth,
            int legendItemHeight,
            int cellFontSize,
            int axisFontSize,
            int legendFontSize,
            int canvasWidth,
            int canvasHeight
    ) {
    }

    /**
     * 基于像素频率 + k-means 的色板压缩
     */
    private static int[] buildPalette(int[] pixels, int maxColors) {
        Map<Integer, Integer> freq = new LinkedHashMap<>();
        for (int p : pixels) {
            freq.merge(p, 1, Integer::sum);
        }
        if (freq.size() <= maxColors) {
            int[] palette = new int[freq.size()];
            int i = 0;
            for (Integer c : freq.keySet()) {
                palette[i++] = c;
            }
            return palette;
        }
        List<Map.Entry<Integer, Integer>> sorted = new ArrayList<>(freq.entrySet());
        sorted.sort((a, b) -> b.getValue() - a.getValue());
        int[] centers = new int[maxColors];
        for (int i = 0; i < maxColors; i++) {
            centers[i] = sorted.get(i).getKey();
        }
        int[] sums = new int[maxColors * 3];
        int[] counts = new int[maxColors];
        for (int iter = 0; iter < 8; iter++) {
            java.util.Arrays.fill(sums, 0);
            java.util.Arrays.fill(counts, 0);
            for (int pixel : pixels) {
                int label = nearestColor(pixel, centers);
                sums[label * 3] += (pixel >> 16) & 0xFF;
                sums[label * 3 + 1] += (pixel >> 8) & 0xFF;
                sums[label * 3 + 2] += pixel & 0xFF;
                counts[label]++;
            }
            for (int c = 0; c < maxColors; c++) {
                if (counts[c] > 0) {
                    int r = sums[c * 3] / counts[c];
                    int g = sums[c * 3 + 1] / counts[c];
                    int b = sums[c * 3 + 2] / counts[c];
                    centers[c] = (r << 16) | (g << 8) | b;
                }
            }
        }
        return centers;
    }

    private static int nearestColor(int pixel, int[] palette) {
        int best = 0;
        long bestDist = Long.MAX_VALUE;
        int pr = (pixel >> 16) & 0xFF;
        int pg = (pixel >> 8) & 0xFF;
        int pb = pixel & 0xFF;
        for (int i = 0; i < palette.length; i++) {
            int c = palette[i];
            int dr = pr - ((c >> 16) & 0xFF);
            int dg = pg - ((c >> 8) & 0xFF);
            int db = pb - (c & 0xFF);
            long dist = dr * dr + dg * dg + db * db;
            if (dist < bestDist) {
                bestDist = dist;
                best = i;
            }
        }
        return best;
    }

    private static int[][] toArray(List<List<Integer>> grid) {
        int h = grid.size();
        int w = h > 0 ? grid.get(0).size() : 0;
        int[][] arr = new int[h][w];
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w && x < grid.get(y).size(); x++) {
                Integer value = grid.get(y).get(x);
                arr[y][x] = value == null ? -1 : value;
            }
        }
        return arr;
    }

    private static int[] toIntArray(List<Integer> list) {
        int[] arr = new int[list.size()];
        for (int i = 0; i < list.size(); i++) {
            arr[i] = list.get(i) == null ? 0 : list.get(i);
        }
        return arr;
    }

    private static String escapeXml(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("\"", "&quot;").replace("'", "&apos;");
    }
}
