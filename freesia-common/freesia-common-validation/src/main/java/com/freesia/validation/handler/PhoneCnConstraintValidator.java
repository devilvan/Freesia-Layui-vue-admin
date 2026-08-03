package com.freesia.validation.handler;

import com.freesia.validation.annotation.Phone_CN;
import com.freesia.util.UEmpty;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Evad.Wu
 * @Description Spring Validation 自定义注解 校验器
 * {@link Phone_CN}
 * @date 2024-03-11
 */
public class PhoneCnConstraintValidator implements ConstraintValidator<Phone_CN, String> {
    public static final Pattern PHONE_CN_PATTERN = Pattern.compile("^((13[0-9])|(14[5,7])|(15[0-3,5-9])|(17[0,3,5-8])|(18[0-9])|166|198|199|(147))\\d{8}$");
    private boolean required = false;

    @Override
    public void initialize(Phone_CN constraintAnnotation) {
        required = constraintAnnotation.required();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (required) {
            return phoneCn(value);
        } else {
            if (UEmpty.isEmpty(value)) {
                return true;
            } else {
                return phoneCn(value);
            }
        }
    }

    /**
     * 校验中国大陆手机号码
     *
     * @param value 手机号码
     * @return 校验结果
     */
    private static boolean phoneCn(String value) {
        if (UEmpty.isEmpty(value)) {
            return false;
        }
        Matcher matcher = PHONE_CN_PATTERN.matcher(value);
        return matcher.matches();
    }
}
