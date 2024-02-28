package com.freesia.oss.dto;

import com.freesia.dto.BaseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.Date;

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
}
