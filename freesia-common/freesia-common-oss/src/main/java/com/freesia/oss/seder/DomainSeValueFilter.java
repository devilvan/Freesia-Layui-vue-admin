package com.freesia.oss.seder;

import com.alibaba.fastjson.serializer.ValueFilter;
import com.freesia.oss.annotation.Domain;
import com.freesia.oss.pojo.OssFactory;
import com.freesia.oss.pojo.OssHandler;
import com.freesia.util.UEmpty;
import com.freesia.util.UReflect;

import java.lang.reflect.Field;
import java.util.List;

/**
 * @author Bliss.Wu
 * @Description FastJSON序列化时将URL转化为IP模式 值过滤器
 * @date 2025-05-15
 */
public class DomainSeValueFilter implements ValueFilter {
    @Override
    public Object process(Object object, String name, Object value) {
        Class<?> clz = object.getClass();
        List<Field> fieldList = UReflect.findFieldsWithAnnotation(clz, Domain.class);
        Field field = null;
        if (UEmpty.isNotEmpty(fieldList)) {
            for (Field item : fieldList) {
                if (name.equals(item.getName())) {
                    field = item;
                    break;
                }
            }
        }
        if (UEmpty.isNull(field)) {
            return value;
        }
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
        return value;
    }
}
