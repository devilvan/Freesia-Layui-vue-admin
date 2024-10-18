package com.freesia.util;


/**
 * @author Evad.Wu
 * @Description 校验与装配 工具类
 * @date 2022-09-12
 */
public class UPath {
    /**
     * 验证导出路径是否正确
     *
     * @param exportPath 导出路径
     * @param fileName   文件名
     * @param suffix     后缀
     * @return 验证后的导出路径
     */
    public static String validExportPath(String exportPath, String fileName, String suffix) {
        String split = "\\";
        StringBuilder sb = new StringBuilder();
        sb.append(exportPath);
        // 如果路径后没加\\
        if (!exportPath.endsWith(split)) {
            sb.append(split);
        }
        if (fileName.startsWith(split)) {
            sb.append(fileName.substring(fileName.indexOf(split) + 1));
        } else {
            sb.append(fileName);
        }
        sb.append(suffix);
        return sb.toString();
    }
}
