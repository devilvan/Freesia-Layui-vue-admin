package com.freesia.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

/**
 * @author Evad.Wu
 * @Description 国际化信息 工具类
 * @date 2023-08-12
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UMessage {
    private static final MessageSource MESSAGE_SOURCE = USpring.getBean(MessageSource.class);

    /**
     * 根据消息键和参数 获取消息 委托给spring messageSource
     *
     * @param code 消息键
     * @param args 参数
     * @return 获取国际化翻译值
     */
    public static String message(String code, Object... args) {
        return MESSAGE_SOURCE.getMessage(replaceCodeFirstAndEnd(code), args, LocaleContextHolder.getLocale());
    }

    private static String replaceCodeFirstAndEnd(String code) {
        return code.replace("{", "").replace("}", "");
    }


}
