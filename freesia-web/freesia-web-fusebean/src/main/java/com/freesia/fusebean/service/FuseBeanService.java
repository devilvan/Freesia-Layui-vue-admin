package com.freesia.fusebean.service;

import com.freesia.fusebean.dto.FuseBeanConfirmReqDto;
import com.freesia.fusebean.vo.FuseBeanConfirmRespVo;
import com.freesia.fusebean.vo.FuseBeanGenerateRespVo;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Evad.Wu
 * @Description 拼豆 业务逻辑接口
 * @date 2026-08-26
 */
public interface FuseBeanService {

    /**
     * 生成拼豆像素风图片
     *
     * @param file      上传的原图（可空，纯提示词生成时依赖外部接口）
     * @param prompt    用户输入的提示词
     * @param gridSize  网格最大边长
     * @param maxColors 最大颜色数
     * @return 像素预览图 + 网格数据
     */
    FuseBeanGenerateRespVo generateImage(MultipartFile file, String prompt, Integer gridSize, Integer maxColors);

    /**
     * 确认生成拼豆图纸
     *
     * @param reqDto 网格与色板
     * @return 图纸（网格图 + SVG + 色号清单）
     */
    FuseBeanConfirmRespVo confirmGenerate(FuseBeanConfirmReqDto reqDto);
}
