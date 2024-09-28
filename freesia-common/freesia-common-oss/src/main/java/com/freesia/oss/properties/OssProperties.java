package com.freesia.oss.properties;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author Evad.Wu
 * @Description OSS对象存储 属性类
 * @date 2024-02-27
 */
@Data
public class OssProperties {
    @Schema(description = "访问站点")
    private String endpoint;
    @Schema(description = "自定义域名")
    private String domain;
    @Schema(description = "前缀")
    private String prefix;
    @Schema(description = "公钥")
    private String accessKey;
    @Schema(description = "私钥")
    private String secretKey;
    @Schema(description = "存储空间名")
    private String bucketName;
    @Schema(description = "存储区域")
    private String region;
    @Schema(description = "是否https（0-否, 1-是）")
    private Boolean isHttps;
    @Schema(description = "桶权限类型(PRIVATE PUBLIC CUSTOM)")
    private String accessPolicy;
}
