package com.freesia.dto;

import com.freesia.dto.BaseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.Date;

/**
 * @author Evad.Wu
 * @Description OSS配置信息表 数据传输对象
 * @date 2024-02-28
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "OSS配置信息表 数据传输对象")
public class SysOssConfigDto extends BaseDto {
    @Schema(description = "配置key")
    private String configKey;
    @Schema(description = "公钥")
    private String accessKey;
    @Schema(description = "秘钥")
    private String secretKey;
    @Schema(description = "桶名称")
    private String bucketName;
    @Schema(description = "前缀")
    private String filePrefix;
    @Schema(description = "访问站点")
    private String endpoint;
    @Schema(description = "自定义域名")
    private String domain;
    @Schema(description = "是否https（0-否 1-是）")
    private Boolean isHttps;
    @Schema(description = "域")
    private String region;
    @Schema(description = "桶权限类型(PRIVATE PUBLIC CUSTOM)")
    private String accessPolicy;
    @Schema(description = "是否默认（0-否 1-是）")
    private Boolean status;
    @Schema(description = "扩展字段")
    private String ext1;
    @Schema(description = "备注")
    private String remark;
}
