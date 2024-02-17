package com.freesia.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Evad.Wu
 * @Description 数据源信息 数据传输对象
 * @date 2022-09-13
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "数据源信息 数据传输对象")
public class DataSourceDto {
    @Schema(description = "数据库驱动")
    private String driver;
    @Schema(description = "数据库URL")
    private String url;
    @Schema(description = "账号")
    private String uname;
    @Schema(description = "密码")
    private String pwd;
}
