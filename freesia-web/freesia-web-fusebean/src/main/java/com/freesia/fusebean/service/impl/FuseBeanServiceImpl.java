package com.freesia.fusebean.service.impl;

import com.freesia.fusebean.component.FuseBeanExternalClient;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Evad.Wu
 * @Description 拼豆 业务逻辑实现
 * @date 2026-08-26
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FuseBeanServiceImpl implements FuseBeanService {

    private final FuseBeanExternalClient externalClient;

    @Value("${freesia.fusebean.grid-size:50}")
    private int defaultGridSize;

    @Value("${freesia.fusebean.max-colors:18}")
    private int defaultMaxColors;

    @Value("${freesia.fusebean.cell-size:14}")
    private int defaultCellSize;

    @Value("${freesia.fusebean.preview-cell-size:10}")
    private int previewCellSize;

    @Override
    public FuseBeanGenerateRespVo generateImage(MultipartFile file, String prompt, Integer gridSize, Integer maxColors) {
        int targetGrid = gridSize != null && gridSize > 0 ? gridSize : defaultGridSize;
        int targetColors = maxColors != null && maxColors > 0 ? maxColors : defaultMaxColors;

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

        // 优先调用外部生成接口；失败或未配置时本地像素化兜底
        BufferedImage pixelSource = null;
        String message;
        if (source != null || UEmpty.isNotEmpty(prompt)) {
            pixelSource = externalClient.generate(imageBytes == null ? new byte[0] : imageBytes, prompt);
        }
        if (pixelSource != null) {
            message = "由外部拼豆生成接口生成";
        } else {
            if (source == null) {
                throw new IllegalArgumentException("本地像素化需要上传图片，或请在配置中启用外部生成接口");
            }
            pixelSource = source;
            message = "由本地像素化算法生成";
        }

        GridResult result = FuseBeanPixelArtUtil.toGrid(pixelSource, targetGrid, targetColors);
        BufferedImage preview = FuseBeanPixelArtUtil.renderPreview(result, previewCellSize);

        FuseBeanGenerateRespVo vo = new FuseBeanGenerateRespVo();
        vo.setPreviewBase64(FuseBeanPixelArtUtil.toBase64Png(preview));
        vo.setGridWidth(result.getWidth());
        vo.setGridHeight(result.getHeight());
        vo.setPalette(buildPalette(result.getPalette()));
        vo.setGrid(toGridList(result.getGrid()));
        vo.setMessage(message);
        return vo;
    }

    @Override
    public FuseBeanConfirmRespVo confirmGenerate(FuseBeanConfirmReqDto reqDto) {
        List<List<Integer>> grid = reqDto.getGrid();
        List<FuseBeanColorVo> paletteVo = reqDto.getPalette();
        if (grid == null || grid.isEmpty() || paletteVo == null || paletteVo.isEmpty()) {
            throw new IllegalArgumentException("图纸数据缺失，请先生成拼豆像素风图片");
        }
        int height = grid.size();
        int width = grid.get(0).size();
        int cellSize = reqDto.getCellSize() != null && reqDto.getCellSize() > 0 ? reqDto.getCellSize() : defaultCellSize;

        List<Integer> palette = new ArrayList<>(paletteVo.size());
        for (FuseBeanColorVo color : paletteVo) {
            palette.add(parseHex(color.getHex()));
        }

        BufferedImage patternImage = FuseBeanPixelArtUtil.renderPattern(grid, palette, cellSize);
        String svg = FuseBeanPixelArtUtil.buildSvg(grid, palette, cellSize, reqDto.getName());
        int[] stats = FuseBeanPixelArtUtil.colorStats(grid, palette.size());

        FuseBeanConfirmRespVo vo = new FuseBeanConfirmRespVo();
        vo.setName(reqDto.getName());
        vo.setGridWidth(width);
        vo.setGridHeight(height);
        vo.setCellSize(cellSize);
        vo.setPatternPngBase64(FuseBeanPixelArtUtil.toBase64Png(patternImage));
        vo.setPatternSvg(svg);
        vo.setColorStats(buildColorStats(stats, paletteVo));
        return vo;
    }

    private List<FuseBeanColorVo> buildPalette(int[] palette) {
        List<FuseBeanColorVo> list = new ArrayList<>(palette.length);
        for (int i = 0; i < palette.length; i++) {
            FuseBeanColorVo color = new FuseBeanColorVo();
            color.setIndex(i);
            color.setHex(FuseBeanPixelArtUtil.toHex(palette[i]));
            list.add(color);
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

    private List<FuseBeanColorStatVo> buildColorStats(int[] stats, List<FuseBeanColorVo> palette) {
        List<FuseBeanColorStatVo> list = new ArrayList<>();
        for (int i = 0; i < stats.length && i < palette.size(); i++) {
            if (stats[i] <= 0) {
                continue;
            }
            FuseBeanColorStatVo stat = new FuseBeanColorStatVo();
            stat.setIndex(i);
            stat.setHex(palette.get(i).getHex());
            stat.setCount(stats[i]);
            list.add(stat);
        }
        return list;
    }

    private int parseHex(String hex) {
        if (UEmpty.isEmpty(hex)) {
            throw new IllegalArgumentException("色板颜色值缺失");
        }
        String value = hex.trim().startsWith("#") ? hex.trim().substring(1) : hex.trim();
        try {
            return Integer.parseInt(value, 16) | 0xFF000000;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("色板颜色值非法: " + hex);
        }
    }
}
