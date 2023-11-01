package com.freesia.download.vo;

import com.freesia.download.constant.PictureSuffixConstant;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Evad.Wu
 * @Description 资源下载 值对象
 * @date 2022-09-07
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "资源下载 值对象")
public class DownloadVo {
    @Schema(description = "导出路径")
    private String exportPath;
    @Schema(description = "导出文件名")
    private String fileName;
    /**
     * {@link PictureSuffixConstant}
     */
    @Schema(description = "文件后缀")
    private String suffix;
}
