package com.freesia.validation.pojo;

import com.freesia.validation.strategy.MaxValidator;

/**
 * @author Evad.Wu
 * @Description {@link MaxValidator} 校验必要参数
 * @date 2024-04-26
 */
public class MaxValidPojo extends BaseValidPojo {
    public MaxValidPojo() {
        super();
    }

    public MaxValidPojo(String messageCode, Class<?> dataType, String desc, String property, Object value) {
        super(messageCode, dataType, desc, property, value);
    }
}
