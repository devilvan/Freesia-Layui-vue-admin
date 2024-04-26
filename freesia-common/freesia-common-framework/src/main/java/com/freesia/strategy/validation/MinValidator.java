package com.freesia.strategy.validation;

import com.freesia.strategy.ValidationStrategy;
import com.freesia.strategy.pojo.LengthValidPojo;
import com.freesia.util.UMessage;
import com.freesia.util.USpringValidation;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Min;


/**
 * @author Evad.Wu
 * @Description 校验 {@link Length} 策略类
 * @date 2024-04-26
 */
public class MinValidator implements ValidationStrategy<LengthValidPojo> {
    @Override
    public String valid(LengthValidPojo pojo) {
        String field = pojo.getField();
        Min fieldAnnotation = USpringValidation.getFieldAnnotation(pojo.getDataType(), field, Min.class);
        return UMessage.message("validation.error.msg",
                UMessage.message(pojo.getMessageCode(), fieldAnnotation.value()), field, pojo.getValue());
    }
}
