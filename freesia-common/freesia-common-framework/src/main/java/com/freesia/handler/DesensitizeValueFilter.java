package com.freesia.handler;

import com.alibaba.fastjson.serializer.ValueFilter;
import com.freesia.annotation.Desensitize;
import com.freesia.constant.DesensitizedType;
import com.freesia.util.UDesensitized;

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
            DesensitizedType strategy = Desensitize.strategy();
            switch (strategy) {
                case CHINESE_NAME:
                    return UDesensitized.chineseName(valueStr);
                case EURO_AMERICAN_NAME:
                    return UDesensitized.euroAmericanName(valueStr);
                case ID_CARD:
                    return UDesensitized.idCardNum(valueStr, 1, 2);
                case FIXED_PHONE:
                    return UDesensitized.fixedPhone(valueStr);
                case MOBILE_PHONE:
                    return UDesensitized.mobilePhone(valueStr);
                case ADDRESS:
                    return UDesensitized.address(valueStr, 8);
                case EMAIL:
                    return UDesensitized.email(valueStr);
                case PASSWORD:
                    return UDesensitized.password(valueStr);
                case CAR_LICENSE:
                    return UDesensitized.carLicense(valueStr);
                case BANK_CARD:
                    return UDesensitized.bankCard(valueStr);
                case NONE:
                    return value;
                default:
            }
        } catch (NoSuchFieldException e) {
            return value;
        }
        return value;
    }
}
