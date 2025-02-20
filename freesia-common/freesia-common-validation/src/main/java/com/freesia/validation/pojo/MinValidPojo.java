package com.freesia.validation.pojo;

import com.freesia.validation.validator.MinValidator;

/**
 * @author Evad.Wu
 * @Description {@link MinValidator} 校验必要参数
 * @date 2024-04-26
 */
public class MinValidPojo extends BaseValidPojo {
    public MinValidPojo() {
        super();
    }

    public MinValidPojo(String messageCode, Class<?> dataType, String desc, String property, Object value) {
        super(messageCode, dataType, desc, property, value);
    }
}
