package com.freesia.helper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author Evad.Wu
 * @Description 数据库 帮助类
 * @date 2023-09-07
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DataBaseHelper {
    /**
     * 构建SQL find_in_set语法
     *
     * @param str   查询条件
     * @param field 查询的数据
     * @param <T>   查询条件的类型
     * @return 构建的SQL
     */
    public static <T> String findInSet(T str, String field) {
        return "find_in_set('" + str + "' , " + field + ") <> 0";
    }
}
