package com.freesia.fusebean.controller;

import com.freesia.controller.BaseController;
import com.freesia.fusebean.dto.FuseBeanConfirmReqDto;
import com.freesia.fusebean.service.FuseBeanService;
import com.freesia.fusebean.vo.FuseBeanConfirmRespVo;
import com.freesia.fusebean.vo.FuseBeanGenerateRespVo;
import com.freesia.vo.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Evad.Wu
 * @Description 拼豆 控制器
 * @date 2026-08-26
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/fusebean")
@Tag(name = "FuseBeanController", description = "拼豆 控制器")
public class FuseBeanController extends BaseController {

    private final FuseBeanService fuseBeanService;

    /**
     * 生成拼豆像素风图片
     *
     * @param file      上传的原图（可选，纯提示词生成时需配置外部接口）
     * @param prompt    用户输入的提示词
     * @param gridSize  网格最大边长
     * @param maxColors 最大颜色数
     * @return 像素预览图 + 网格数据
     */
    @Operation(summary = "生成拼豆像素风图片")
    @PostMapping(value = "generateImage", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public R<FuseBeanGenerateRespVo> generateImage(@RequestParam(value = "file", required = false) MultipartFile file,
                                                   @RequestParam(value = "prompt", required = false) String prompt,
                                                   @RequestParam(value = "gridSize", required = false) Integer gridSize,
                                                   @RequestParam(value = "maxColors", required = false) Integer maxColors) {
        FuseBeanGenerateRespVo vo = fuseBeanService.generateImage(file, prompt, gridSize, maxColors);
        return R.ok(vo);
    }

    /**
     * 确认生成拼豆图纸
     *
     * @param reqDto 网格与色板
     * @return 图纸（网格图 + SVG + 色号清单）
     */
    @Operation(summary = "确认生成拼豆图纸")
    @PostMapping(value = "confirmGenerate")
    public R<FuseBeanConfirmRespVo> confirmGenerate(@RequestBody FuseBeanConfirmReqDto reqDto) {
        FuseBeanConfirmRespVo vo = fuseBeanService.confirmGenerate(reqDto);
        return R.ok(vo);
    }
}
