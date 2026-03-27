package com.freesia.vo;

import com.fasterxml.jackson.annotation.JsonAlias;
import io.swagger.v3.oas.annotations.media.Schema;
import com.freesia.vo.BaseVo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.math.BigDecimal;

/**
 * @author Evad.Wu
 * @Description 系统列中间表 值对象
 * @date 2026-03-27
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "系统列中间表 值对象")
public class SysColumnMiddleVo extends BaseVo {
    @Schema(description = "系统列头ID")
    @JsonAlias(value = {"headerId"})
    private Long headerId;
    @Schema(description = "列名")
    @JsonAlias(value = {"title"})
    private String title;
    @Schema(description = "属性名")
    @JsonAlias(value = {"name"})
    private String name;
    @Schema(description = "是否可用（true-是；false-否）")
    @JsonAlias(value = {"enabled"})
    private Boolean enabled;
}
