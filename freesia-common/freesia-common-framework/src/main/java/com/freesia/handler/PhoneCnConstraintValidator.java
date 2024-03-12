package com.freesia.handler;

import com.freesia.annotation.Phone_CN;
import com.freesia.util.UEmpty;
import com.freesia.util.USpringValidation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author Evad.Wu
 * @Description Spring Validation 自定义注解 校验器
 * {@link Phone_CN}
 * @date 2024-03-11
 */
public class PhoneCnConstraintValidator implements ConstraintValidator<Phone_CN, String> {
    private boolean required = false;

    @Override
    public void initialize(Phone_CN constraintAnnotation) {
        required = constraintAnnotation.required();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (required) {
            return USpringValidation.phoneCn(value);
        } else {
            if (UEmpty.isEmpty(value)) {
                return true;
            } else {
                return USpringValidation.phoneCn(value);
            }
        }
    }
}
