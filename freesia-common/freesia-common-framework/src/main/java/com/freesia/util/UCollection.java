package com.freesia.util;

import cn.hutool.core.collection.CollUtil;
import com.freesia.exception.BaseException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.*;

/**
 * @author Evad.Wu
 * @Description 集合/数组 工具类
 * @date 2022-10-01
 */
@SuppressWarnings(value = "unused")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UCollection extends CollUtil {
    public static final int MAXIMUM_CAPACITY = 1 << 30;
    public static final float LOAD_FACTOR = 0.75f;
    public static final int LIST_INIT_CAPACITY = 10;
    public static final int LIST_MAX_LENGTH = Integer.MAX_VALUE - 8;

    public static <T> List<T> optimizeInitialCapacityArrayList(int expectSize) {
        if (expectSize < 0) {
            return new ArrayList<>(LIST_INIT_CAPACITY);
        } else if (expectSize > LIST_MAX_LENGTH) {
            return new ArrayList<>(LIST_MAX_LENGTH);
        } else {
            return new ArrayList<>(arrayListRightSize(expectSize));
        }
    }

    /**
     * 计算期望使用的List容量需要多大的预备容量
     *
     * @param num 期望使用的容量
     * @return List的默认初始化容量
     */
    public static int arrayListRightSize(int num) {
        if (num > 0) {
            //向上取整
            return (int) Math.ceil(1.5 * num);
        } else {
            return LIST_INIT_CAPACITY;
        }
    }

    public static <T> Set<T> optimizeInitialCapacitySet(int expectSize) {
        return optimizeInitialCapacitySet(expectSize, LOAD_FACTOR);
    }

    /**
     * 根据期望使用set的size，计算出合适的初始化size，生成set返回
     *
     * @param expectSize 期望的size
     * @param loadFactor 负载因子（默认0.75）
     * @return 初始化容量的set
     */
    public static <T> Set<T> optimizeInitialCapacitySet(int expectSize, float loadFactor) {
        if (expectSize < 0) {
            return new HashSet<>(1);
        } else if (expectSize > MAXIMUM_CAPACITY) {
            return new HashSet<>(MAXIMUM_CAPACITY);
        } else {
            if (loadFactor <= 0 || loadFactor >= 1) {
                return new HashSet<>((int) (expectSize / LOAD_FACTOR) + 1);
            } else {
                return new HashSet<>((int) (expectSize / loadFactor) + 1);
            }
        }
    }

    public static <T> Map<String, T> optimizeInitialCapacityMap(int expectSize) {
        return optimizeInitialCapacityMap(expectSize, LOAD_FACTOR);
    }

    /**
     * 根据期望使用map的size，计算出合适的初始化size，生成map返回
     *
     * @param expectSize 期望的size
     * @param loadFactor 负载因子（默认0.75）
     * @return 初始化容量的map
     */
    public static <T> Map<String, T> optimizeInitialCapacityMap(int expectSize, float loadFactor) {
        if (expectSize < 0) {
            return new HashMap<>(1);
        } else if (expectSize > MAXIMUM_CAPACITY) {
            return new HashMap<>(MAXIMUM_CAPACITY);
        } else {
            if (loadFactor <= 0 || loadFactor >= 1) {
                return new HashMap<>((int) (expectSize / LOAD_FACTOR) + 1);
            } else {
                return new HashMap<>((int) (expectSize / loadFactor) + 1);
            }
        }
    }

    /**
     * 获取map中的子串，并对子key判空
     *
     * @param map  父map
     * @param keys 子map的key数组
     * @param <T>  value类型
     * @return 子map
     */
    public static <T> Map<String, T> checkSubMap(Map<String, T> map, String... keys) {
        Map<String, T> subMap = new HashMap<>(16);
        for (String key : keys) {
            if (map.containsKey(key)) {
                subMap.put(key, map.get(key));
            } else {
                throw new BaseException("子map字段：" + key + "不能为空");
            }
        }
        return subMap;
    }

    /**
     * 获取map中的子串
     *
     * @param map  父map
     * @param keys 子map的key数组
     * @param <T>  value类型
     * @return 子map
     */
    public static <T> Map<String, T> subMap(Map<String, T> map, String... keys) {
        Map<String, T> subMap = new HashMap<>(16);
        for (String key : keys) {
            if (map.containsKey(key)) {
                subMap.put(key, map.get(key));
            }
        }
        return subMap;
    }

    /**
     * 判断List中某个位置是否越界
     *
     * @param list  集合
     * @param index 下标
     * @param <T>   集合的类型
     * @return 返回对应位置的值
     */
    public static <T> T isListOutOfIndex(List<T> list, int index) {
        int size = list.size();
        if (index < size) {
            return list.get(index);
        }
        return null;
    }

    /**
     * 判断数组中某个位置是否越界
     *
     * @param arr   集合
     * @param index 下标
     * @param <T>   数组的类型
     * @return 返回对应位置的值
     */
    public static <T> T isArrayOutOfIndex(T[] arr, int index) {
        int length = arr.length;
        if (index < length) {
            return arr[index];
        }
        return null;
    }
}
