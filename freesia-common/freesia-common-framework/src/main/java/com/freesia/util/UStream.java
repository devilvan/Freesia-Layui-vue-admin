package com.freesia.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author Evad.Wu
 * @Description Stream流 工具类
 * @date 2023-09-05
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UStream {

    /**
     * 判断集合中是否存在符合条件的数据
     *
     * @param sourceList 集合
     * @param function   判断条件
     * @param <T>        集合类型
     * @return flag
     */
    public static <T> boolean anyMatch(Collection<T> sourceList, Predicate<T> function) {
        if (CollUtil.isEmpty(sourceList)) {
            return false;
        }
        return sourceList.stream().anyMatch(function);
    }

    /**
     * 将集合根据Key进行分组
     *
     * @param sourceList 集合
     * @param function   分组的key
     * @param <T>        集合类型
     * @param <R>        分组key类型
     * @return 根据Key进行分组的Map
     */
    public static <T, R> Map<R, List<T>> groupingByKey(Collection<T> sourceList, Function<T, R> function) {
        if (CollUtil.isEmpty(sourceList)) {
            return MapUtil.newHashMap();
        }
        return sourceList.stream().filter(ObjectUtil::isNotNull).collect(Collectors.groupingBy(function));
    }

    /**
     * 将collection转化为List集合，但是两者的泛型不同<br>
     * <B>{@code Collection<E>  ------>  List<T> } </B>
     *
     * @param collection 需要转化的集合
     * @param function   collection中的泛型转化为list泛型的lambda表达式
     * @param <E>        collection中的泛型
     * @param <T>        List中的泛型
     * @return 转化后的list
     */
    public static <E, T> List<T> toList(Collection<E> collection, Function<E, T> function) {
        if (CollUtil.isEmpty(collection)) {
            return CollUtil.newArrayList();
        }
        return collection
                .stream()
                .map(function)
                .filter(Objects::nonNull)
                // 注意此处不要使用 .toList() 新语法 因为返回的是不可变List 会导致序列化问题
                .collect(Collectors.toList());
    }

    /**
     * 将Collection转化为map(value类型与collection的泛型不同)<br>
     * <B>{@code Collection<E> -----> Map<K,V>  }</B>
     *
     * @param collection 需要转化的集合
     * @param key        E类型转化为K类型的lambda方法
     * @param value      E类型转化为V类型的lambda方法
     * @param <E>        collection中的泛型
     * @param <K>        map中的key类型
     * @param <V>        map中的value类型
     * @return 转化后的map
     */
    public static <E, K, V> Map<K, V> toMap(Collection<E> collection, Function<E, K> key, Function<E, V> value) {
        if (CollUtil.isEmpty(collection)) {
            return MapUtil.newHashMap();
        }
        return collection.stream().filter(ObjectUtil::isNotNull).collect(Collectors.toMap(key, value));
    }

    /**
     * 将collection拼接
     *
     * @param collection 需要转化的集合
     * @param function   拼接方法
     * @return 拼接后的list
     */
    public static <E> String join(Collection<E> collection, Function<E, String> function) {
        return join(collection, function, UString.SEPARATOR);
    }

    /**
     * 将collection拼接
     *
     * @param collection 需要转化的集合
     * @param function   拼接方法
     * @param delimiter  拼接符
     * @return 拼接后的list
     */
    public static <E> String join(Collection<E> collection, Function<E, String> function, CharSequence delimiter) {
        if (CollUtil.isEmpty(collection)) {
            return StringUtils.EMPTY;
        }
        return collection.stream().map(function).filter(Objects::nonNull).collect(Collectors.joining(delimiter));
    }
}
