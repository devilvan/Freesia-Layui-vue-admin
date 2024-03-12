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
public enum ExcelSuffix {
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

    /**
     * 后缀
     */
    String suffix;

    /**
     * 判断入参的类型是否为合法的EXCEL类型
     *
     * @param suffix 后缀名
     * @return flag
     */
    public static boolean includeBySuffix(String suffix) {
        ExcelSuffix[] excelSuffixes = ExcelSuffix.values();
        for (ExcelSuffix excelSuffix : excelSuffixes) {
            if (excelSuffix.name().equalsIgnoreCase(suffix)) {
                return true;
            }
        }
        return false;
    }
}
