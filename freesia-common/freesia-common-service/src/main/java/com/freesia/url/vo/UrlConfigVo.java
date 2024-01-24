package com.freesia.url.vo;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author Evad.Wu
 * @Description URL配置信息表 值对象
 * @date 2024-01-24
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "URL配置信息表 值对象")
public class UrlConfigVo {
    @Schema(description = "网址")
    @JSONField(alternateNames = {"url"})
    private String url;
    @Schema(description = "请求方式")
    @JSONField(alternateNames = {"requestType"})
    private String requestType;
    @Schema(description = "请求头信息")
    @JSONField(alternateNames = {"header"})
    private String header;
    @Schema(description = "请求参数")
    @JSONField(alternateNames = {"param"})
    private String param;
    @Schema(description = "内容类型")
    @JSONField(alternateNames = {"contentType"})
    private String contentType;
}
