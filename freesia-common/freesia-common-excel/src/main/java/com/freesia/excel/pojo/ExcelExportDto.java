package com.freesia.excel.pojo;

import com.alibaba.excel.support.ExcelTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

/**
 * @author Evad.Wu
 * @Description Excel写操作 数据传输对象
 * @date 2022-09-07
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Excel写操作 数据传输对象")
public class ExcelExportDto {
    @Schema(description = "导出路径")
    @NotEmpty(message = "{not.null}")
    private String exportPath;
    @Schema(description = "导出文件名")
    @NotEmpty(message = "{not.null}")
    private String fileName;
    @Schema(description = "文件后缀")
    @NotEmpty(message = "{not.null}")
    private ExcelTypeEnum suffix;
    @Schema(description = "表单")
    @NotEmpty(message = "{not.null}")
    private String sheet;
    @Schema(description = "导出的数据类型")
    @NotEmpty(message = "{not.null}")
    private Class<?> classType;
}
