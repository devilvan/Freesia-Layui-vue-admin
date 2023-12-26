package com.freesia.handler;

import cn.hutool.core.util.StrUtil;
import com.freesia.constant.DesensitizedType;
import com.freesia.util.UDesensitized;
import org.springframework.format.Formatter;

import java.util.Locale;

/**
 * @author Evad.Wu
 * @Description 创建格式化类实现Formatter接口
 * @date 2023-03-12
 */
public class DesensitizationFormatter implements Formatter<String> {
    private DesensitizedType desensitizedType;

    public void setDesensitizedType(DesensitizedType desensitizedType) {
        this.desensitizedType = desensitizedType;
    }

    @Override
    public String parse(String valueStr, Locale locale) {
        if (StrUtil.isNotBlank(valueStr)) {
            switch (desensitizedType) {
                case USER_ID:
                    valueStr = String.valueOf(UDesensitized.userId());
                    break;
                case CHINESE_NAME:
                    valueStr = UDesensitized.chineseName(valueStr);
                    break;
                case ID_CARD:
                    valueStr = UDesensitized.idCardNum(valueStr, 1, 2);
                    break;
                case FIXED_PHONE:
                    valueStr = UDesensitized.fixedPhone(valueStr);
                    break;
                case MOBILE_PHONE:
                    valueStr = UDesensitized.mobilePhone(valueStr);
                    break;
                case ADDRESS:
                    valueStr = UDesensitized.address(valueStr, 8);
                    break;
                case EMAIL:
                    valueStr = UDesensitized.email(valueStr);
                    break;
                case PASSWORD:
                    valueStr = UDesensitized.password(valueStr);
                    break;
                case CAR_LICENSE:
                    valueStr = UDesensitized.carLicense(valueStr);
                    break;
                case BANK_CARD:
                    valueStr = UDesensitized.bankCard(valueStr);
                    break;
                case NONE:
                    break;
                default:
            }
        }
        return valueStr;
    }

    @Override
    public String print(String s, Locale locale) {
        return s;
    }
}
