package com.freesia.oss.vo;

import com.alibaba.fastjson.annotation.JSONField;
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
    @JSONField(alternateNames = {"configKey"})
    private String configKey;
    @Schema(description = "公钥")
    @JSONField(alternateNames = {"accessKey"})
    private String accessKey;
    @Schema(description = "秘钥")
    @JSONField(alternateNames = {"secretKey"})
    private String secretKey;
    @Schema(description = "桶名称")
    @JSONField(alternateNames = {"bucketName"})
    private String bucketName;
    @Schema(description = "前缀")
    @JSONField(alternateNames = {"filePrefix"})
    private String filePrefix;
    @Schema(description = "访问站点")
    @JSONField(alternateNames = {"endpoint"})
    private String endpoint;
    @Schema(description = "自定义域名")
    @JSONField(alternateNames = {"domain"})
    private String domain;
    @Schema(description = "是否https（0-否 1-是）")
    @JSONField(alternateNames = {"isHttps"})
    private Boolean isHttps;
    @Schema(description = "域")
    @JSONField(alternateNames = {"region"})
    private String region;
    @Schema(description = "桶权限类型(PRIVATE PUBLIC CUSTOM)")
    @JSONField(alternateNames = {"accessPolicy"})
    private String accessPolicy;
    @Schema(description = "是否默认（0-否 1-是）")
    @JSONField(alternateNames = {"status"})
    private Boolean status;
    @Schema(description = "扩展字段")
    @JSONField(alternateNames = {"ext1"})
    private String ext1;
    @Schema(description = "备注")
    @JSONField(alternateNames = {"remark"})
    private String remark;
}
