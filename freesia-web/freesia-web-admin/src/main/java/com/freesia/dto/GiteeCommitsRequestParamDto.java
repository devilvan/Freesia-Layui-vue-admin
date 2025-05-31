package com.freesia.dto;


import com.fasterxml.jackson.annotation.JsonAlias;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Evad.Wu
 * @Description Gitee提交更新记录-请求参数 数据传输对象
 * @date 2024-01-15
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Gitee提交更新记录-请求参数 数据传输对象")
public class GiteeCommitsRequestParamDto {

    @Schema(description = "令牌")
    private String accessToken;
    @Schema(description = "当前页")
    private Integer page;
    @Schema(description = "页面大小")
    @JsonAlias(value = "per_page")
    private Integer perPage;
}
