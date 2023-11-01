package com.freesia.pojo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Evad.Wu
 * @Description 资源下载 实体类
 * @date 2022-11-17
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "资源下载 实体类")
public class DownloadDto {
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
