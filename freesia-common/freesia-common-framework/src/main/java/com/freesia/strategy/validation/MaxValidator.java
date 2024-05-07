package com.freesia.strategy.validation;

import com.freesia.strategy.ValidationStrategy;
import com.freesia.strategy.pojo.MaxValidPojo;
import com.freesia.util.UMessage;
import com.freesia.util.USpringValidation;

import javax.validation.constraints.Max;


/**
 * @author Evad.Wu
 * @Description 校验 {@link javax.validation.constraints.Max} 策略类
 * @date 2024-04-26
 */
public class MaxValidator implements ValidationStrategy<MaxValidPojo> {
    @Override
    public String valid(MaxValidPojo pojo) {
        String property = pojo.getProperty();
        Max fieldAnnotation = USpringValidation.getFieldAnnotation(pojo.getDataType(), property, Max.class);
        return UMessage.message("validation.error.msg",
                UMessage.message(pojo.getMessageCode(), fieldAnnotation.value()), property, pojo.getValue());
    }
}
