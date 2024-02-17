package com.freesia.util;

import cn.hutool.core.util.ObjectUtil;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.Map;

/**
 * @author Evad.Wu
 * @Description 判空 工具类
 * @date 2022-09-25
 */
@SuppressWarnings(value = "unused")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UEmpty extends ObjectUtil {

    public static boolean isNotEmpty(String value) {
        return !isEmpty(value);
    }

    public static boolean isEmpty(String value) {
        return value == null || value.length() < 1;
    }

    public static boolean isAllEmpty(String[] stringArray) {
        if (stringArray != null) {
            for (String s : stringArray) {
                if (!isEmpty(s)) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean hasOneEmpty(String[] stringArray) {
        if (stringArray != null) {
            for (String s : stringArray) {
                if (isEmpty(s)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isTrimEmpty(String value) {
        int strLen;
        if (value != null && (strLen = value.length()) != 0) {
            for (int i = 0; i < strLen; ++i) {
                if (!Character.isWhitespace(value.charAt(i))) {
                    return false;
                }
            }

        }
        return true;
    }

    public static boolean isNotBlank(String value) {
        return !isTrimEmpty(value);
    }

    public static boolean isNotEmpty(Object[] array) {
        return !isEmpty(array);
    }

    public static boolean isEmpty(Object[] array) {
        return array == null || array.length < 1;
    }

    public static boolean isNotEmpty(Collection<?> collection) {
        return !isEmpty(collection);
    }

    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    public static boolean isNotEmpty(Map<?, ?> map) {
        return !isEmpty(map);
    }

    public static boolean isEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

    public static int size(Collection<?> collection) {
        return collection != null && !collection.isEmpty() ? collection.size() : 0;
    }

    public static int size(Map<?, ?> map) {
        return map != null && !map.isEmpty() ? map.size() : 0;
    }

    public static String defaultString(String value, String defaultValue) {
        return isEmpty(value) ? defaultValue : value;
    }

    public static Object defaultValue(Object value, Object defaultValue) {
        return value == null ? defaultValue : value;
    }

    public static String defaultStringValue(Object value, String defaultValue) {
        return value == null ? defaultValue : value.toString();
    }
}
