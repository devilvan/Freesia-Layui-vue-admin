package com.freesia.fusebean.util;

import javax.imageio.ImageIO;
import java.awt.Color;
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
        return render(result, cellSize, true);
    }

    /**
     * 基于前端回传的网格与色板渲染图纸网格图
     *
     * @param grid     网格数据（行优先，每格为色板索引）
     * @param palette  色板 ARGB 颜色数组
     * @param cellSize 每格渲染像素大小
     * @return 图纸网格图
     */
    public static BufferedImage renderPattern(List<List<Integer>> grid, List<Integer> palette, int cellSize) {
        int h = grid.size();
        int w = h > 0 ? grid.get(0).size() : 0;
        return render(new GridResult(toArray(grid), toIntArray(palette), w, h), cellSize, true);
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
    public static String buildSvg(List<List<Integer>> grid, List<Integer> palette, int cellSize, String name) {
        int h = grid.size();
        int w = h > 0 ? grid.get(0).size() : 0;
        int legendSize = 18;
        int legendGap = 12;
        int svgWidth = w * cellSize;
        int svgHeight = h * cellSize + legendGap + legendSize;

        StringBuilder sb = new StringBuilder(4096);
        sb.append("<svg xmlns=\"http://www.w3.org/2000/svg\"")
                .append(" width=\"").append(svgWidth).append("\" height=\"").append(svgHeight).append("\"")
                .append(" viewBox=\"0 0 ").append(svgWidth).append(" ").append(svgHeight).append("\"")
                .append(" font-family=\"Arial, sans-serif\">\n");
        sb.append("  <rect width=\"100%\" height=\"100%\" fill=\"#ffffff\"/>\n");
        // 网格主体：每行连续同色格子合并为一个矩形，减少节点数
        for (int y = 0; y < h; y++) {
            int x = 0;
            while (x < w) {
                int colorIndex = grid.get(y).get(x);
                int runEnd = x + 1;
                while (runEnd < w && grid.get(y).get(runEnd) == colorIndex) {
                    runEnd++;
                }
                int runLength = runEnd - x;
                if (runLength >= 4) {
                    // 整段合并为一个矩形（留 0.5 间隙保证网格线可见）
                    sb.append("  <rect x=\"").append(x * cellSize + 0.5)
                            .append("\" y=\"").append(y * cellSize + 0.5)
                            .append("\" width=\"").append(runLength * cellSize - 1)
                            .append("\" height=\"").append(cellSize - 1)
                            .append("\" fill=\"").append(toHex(palette.get(colorIndex))).append("\"/>\n");
                    x = runEnd;
                } else {
                    for (int k = x; k < runEnd; k++) {
                        sb.append("  <rect x=\"").append(k * cellSize + 0.5)
                                .append("\" y=\"").append(y * cellSize + 0.5)
                                .append("\" width=\"").append(cellSize - 1)
                                .append("\" height=\"").append(cellSize - 1)
                                .append("\" fill=\"").append(toHex(palette.get(colorIndex))).append("\"/>\n");
                    }
                    x = runEnd;
                }
            }
        }
        // 图例
        int legendY = h * cellSize + legendGap;
        sb.append("  <text x=\"0\" y=\"").append(legendY - 2).append("\" font-size=\"12\" fill=\"#333\">")
                .append(escapeXml(name == null || name.isEmpty() ? "拼豆图纸" : name))
                .append(" · ").append(palette.size()).append(" 色 · ").append(w).append("×").append(h)
                .append("</text>\n");
        for (int i = 0; i < palette.size(); i++) {
            int px = i * (legendSize + 8);
            sb.append("  <rect x=\"").append(px).append("\" y=\"").append(legendY)
                    .append("\" width=\"").append(legendSize).append("\" height=\"").append(legendSize)
                    .append("\" fill=\"").append(toHex(palette.get(i))).append("\" stroke=\"#ccc\"/>\n");
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
                g.setColor(new Color(result.getPalette()[result.getGrid()[y][x]]));
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
                arr[y][x] = grid.get(y).get(x) == null ? 0 : grid.get(y).get(x);
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
