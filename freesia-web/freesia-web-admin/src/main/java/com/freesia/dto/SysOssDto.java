package com.freesia.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author Evad.Wu
 * @Description OSS对象存储表 数据传输对象
 * @date 2024-02-28
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "OSS对象存储表 数据传输对象")
public class SysOssDto extends BaseDto {
    @Schema(description = "文件名")
    private String fileName;
    @Schema(description = "原名")
    private String originalName;
    @Schema(description = "文件后缀名")
    private String fileSuffix;
    @Schema(description = "URL地址")
    private String url;
    @Schema(description = "服务商")
    private String service;
    @Schema(description = "是否为临时文件（0-否 1-是）")
    private Boolean tempFlag;
    @Schema(description = "文件大小")
    private Long fileSize;
}
