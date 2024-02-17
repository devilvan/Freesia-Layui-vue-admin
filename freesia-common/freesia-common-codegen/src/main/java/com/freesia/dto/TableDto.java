package com.freesia.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 数据源信息 数据传输对象
 * @date 2022-09-13
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "数据源信息 数据传输对象")
public class TableDto extends DataSourceDto {
    @Schema(description = "需要生成的表名")
    private List<String> tableList;

    public TableDto(String driver, String url, String uname, String pwd) {
        super(driver, url, uname, pwd);
    }
}
