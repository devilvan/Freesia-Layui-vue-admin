package com.freesia.dto;

import com.freesia.dto.BaseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.Date;

/**
 * @author Evad.Wu
 * @Description URL配置信息表 数据传输对象
 * @date 2024-01-24
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "URL配置信息表 数据传输对象")
public class UrlConfigDto extends BaseDto {
    @Schema(description = "配置标识")
    private String code;
    @Schema(description = "网址")
    private String url;
    @Schema(description = "请求方式")
    private String requestType;
    @Schema(description = "请求头信息")
    private String header;
    @Schema(description = "请求参数")
    private String param;
    @Schema(description = "内容类型")
    private String contentType;
    @Schema(description = "备注")
    private String remark;
}
