package com.freesia.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.Map;
import java.util.Set;

/**
 * @author Evad.Wu
 * @Description Spirng缓存管理 工具类
 * @date 2023-09-21
 */
@Deprecated
@SuppressWarnings({"unchecked", "unused"})
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UCache {
    private static final CacheManager CACHE_MANAGER = USpring.getBean(CacheManager.class);

    private static Cache getCache(String cacheNames) {
        return CACHE_MANAGER.getCache(cacheNames);
    }

    /**
     * 获取缓存组内所有的KEY
     *
     * @param cacheNames 缓存组名称
     */

    public static Set<Object> keys(String cacheNames) {
        Map<Object, Object> map = (Map<Object, Object>) getCache(cacheNames).getNativeCache();
        return map.keySet();
    }

    /**
     * 获取缓存值
     *
     * @param cacheNames 缓存组名称
     * @param key        缓存key
     */
    public static <T> T get(String cacheNames, Object key) {
        Cache.ValueWrapper wrapper = getCache(cacheNames).get(key);
        return wrapper != null ? (T) wrapper.get() : null;
    }

    /**
     * 保存缓存值
     *
     * @param cacheNames 缓存组名称
     * @param key        缓存key
     * @param value      缓存值
     */
    public static void put(String cacheNames, Object key, Object value) {
        getCache(cacheNames).put(key, value);
    }

    /**
     * 删除缓存值
     *
     * @param cacheNames 缓存组名称
     * @param key        缓存key
     */
    public static void evict(String cacheNames, Object key) {
        getCache(cacheNames).evict(key);
    }

    /**
     * 清空缓存值
     *
     * @param cacheNames 缓存组名称
     */
    public static void clear(String cacheNames) {
        getCache(cacheNames).clear();
    }
}
