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
 * 拼豆控制器
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/fusebean")
@Tag(name = "FuseBeanController", description = "拼豆控制器")
public class FuseBeanController extends BaseController {

    private final FuseBeanService fuseBeanService;

    /**
     * 生成拼豆像素风图片
     */
    @Operation(summary = "生成拼豆像素风图片")
    @PostMapping(value = "generateImage", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public R<FuseBeanGenerateRespVo> generateImage(@RequestParam(value = "file", required = false) MultipartFile file,
                                                   @RequestParam(value = "prompt", required = false) String prompt,
                                                   @RequestParam(value = "gridSize", required = false) Integer gridSize,
                                                   @RequestParam(value = "maxColors", required = false) Integer maxColors,
                                                   @RequestParam(value = "processingMode", required = false, defaultValue = "edge") String processingMode,
                                                   @RequestParam(value = "mergeSimilarColors", required = false, defaultValue = "false") Boolean mergeSimilarColors,
                                                   @RequestParam(value = "removeBackground", required = false, defaultValue = "false") Boolean removeBackground,
                                                   @RequestParam(value = "flipHorizontal", required = false, defaultValue = "false") Boolean flipHorizontal,
                                                   @RequestParam(value = "aiStylePrompt", required = false) String aiStylePrompt) {
        FuseBeanGenerateRespVo vo = fuseBeanService.generateImage(file, prompt, gridSize, maxColors, processingMode, mergeSimilarColors, removeBackground, flipHorizontal, aiStylePrompt);
        return R.ok(vo);
    }

    /**
     * 确认生成拼豆图纸
     */
    @Operation(summary = "确认生成拼豆图纸")
    @PostMapping(value = "confirmGenerate")
    public R<FuseBeanConfirmRespVo> confirmGenerate(@RequestBody FuseBeanConfirmReqDto reqDto) {
        FuseBeanConfirmRespVo vo = fuseBeanService.confirmGenerate(reqDto);
        return R.ok(vo);
    }
}
