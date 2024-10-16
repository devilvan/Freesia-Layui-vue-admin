package com.freesia.validation.pojo;

import com.freesia.validation.strategy.LengthValidator;

/**
 * @author Evad.Wu
 * @Description {@link LengthValidator} 校验必要参数
 * @date 2024-04-26
 */
public class LengthValidPojo extends BaseValidPojo {

    public LengthValidPojo() {
        super();

    }

    public LengthValidPojo(String messageCode, Class<?> dataType, String desc, String property, Object value) {
        super(messageCode, dataType, desc, property, value);
    }
}
