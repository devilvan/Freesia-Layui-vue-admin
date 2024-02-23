package com.freesia.excel.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Evad.Wu
 * @Description 导出excel文件后缀
 * @date 2022-09-08
 */
@Getter
@AllArgsConstructor
public enum ExcelExportSuffix {
    /**
     * XLS类型
     */
    XLS("xls"),
    /**
     * XLSX类型
     */
    XLSX("xlsx"),
    /**
     * CSV类型
     */
    CSV("csv");


    String suffix;
}
