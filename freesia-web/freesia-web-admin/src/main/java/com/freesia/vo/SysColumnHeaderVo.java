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
 * @Description 系统列头表 值对象
 * @date 2026-03-16
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "系统列头表 值对象")
public class SysColumnHeaderVo extends BaseVo {
    @Schema(description = "组件名")
    @JsonAlias(value = {"component"})
    private String component;
    @Schema(description = "是否启用（true-是；false-否）")
    @JsonAlias(value = {"enabled"})
    private Boolean enabled;
}
