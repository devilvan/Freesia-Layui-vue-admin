package com.freesia.fusebean.service;

import com.freesia.fusebean.dto.FuseBeanConfirmReqDto;
import com.freesia.fusebean.vo.FuseBeanConfirmRespVo;
import com.freesia.fusebean.vo.FuseBeanGenerateRespVo;
import org.springframework.web.multipart.MultipartFile;

/**
 * 拼豆业务逻辑接口
 */
public interface FuseBeanService {

    /**
     * 生成拼豆像素风图片
     *
     * @param file             上传的原图（可空，纯提示词生成时依赖外部接口）
     * @param prompt           用户输入的提示词
     * @param gridSize         网格最大边长
     * @param maxColors        最大颜色数
     * @param processingMode   处理模式：edge / average / dominant
     * @param removeBackground 是否去除背景
     * @param flipHorizontal   是否水平翻转
     * @return 像素预览图 + 网格数据
     */
    FuseBeanGenerateRespVo generateImage(MultipartFile file,
                                         String prompt,
                                         Integer gridSize,
                                         Integer maxColors,
                                         String processingMode,
                                         Boolean removeBackground,
                                         Boolean flipHorizontal);

    /**
     * 确认生成拼豆图纸
     *
     * @param reqDto 网格与色板
     * @return 图纸（网格图 + SVG + 色号清单）
     */
    FuseBeanConfirmRespVo confirmGenerate(FuseBeanConfirmReqDto reqDto);
}
