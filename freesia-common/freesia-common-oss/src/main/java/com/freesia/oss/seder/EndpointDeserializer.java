package com.freesia.oss.seder;


import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import com.freesia.oss.annotation.Endpoint;
import com.freesia.oss.pojo.OssFactory;
import com.freesia.oss.pojo.OssHandler;
import com.freesia.util.UEmpty;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

/**
 * @author Bliss.Wu
 * @Description FastJSON反序列化时将URL转化为域名模式 反序列化器
 * @date 2025-05-15
 */
public class EndpointDeserializer implements ObjectDeserializer {
    @Override
    public <T> T deserialze(DefaultJSONParser parser, Type type, Object fieldName) {
        // 获取原始值
        Object value = parser.parse();

        // 如果fieldName不为空，说明这是某个对象的字段
        if (fieldName instanceof String) {
            try {
                // 获取当前解析的对象的类
                Class<?> clazz = parser.getConfig().getClass();
                // 获取字段
                Field field = clazz.getDeclaredField(fieldName.toString());
                // 检查是否有@Endpoint注解
                if (field.isAnnotationPresent(Endpoint.class)) {
                    Endpoint endpoint = field.getAnnotation(Endpoint.class);
                    // 处理带有@Endpoint注解的字段
                    value = processEndpointValue(value, endpoint);
                }
            } catch (NoSuchFieldException e) {
                // 字段不存在，忽略
            }
        }

        return (T) value;
    }

    @Override
    public int getFastMatchToken() {
        return 0;
    }

    private Object processEndpointValue(Object value, Endpoint endpoint) {
        String configKey = endpoint.configKey();
        OssHandler ossHandler;
        if (UEmpty.isNotEmpty(configKey)) {
            ossHandler = OssFactory.getInstance(configKey);
        } else {
            ossHandler = OssFactory.getInstance();
        }
        // 实现你的自定义处理逻辑
        if (value instanceof String) {
            return ((String) value).toUpperCase(); // 示例：转换为大写
        }
        return value;
    }
}