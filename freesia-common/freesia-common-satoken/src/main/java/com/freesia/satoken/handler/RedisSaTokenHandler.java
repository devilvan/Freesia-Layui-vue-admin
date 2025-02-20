package com.freesia.satoken.handler;

import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.util.SaFoxUtil;
import com.freesia.redis.util.URedis;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Evad.Wu
 * @Description Sa-Token持久层接口(通过Redis实现)
 * @date 2023-08-29
 */
@Slf4j
public class RedisSaTokenHandler implements SaTokenDao {
    /**
     * 获取value
     *
     * @param key 键
     * @return value
     */
    @Override
    public String get(String key) {
        return URedis.get(key);
    }

    /**
     * 设置key-value
     *
     * @param key     键
     * @param value   值
     * @param timeout 存活时间（秒）
     */
    @Override
    public void set(String key, String value, long timeout) {
        if (timeout == 0 || timeout <= SaTokenDao.NOT_VALUE_EXPIRE) {
            return;
        }
        // 判断是否为永不过期
        if (timeout == SaTokenDao.NEVER_EXPIRE) {
            URedis.set(key, value);
        } else {
            URedis.set(key, value, Duration.ofSeconds(timeout));
        }
    }

    /**
     * 更新key
     *
     * @param key   键
     * @param value 修改后的值
     */
    @Override
    public void update(String key, String value) {
        long expire = getTimeout(key);
        // -2 = 无此键
        if (expire == SaTokenDao.NOT_VALUE_EXPIRE) {
            return;
        }
        this.set(key, value, expire);
    }

    /**
     * 删除Key
     *
     * @param key 键
     */
    @Override
    public void delete(String key) {
        URedis.delete(key);
    }

    /**
     * 获取存活时间（秒）
     *
     * @param key 键
     * @return 存活时间
     */
    @Override
    public long getTimeout(String key) {
        return URedis.getTimeToLive(key);
    }

    /**
     * 修改存活时间（秒）
     *
     * @param key     键
     * @param timeout 修改后的存活时间
     */
    @Override
    public void updateTimeout(String key, long timeout) {
        // 判断是否想要设置为永久
        if (timeout == SaTokenDao.NEVER_EXPIRE) {
            long expire = getTimeout(key);
            // 如果尚未被设置为永久，那么再次set一次
            if (expire != SaTokenDao.NEVER_EXPIRE) {
                this.set(key, this.get(key), timeout);
            }
            return;
        }
        URedis.expire(key, Duration.ofSeconds(timeout));
    }

    /**
     * 获取value对象
     *
     * @param key 键
     * @return value对象
     */
    @Override
    public Object getObject(String key) {
        return URedis.get(key);
    }

    /**
     * 设置key-value
     *
     * @param key     键
     * @param value   值
     * @param timeout 存活时间（秒）
     */
    @Override
    public void setObject(String key, Object value, long timeout) {
        if (timeout == 0 || timeout <= SaTokenDao.NOT_VALUE_EXPIRE) {
            return;
        }
        // 判断是否为永不过期
        if (timeout == SaTokenDao.NEVER_EXPIRE) {
            URedis.set(key, value);
        } else {
            URedis.set(key, value, Duration.ofSeconds(timeout));
        }
    }

    /**
     * 更新key
     *
     * @param key   键
     * @param value 修改后的值
     */
    @Override
    public void updateObject(String key, Object value) {
        long expire = getTimeout(key);
        // -2 = 无此键
        if (expire == SaTokenDao.NOT_VALUE_EXPIRE) {
            return;
        }
        this.setObject(key, value, expire);
    }

    /**
     * 删除Key
     *
     * @param key 键
     */
    @Override
    public void deleteObject(String key) {
        URedis.delete(key);
    }

    /**
     * 获取存活时间（秒）
     *
     * @param key 键
     * @return 存活时间
     */
    @Override
    public long getObjectTimeout(String key) {
        return URedis.getTimeToLive(key);
    }

    /**
     * 修改存活时间（秒）
     *
     * @param key     键
     * @param timeout 修改后的存活时间
     */
    @Override
    public void updateObjectTimeout(String key, long timeout) {
        // 判断是否想要设置为永久
        if (timeout == SaTokenDao.NEVER_EXPIRE) {
            long expire = getTimeout(key);
            // 如果尚未被设置为永久，那么再次set一次
            if (expire != SaTokenDao.NEVER_EXPIRE) {
                this.setObject(key, this.getObject(key), timeout);
            }
            return;
        }
        URedis.expire(key, Duration.ofSeconds(timeout));
    }

    @Override
    public List<String> searchData(String prefix, String keyword, int start, int size, boolean sortType) {
        Collection<String> keys = URedis.keys(prefix + "*" + keyword + "*");
        List<String> list = new ArrayList<>(keys);
        return SaFoxUtil.searchList(list, start, size, sortType);
    }
}
