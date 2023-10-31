package com.freesia.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Evad.Wu
 * @Description 表字段及转换相关字段
 * @date 2022-09-13
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "表字段及转换相关字段")
public class ColumnFieldDto {
    @Schema(description = "字段名称")
    private String columnName;
    @Schema(description = "字段名称转换的属性名称")
    private String fieldName;
    @Schema(description = "字段类型")
    private String columnType;
    @Schema(description = "Java类型")
    private String javaType;
    @Schema(description = "字段长度")
    private Integer dataSize;
    @Schema(description = "精度")
    private Integer digits;
    @Schema(description = "是否为空")
    private Boolean nullable;
    @Schema(description = "字段描述")
    private String remark;
}
