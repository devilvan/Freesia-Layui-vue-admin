package com.freesia.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Evad.Wu
 * @Description 资源下载文件后缀 枚举类
 * @date 2022-09-12
 */
@Getter
@AllArgsConstructor
public enum PictureSuffixConstant {
    /**
     * JPG
     */
    JPG(".jpg"),
    /**
     * PNG
     */
    PNG(".png");

    private final String value;

    /**
     * 根据输入后缀名获取枚举对象
     *
     * @param suffix 输入的后缀
     * @return 对应枚举对象
     */
    public static PictureSuffixConstant getInstanceBySuffix(String suffix) {
        PictureSuffixConstant[] values = PictureSuffixConstant.values();
        for (PictureSuffixConstant value : values) {
            String name = value.name();
            if (name.equalsIgnoreCase(suffix)) {
                return value;
            }
        }
        return JPG;
    }
}
