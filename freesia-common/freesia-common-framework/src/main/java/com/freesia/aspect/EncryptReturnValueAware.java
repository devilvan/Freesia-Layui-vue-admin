package com.freesia.aspect;

import com.freesia.component.EncryptReturnValueHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Evad.Wu
 * @Description 响应报文加密 处理器重排序
 * @date 2024-04-07
 */
@Component
public class EncryptReturnValueAware implements ApplicationContextAware {
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        RequestMappingHandlerAdapter requestMappingHandlerAdapter = applicationContext.getBean(RequestMappingHandlerAdapter.class);
        EncryptReturnValueHandler encryptReturnValueHandler = applicationContext.getBean(EncryptReturnValueHandler.class);
        List<HandlerMethodReturnValueHandler> returnValueHandlers = new ArrayList<>();
        List<HandlerMethodReturnValueHandler> handlerMethodReturnValueHandlerList = Objects.requireNonNull(requestMappingHandlerAdapter.getReturnValueHandlers());
        for (HandlerMethodReturnValueHandler handlerMethodReturnValueHandler : handlerMethodReturnValueHandlerList) {
            if (handlerMethodReturnValueHandler instanceof RequestResponseBodyMethodProcessor) {
                returnValueHandlers.add(encryptReturnValueHandler);
            }
            returnValueHandlers.add(handlerMethodReturnValueHandler);
        }
        requestMappingHandlerAdapter.setReturnValueHandlers(returnValueHandlers);
    }
}
