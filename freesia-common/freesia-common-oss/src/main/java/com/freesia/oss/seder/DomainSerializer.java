package com.freesia.oss.seder;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.freesia.oss.annotation.Domain;
import com.freesia.oss.pojo.OssFactory;
import com.freesia.oss.pojo.OssHandler;
import com.freesia.util.UEmpty;

import java.io.IOException;

/**
 * @author Evad.Wu
 * @Description Jackson序列化时将URL转化为IP模式 值过滤器
 * @date 2025-05-31
 */
public class DomainSerializer extends JsonSerializer<Object> implements ContextualSerializer {
    private Domain domain;

    @Override
    public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value == null || (value instanceof String && ((String) value).isEmpty())) {
            gen.writeObject(value);
            return;
        }

        if (value instanceof String) {
            OssHandler ossHandler;
            if (UEmpty.isNotEmpty(domain.configKey())) {
                ossHandler = OssFactory.getInstance(domain.configKey());
            } else {
                ossHandler = OssFactory.getInstance();
            }
            String result = ossHandler.convertEndpoint2Domain(value.toString());
            gen.writeString(result);
        } else {
            gen.writeObject(value);
        }
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property) throws JsonMappingException {
        if (property != null) {
            Domain ann = property.getAnnotation(Domain.class);
            if (ann != null) {
                DomainSerializer serializer = new DomainSerializer();
                serializer.domain = ann;
                return serializer;
            }
        }
        return this;
    }
}
