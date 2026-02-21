package com.freesia.desensization.handler;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

/**
 * @author Bliss.Wu
 * @Description 数字类型->字符串 序列化器
 * @date 2026-02-22
 */
public class NumberToStringSerializer extends StdSerializer<Number> {
    public NumberToStringSerializer() {
        super(Number.class);
    }

    @Override
    public void serialize(Number value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeString(String.valueOf(value));
    }
}
