package com.freesia.vo;

import com.fasterxml.jackson.annotation.JsonAlias;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author Evad.Wu
 * @Description OSS配置信息表 值对象
 * @date 2024-02-28
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "OSS配置信息表 值对象")
public class SysOssConfigVo {
    @Schema(description = "配置key")
    @JsonAlias(value = {"configKey"})
    private String configKey;
    @Schema(description = "公钥")
    @JsonAlias(value = {"accessKey"})
    private String accessKey;
    @Schema(description = "秘钥")
    @JsonAlias(value = {"secretKey"})
    private String secretKey;
    @Schema(description = "桶名称")
    @JsonAlias(value = {"bucketName"})
    private String bucketName;
    @Schema(description = "前缀")
    @JsonAlias(value = {"filePrefix"})
    private String filePrefix;
    @Schema(description = "访问站点")
    @JsonAlias(value = {"endpoint"})
    private String endpoint;
    @Schema(description = "自定义域名")
    @JsonAlias(value = {"domain"})
    private String domain;
    @Schema(description = "是否https（0-否 1-是）")
    @JsonAlias(value = {"isHttps"})
    private Boolean isHttps;
    @Schema(description = "域")
    @JsonAlias(value = {"region"})
    private String region;
    @Schema(description = "桶权限类型(PRIVATE PUBLIC CUSTOM)")
    @JsonAlias(value = {"accessPolicy"})
    private String accessPolicy;
    @Schema(description = "是否默认（0-否 1-是）")
    @JsonAlias(value = {"status"})
    private Boolean status;
    @Schema(description = "扩展字段")
    @JsonAlias(value = {"ext1"})
    private String ext1;
    @Schema(description = "备注")
    @JsonAlias(value = {"remark"})
    private String remark;
}
