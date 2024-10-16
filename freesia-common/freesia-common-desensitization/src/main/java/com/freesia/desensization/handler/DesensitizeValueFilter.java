package com.freesia.desensization.handler;

import com.alibaba.fastjson.serializer.ValueFilter;
import com.freesia.desensization.annotation.Desensitize;
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
            DesensitizedType[] strategyArr = Desensitize.strategy();
            for (DesensitizedType strategy : strategyArr) {
                switch (strategy) {
                    case CHINESE_NAME -> valueStr = UDesensitized.chineseName(valueStr);
                    case EURO_AMERICAN_NAME -> valueStr = UDesensitized.euroAmericanName(valueStr);
                    case ID_CARD -> valueStr = UDesensitized.idCardNum(valueStr, 1, 2);
                    case FIXED_PHONE -> valueStr = UDesensitized.fixedPhone(valueStr);
                    case MOBILE_PHONE -> valueStr = UDesensitized.mobilePhone(valueStr);
                    case ADDRESS -> valueStr = UDesensitized.address(valueStr, 8);
                    case EMAIL -> valueStr = UDesensitized.email(valueStr);
                    case PASSWORD -> valueStr = UDesensitized.password(valueStr);
                    case CAR_LICENSE -> valueStr = UDesensitized.carLicense(valueStr);
                    case BANK_CARD -> valueStr = UDesensitized.bankCard(valueStr);
                    default -> {
                        return value;
                    }
                }
                // 如果已经经过某个脱敏规则处理被修改，则不再处理之后的规则
                if (!((String) value).equalsIgnoreCase(valueStr)) {
                    return valueStr;
                }
            }
            value = valueStr;
        } catch (NoSuchFieldException e) {
            return value;
        }
        return value;
    }
}
