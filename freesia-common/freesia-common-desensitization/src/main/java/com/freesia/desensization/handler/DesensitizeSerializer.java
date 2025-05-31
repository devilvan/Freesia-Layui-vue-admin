package com.freesia.desensization.handler;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.freesia.desensization.annotation.Desensitize;
import com.freesia.desensization.constant.DesensitizedType;
import com.freesia.desensization.util.UDesensitized;

import java.io.IOException;

/**
 * @author Evad.Wu
 * @Description Jackson脱敏数据转换过滤器
 * @date 2025-05-31
 */
public class DesensitizeSerializer extends JsonSerializer<Object> implements ContextualSerializer {
    private Desensitize desensitize;

    @Override
    public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value instanceof Long) {
            gen.writeString(String.valueOf(UDesensitized.userId()));
            return;
        }

        if (!(value instanceof String valueStr) || ((String) value).isEmpty()) {
            gen.writeObject(value);
            return;
        }

        DesensitizedType[] strategyArr = desensitize.strategy();
        String result = UDesensitized.desensitized(valueStr, strategyArr);
        gen.writeString(result);
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property) throws JsonMappingException {
        Desensitize ann = property.getAnnotation(Desensitize.class);
        if (ann != null) {
            DesensitizeSerializer serializer = new DesensitizeSerializer();
            serializer.desensitize = ann;
            return serializer;
        }
        return this;
    }
}