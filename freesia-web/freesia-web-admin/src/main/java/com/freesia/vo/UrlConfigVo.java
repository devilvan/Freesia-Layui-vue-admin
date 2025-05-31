package com.freesia.vo;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.freesia.vo.BaseVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author Evad.Wu
 * @Description URL配置信息表 值对象
 * @date 2024-01-24
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "URL配置信息表 值对象")
public class UrlConfigVo extends BaseVo {
    @Schema(description = "配置标识")
    @JsonAlias(value = {"code"})
    private String code;
    @Schema(description = "网址")
    @JsonAlias(value = {"url"})
    private String url;
    @Schema(description = "请求方式")
    @JsonAlias(value = {"requestType"})
    private String requestType;
    @Schema(description = "请求头信息")
    @JsonAlias(value = {"header"})
    private String header;
    @Schema(description = "请求参数")
    @JsonAlias(value = {"param"})
    private String param;
    @Schema(description = "内容类型")
    @JsonAlias(value = {"contentType"})
    private String contentType;
    @Schema(description = "备注")
    @JsonAlias(value = {"remark"})
    private String remark;
}
