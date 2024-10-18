package com.freesia.validation.validator;

import com.freesia.util.UMessage;
import com.freesia.validation.strategy.ValidationStrategy;
import com.freesia.validation.util.USpringValidation;
import com.freesia.validation.pojo.LengthValidPojo;
import org.hibernate.validator.constraints.Length;


/**
 * @author Evad.Wu
 * @Description 校验 {@link Length} 策略类
 * @date 2024-04-26
 */
public class LengthValidator implements ValidationStrategy<LengthValidPojo> {
    @Override
    public String valid(LengthValidPojo pojo) {
        String property = pojo.getProperty();
        Length fieldAnnotation = USpringValidation.getFieldAnnotation(pojo.getDataType(), property, Length.class);
        int max = fieldAnnotation.max();
        int min = fieldAnnotation.min();
        return UMessage.message("validation.error.msg",
                UMessage.message(pojo.getMessageCode(), min, max), property, pojo.getValue());
    }
}
