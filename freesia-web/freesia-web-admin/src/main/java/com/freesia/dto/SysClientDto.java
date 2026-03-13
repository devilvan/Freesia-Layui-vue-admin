package com.freesia.dto;

import com.freesia.dto.BaseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.Date;
import java.math.BigDecimal;

/**
 * @author Evad.Wu
 * @Description 系统用户授权表 数据传输对象
 * @date 2026-03-13
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "系统用户授权表 数据传输对象")
public class SysClientDto extends BaseDto {
    @Schema(description = "客户端ID")
    private Long clientId;
    @Schema(description = "客户端key")
    private String clientKey;
    @Schema(description = "客户端秘钥")
    private String clientSecret;
    @Schema(description = "授权类型")
    private String grantType;
    @Schema(description = "设备类型")
    private String deviceType;
    @Schema(description = "token活跃超时时间（单位：秒）")
    private Integer activeTimeout;
    @Schema(description = "token固定超时（单位：秒）")
    private Integer timeout;
}
