package com.freesia.oss.factory;

import com.alibaba.fastjson.JSONObject;
import com.freesia.oss.constant.OssConstant;
import com.freesia.oss.exception.OssException;
import com.freesia.oss.handler.OssHandler;
import com.freesia.oss.properties.OssProperties;
import com.freesia.util.UEmpty;
import com.freesia.util.URedis;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Evad.Wu
 * @Description 对象存储 工厂类
 * @date 2024-02-27
 */
@Slf4j
public class OssFactory {
    private static final Map<String, OssHandler> CLIENT_CACHE = new ConcurrentHashMap<>();

    /**
     * 获取对象存储处理器实例
     *
     * @return 对象存储处理器实例
     */
    public static OssHandler getInstance() {
        // 获取redis 默认类型
        String configKey = URedis.get(OssConstant.SYS_OSS_DEFAULT_CONFIG);
        if (UEmpty.isEmpty(configKey)) {
            throw new OssException("文件存储服务类型无法找到!");
        }
        return getInstance(configKey);
    }

    /**
     * 获取对象存储处理器实例
     *
     * @param configKey 配置key
     * @return 对象存储处理器实例
     */
    public static synchronized OssHandler getInstance(String configKey) {
        String json = (String) URedis.hashGet(OssConstant.SYS_OSS_CONFIG, configKey);
        if (json == null) {
            throw new OssException("系统异常, '" + configKey + "'配置信息不存在!");
        }
        OssProperties properties = JSONObject.parseObject(json, OssProperties.class);
        OssHandler ossHandler = CLIENT_CACHE.get(configKey);
        if (ossHandler == null) {
            CLIENT_CACHE.put(configKey, new OssHandler(configKey, properties));
            log.info("创建OSS实例 key => {}", configKey);
            return CLIENT_CACHE.get(configKey);
        }
        // 配置不相同则重新构建
        if (!ossHandler.checkPropertiesSame(properties)) {
            CLIENT_CACHE.put(configKey, new OssHandler(configKey, properties));
            log.info("重载OSS实例 key => {}", configKey);
            return CLIENT_CACHE.get(configKey);
        }
        return ossHandler;
    }
}
