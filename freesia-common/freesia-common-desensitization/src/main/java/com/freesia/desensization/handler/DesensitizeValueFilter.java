package com.freesia.desensization.handler;

import com.alibaba.fastjson.serializer.ValueFilter;
import com.freesia.desensization.annotation.Desensitize;
import com.freesia.desensization.constant.DesensitizedType;
import com.freesia.desensization.util.UDesensitized;

import java.lang.reflect.Field;

/**
 * @author Evad.Wu
 * @Description 脱敏数据转换过滤器
 * @date 2023-03-12
 */
public class DesensitizeValueFilter implements ValueFilter {
    @Override
    public Object process(Object object, String name, Object value) {
        try {
            Field field = object.getClass().getDeclaredField(name);
            Desensitize Desensitize = field.getAnnotation(Desensitize.class);
            if (Desensitize == null) {
                return value;
            }
            if (value instanceof Long) {
                return String.valueOf(UDesensitized.userId());
            }
            if (!(value instanceof String valueStr) || ((String) value).length() == 0) {
                return value;
            }
            DesensitizedType[] strategyArr = Desensitize.strategy();
            value = UDesensitized.desensitized(valueStr, strategyArr);
        } catch (NoSuchFieldException e) {
            return value;
        }
        return value;
    }
}
