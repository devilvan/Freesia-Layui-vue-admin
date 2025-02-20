package com.freesia.redis.util;

import cn.hutool.core.convert.Convert;
import com.freesia.util.USpring;
import lombok.NonNull;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author Evad.Wu
 * @Description Redis操作 工具类
 * @date 2023-08-12
 */
@SuppressWarnings({"unchecked", "unused"})
public class URedis {
    private static final RedisTemplate<String, Object> REDIS_TEMPLATE = USpring.getBean("freesiaRedisTemplate");

    /**
     * 发布消息
     *
     * @param topic   主题
     * @param message 消息
     */
    public static void send(String topic, String message) {
        REDIS_TEMPLATE.convertAndSend(topic, message);
    }

    /**
     * 发布消息（批量主题）
     *
     * @param topicList 主题
     * @param message   消息
     */
    public static void send(List<String> topicList, String message) {
        for (String topic : topicList) {
            send(topic, message);
        }
    }

    /**
     * string 互斥set操作
     *
     * @param key   键
     * @param value 值
     * @param <T>   值类型
     */
    public static <T> boolean setNx(String key, T value) {
        return Convert.toBool(REDIS_TEMPLATE.opsForValue().setIfAbsent(key, value), false);
    }

    /**
     * string 互斥set操作
     *
     * @param key      键
     * @param value    值
     * @param duration 持续时间
     * @param <T>      值类型
     */
    public static <T> boolean setNx(String key, T value, Duration duration) {
        return Convert.toBool(REDIS_TEMPLATE.opsForValue().setIfAbsent(key, value, duration), false);
    }

    /**
     * string 互斥set操作
     *
     * @param key     键
     * @param value   值
     * @param timeout 持续时间
     * @param unit    持续时间单位
     * @param <T>     值类型
     */
    public static <T> boolean setNx(String key, T value, long timeout, TimeUnit unit) {
        return Convert.toBool(REDIS_TEMPLATE.opsForValue().setIfAbsent(key, value, timeout, unit), false);
    }

    /**
     * 获取存活时间（秒）
     *
     * @param key 键
     * @return 存活时间
     */
    public static long getTimeToLive(String key) {
        return Convert.toLong(REDIS_TEMPLATE.getExpire(key));
    }

    /**
     * 修改存活时间
     *
     * @param key      键
     * @param duration 修改后持续时间
     */
    public static void expire(String key, Duration duration) {
        REDIS_TEMPLATE.expire(key, duration);
    }

    /**
     * 查询k-v对
     *
     * @param keyword 关键字
     * @return 结果集
     */
    public static Collection<String> keys(String keyword) {
        return REDIS_TEMPLATE.keys(keyword);
    }

    /**
     * Set 出队
     *
     * @param key Set键
     * @param <T> 元素类型
     * @return 出队的元素
     */
    public static <T> T setPop(final String key) {
        return (T) REDIS_TEMPLATE.opsForSet().pop(key);
    }

    /**
     * Set 出队
     *
     * @param key  Set键
     * @param size 出队元素数量
     * @param <T>  元素类型
     * @return 出队的元素集合
     */
    public static <T> List<T> setPop(final String key, Integer size) {
        return (List<T>) REDIS_TEMPLATE.opsForSet().pop(key, size);
    }

    /**
     * Set 添加元素
     *
     * @param key    Set键
     * @param values 元素
     * @param <T>    元素类型
     */
    public static <T> void addAll(String key, T[] values) {
        REDIS_TEMPLATE.opsForSet().add(key, values);
    }

    /**
     * Set 随机获取元素
     *
     * @param key Set键
     * @param <T> 元素类型
     * @return 元素
     */
    public static <T> T randomMember(String key) {
        return (T) REDIS_TEMPLATE.opsForSet().randomMember(key);
    }

    /**
     * Hash 判断键值对是否存在
     *
     * @param name Hash键
     * @param key  map键
     */
    public static void hashExist(String name, Object key) {
        REDIS_TEMPLATE.opsForHash().hasKey(name, key);
    }

    /**
     * Hash 删除键值对
     *
     * @param name Hash键
     * @param keys map键
     */
    public static void hashDelete(String name, Object... keys) {
        REDIS_TEMPLATE.opsForHash().delete(name, keys);
    }

    /**
     * Hash 获取键值对
     *
     * @param name Hash键
     * @param key  map键
     * @return 值
     */
    public static Object hashGet(String name, String key) {
        return REDIS_TEMPLATE.opsForHash().get(name, key);
    }

    /**
     * Hash 添加键值对
     *
     * @param hashKey Hash键
     * @param key     map键
     * @param value   值
     */
    public static void put(String hashKey, String key, Object value) {
        REDIS_TEMPLATE.opsForHash().put(hashKey, key, value);
    }

    /**
     * Hash 添加键值对
     *
     * @param key Hash键
     * @param map 键值对
     */
    public static void putAll(@NonNull String key, @NonNull Map<String, String> map) {
        REDIS_TEMPLATE.opsForHash().putAll(key, map);
    }

    /**
     * Hash 根据key获取map
     *
     * @param key Hash的键
     * @return map
     */
    public static Map<Object, Object> entries(@NonNull String key) {
        return REDIS_TEMPLATE.opsForHash().entries(key);
    }

    /**
     * List 头出队
     *
     * @param key List对应的键
     * @param <T> 出列元素的类型
     * @return 出列的元素
     */
    public static <T> T leftPop(@NonNull String key) {
        return (T) REDIS_TEMPLATE.opsForList().leftPop(key);
    }

    /**
     * List 头入队
     *
     * @param key   List对应的键
     * @param value 入队的元素
     */
    public static void leftPush(@NonNull String key, @NonNull String value) {
        REDIS_TEMPLATE.opsForList().leftPush(key, value);
    }

    /**
     * List 尾入队
     *
     * @param key    List对应的键
     * @param values 入队的元素
     */
    public static void rightPushAll(@NonNull String key, Object... values) {
        REDIS_TEMPLATE.opsForList().rightPushAll(key, values);
    }

    /**
     * List 尾入队
     *
     * @param key   List对应的键
     * @param value 入队的元素
     */
    public static void rightPush(@NonNull String key, String value) {
        REDIS_TEMPLATE.opsForList().rightPush(key, value);
    }

    /**
     * string get操作
     *
     * @param key 键
     * @param <T> 键类型
     * @return 键对应的value
     */
    public static <T> T get(final String key) {
        return (T) REDIS_TEMPLATE.opsForValue().get(key);
    }

    /**
     * string set操作
     *
     * @param key   键
     * @param value 值
     * @param <T>   值类型
     */
    public static <T> void set(String key, T value) {
        REDIS_TEMPLATE.opsForValue().set(key, value);
    }

    /**
     * string set操作
     *
     * @param key      键
     * @param value    值
     * @param duration 持续时间
     * @param <T>      值类型
     */
    public static <T> void set(String key, T value, Duration duration) {
        REDIS_TEMPLATE.opsForValue().set(key, value, duration);
    }

    /**
     * string set操作
     *
     * @param key     键
     * @param value   值
     * @param timeout 持续时间
     * @param unit    持续时间单位
     * @param <T>     值类型
     */
    public static <T> void set(String key, T value, long timeout, TimeUnit unit) {
        REDIS_TEMPLATE.opsForValue().set(key, value, timeout, unit);
    }

    /**
     * 删除key
     *
     * @param key 待删除的key
     * @return 删除成功/失败
     */
    public static boolean delete(String key) {
        return Convert.toBool(REDIS_TEMPLATE.delete(key), false);
    }

    /**
     * 获取redis模板示例
     *
     * @return redis模板示例
     */
    @SuppressWarnings("unused")
    public RedisTemplate<String, Object> getRedisTemplate() {
        return REDIS_TEMPLATE;
    }
}
