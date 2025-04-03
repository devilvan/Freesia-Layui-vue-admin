package com.freesia.vo;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author Evad.Wu
 * @Description OSS对象存储表 值对象
 * @date 2024-02-28
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "OSS对象存储表 值对象")
public class SysOssVo extends BaseVo {
    @Schema(description = "文件名")
    @JSONField(alternateNames = {"fileName"})
    private String fileName;
    @Schema(description = "原名")
    @JSONField(alternateNames = {"originalName"})
    private String originalName;
    @Schema(description = "文件后缀名")
    @JSONField(alternateNames = {"fileSuffix"})
    private String fileSuffix;
    @Schema(description = "URL地址")
    @JSONField(alternateNames = {"url"})
    private String url;
    @Schema(description = "服务商")
    @JSONField(alternateNames = {"service"})
    private String service;
    @Schema(description = "是否为临时文件（0-否 1-是）")
    private Boolean tempFlag;
    @Schema(description = "文件大小")
    private Long fileSize;
}
