package com.freesia.crypt.handler;

import cn.hutool.http.ContentType;
import com.alibaba.fastjson.JSONObject;
import com.freesia.crypt.annotation.Encrypt;
import com.freesia.crypt.constant.CryptModule;
import com.freesia.crypt.util.UCrypt;
import com.freesia.crypt.exception.CryptException;
import com.freesia.util.UEmpty;
import com.freesia.vo.R;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

/**
 * @author Evad.Wu
 * @Description 响应报文加密 web处理类
 * @date 2024-04-07
 */
@Slf4j
@Component
public class EncryptReturnValueHandler implements HandlerMethodReturnValueHandler {
    @Override
    public boolean supportsReturnType(MethodParameter parameter) {
        return (AnnotatedElementUtils.hasAnnotation(parameter.getContainingClass(), ResponseBody.class) ||
                parameter.hasMethodAnnotation(ResponseBody.class)) && parameter.hasMethodAnnotation(Encrypt.class);
    }

    @Override
    public void handleReturnValue(
            Object returnValue,
            @NonNull MethodParameter returnType,
            @NonNull ModelAndViewContainer mavContainer,
            @NonNull NativeWebRequest webRequest) throws IOException {
        // 形式返回则加密data后再返回
        if (returnValue instanceof final R r) {
            Object data = r.getData();
            if (UEmpty.isNotNull(data)) {
                r.setData(encryptBasicType(data));
            }
            doWrite(webRequest, mavContainer, JSONObject.toJSONString(r));
        } else {
            // 基础类型、数组类型统一转String后加密返回
            String encrypt = encryptBasicType(returnValue);
            doWrite(webRequest, mavContainer, JSONObject.toJSONString(encrypt));
        }
    }

    private void doWrite(NativeWebRequest webRequest, ModelAndViewContainer mavContainer, String responseBody) throws IOException {
        if (UEmpty.isNotNull(responseBody)) {
            HttpServletResponse httpServletResponse = Optional.of(webRequest)
                    .map(nativeWebRequest -> nativeWebRequest.getNativeResponse(HttpServletResponse.class))
                    .orElseThrow(() -> new CryptException("crypt.get.native.response.failed", new Object[][] {}));
            httpServletResponse.setCharacterEncoding("UTF-8");
            httpServletResponse.setHeader("Content-type", ContentType.TEXT_HTML.getValue());
            httpServletResponse.getWriter().write(responseBody);
            mavContainer.setRequestHandled(true);
        }
    }

    private String encryptBasicType(Object returnValue) {
        String encrypt;
        if (returnValue instanceof Byte) {
            encrypt = UCrypt.aesEncrypt(Byte.toString((Byte) returnValue));
        } else if (returnValue instanceof Short) {
            encrypt = UCrypt.aesEncrypt(Short.toString((Short) returnValue));
        } else if (returnValue instanceof Integer) {
            encrypt = UCrypt.aesEncrypt(Integer.toString((Integer) returnValue));
        } else if (returnValue instanceof Long) {
            encrypt = UCrypt.aesEncrypt(Long.toString((Long) returnValue));
        } else if (returnValue instanceof Float) {
            encrypt = UCrypt.aesEncrypt(Float.toString((Float) returnValue));
        } else if (returnValue instanceof Double) {
            encrypt = UCrypt.aesEncrypt(Double.toString((Double) returnValue));
        } else if (returnValue instanceof Boolean) {
            encrypt = UCrypt.aesEncrypt(Boolean.toString((Boolean) returnValue));
        } else if (returnValue instanceof Character) {
            encrypt = UCrypt.aesEncrypt(Character.toString((Character) returnValue));
        } else if (returnValue instanceof Object[]) {
            encrypt = Arrays.toString((Object[]) returnValue);
        } else {
            encrypt = UCrypt.aesEncrypt(JSONObject.toJSONString(returnValue));
        }
        return encrypt;
    }
}
