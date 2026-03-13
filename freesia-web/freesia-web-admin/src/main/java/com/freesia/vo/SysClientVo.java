package com.freesia.vo;

import com.fasterxml.jackson.annotation.JsonAlias;
import io.swagger.v3.oas.annotations.media.Schema;
import com.freesia.vo.BaseVo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.math.BigDecimal;

/**
 * @author Evad.Wu
 * @Description 系统用户授权表 值对象
 * @date 2026-03-13
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "系统用户授权表 值对象")
public class SysClientVo extends BaseVo {
    @Schema(description = "客户端ID")
    @JsonAlias(value = {"clientId"})
    private Long clientId;
    @Schema(description = "客户端key")
    @JsonAlias(value = {"clientKey"})
    private String clientKey;
    @Schema(description = "客户端秘钥")
    @JsonAlias(value = {"clientSecret"})
    private String clientSecret;
    @Schema(description = "授权类型")
    @JsonAlias(value = {"grantType"})
    private String grantType;
    @Schema(description = "设备类型")
    @JsonAlias(value = {"deviceType"})
    private String deviceType;
    @Schema(description = "token活跃超时时间（单位：秒）")
    @JsonAlias(value = {"activeTimeout"})
    private Integer activeTimeout;
    @Schema(description = "token固定超时（单位：秒）")
    @JsonAlias(value = {"timeout"})
    private Integer timeout;
}
