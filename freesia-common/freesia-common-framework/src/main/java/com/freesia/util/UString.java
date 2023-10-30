package com.freesia.util;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.StrUtil;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Evad.Wu
 * @Description 字符串相关 工具类
 * @date 2022-09-25
 */
@SuppressWarnings(value = "unused")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UString extends StrUtil {
    public static final String SEPARATOR = ",";

    /**
     * 切分字符串(分隔符默认逗号)
     *
     * @param str 被切分的字符串
     * @return 分割后的数据列表
     */
    public static List<String> splitList(String str) {
        return splitTo(str, Convert::toStr);
    }

    /**
     * 切分字符串
     *
     * @param str       被切分的字符串
     * @param separator 分隔符
     * @return 分割后的数据列表
     */
    public static List<String> splitList(String str, String separator) {
        return splitTo(str, separator, Convert::toStr);
    }

    /**
     * 切分字符串自定义转换(分隔符默认逗号)
     *
     * @param str    被切分的字符串
     * @param mapper 自定义转换
     * @return 分割后的数据列表
     */
    public static <T> List<T> splitTo(String str, Function<? super Object, T> mapper) {
        return splitTo(str, SEPARATOR, mapper);
    }

    /**
     * 切分字符串自定义转换
     *
     * @param str       被切分的字符串
     * @param separator 分隔符
     * @param mapper    自定义转换
     * @return 分割后的数据列表
     */
    public static <T> List<T> splitTo(String str, String separator, Function<? super Object, T> mapper) {
        if (isBlank(str)) {
            return new ArrayList<>(0);
        }
        return StrUtil.split(str, separator)
                .stream()
                .filter(Objects::nonNull)
                .map(mapper)
                .collect(Collectors.toList());
    }

    /**
     * 将字符串首字母大写
     *
     * @param str 待转化的字符串
     * @return 首字母大写后的字符串
     */
    public static String upperCaseFirst(String str) {
        if (UEmpty.isNotEmpty(str)) {
            String tmp = str.substring(0, 1).toUpperCase(Locale.ROOT);
            return tmp + str.substring(1);
        }
        return str;
    }

    /**
     * URLEncode格式字符串转UTR-8字符串
     *
     * @param urlEncodeStr 待转换的字符串
     * @return 转换后的字符串
     */
    public static String decode(String urlEncodeStr) {
        return URLDecoder.decode(urlEncodeStr, StandardCharsets.UTF_8);
    }

    /**
     * 集合转字符串
     *
     * @param list     集合
     * @param separate 分隔符
     * @return 拼接后的字符串
     */
    public static String join(List<String> list, String separate) {
        if (UEmpty.isEmpty(list)) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (String s : list) {
            sb.append(s).append(separate);
        }
        String string = sb.toString();
        return string.substring(0, string.length() - 1);
    }

    /**
     * 数组转字符串
     *
     * @param arr      数组
     * @param separate 分隔符
     * @return 拼接后的字符串
     */
    public static String join(String[] arr, String separate) {
        if (UEmpty.isEmpty(arr)) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (String s : arr) {
            sb.append(s).append(separate);
        }
        String string = sb.toString();
        return string.substring(0, string.length() - 1);
    }

    /**
     * 是否为http(s)://开头
     *
     * @param link 链接
     * @return flag
     */
    public static boolean isHttp(String link) {
        return Validator.isUrl(link);
    }
}
