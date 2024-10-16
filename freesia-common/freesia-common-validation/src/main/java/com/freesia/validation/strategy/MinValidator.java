package com.freesia.validation.strategy;

import com.freesia.util.UMessage;
import com.freesia.validation.util.USpringValidation;
import com.freesia.validation.pojo.MinValidPojo;

import javax.validation.constraints.Min;


/**
 * @author Evad.Wu
 * @Description 校验 {@link Min} 策略类
 * @date 2024-04-26
 */
public class MinValidator implements ValidationStrategy<MinValidPojo> {
    @Override
    public String valid(MinValidPojo pojo) {
        String property = pojo.getProperty();
        Min fieldAnnotation = USpringValidation.getFieldAnnotation(pojo.getDataType(), property, Min.class);
        return UMessage.message("validation.error.msg",
                UMessage.message(pojo.getMessageCode(), fieldAnnotation.value()), property, pojo.getValue());
    }
}
