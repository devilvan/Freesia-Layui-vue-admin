package com.freesia.oss.filter;

import com.alibaba.fastjson.serializer.ValueFilter;
import com.freesia.oss.annotation.Domain;
import com.freesia.oss.pojo.OssFactory;
import com.freesia.oss.pojo.OssHandler;
import com.freesia.util.UEmpty;

import java.lang.reflect.Field;

/**
 * @author Bliss.Wu
 * @Description FastJSON序列化时将URL转化为域名模式 值过滤器
 * @date 2025-05-15
 */
public class DomainSeValueFilter implements ValueFilter {
    @Override
    public Object process(Object object, String name, Object value) {
        try {
            Field field = object.getClass().getDeclaredField(name);
            Domain domain = field.getAnnotation(Domain.class);
            if (domain == null) {
                return value;
            }
            String configKey = domain.configKey();
            OssHandler ossHandler;
            if (UEmpty.isNotEmpty(configKey)) {
                ossHandler = OssFactory.getInstance(configKey);
            } else {
                ossHandler = OssFactory.getInstance();
            }
            if (value instanceof String) {
                if (UEmpty.isEmpty(value)) {
                    return value;
                }
                return ossHandler.convertEndpoint2Domain(value.toString());
            }
        } catch (NoSuchFieldException e) {
            return value;
        }
        return value;
    }
}
