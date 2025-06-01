package com.freesia.serde;

import cn.hutool.core.date.DateUtil;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.util.Date;

/**
 * @author Evad.Wu
 * @Description Jackson反序列化器
 * @date 2025-06-02
 */
public class JacksonDateDeserializer extends JsonDeserializer<Date> {

    /**
     * 反序列化逻辑：将字符串转换为 Date 对象
     *
     * @param p       JSON 解析器，用于获取字符串值
     * @param context 上下文环境（可用于获取更多配置）
     * @return 转换后的 Date 对象，若为空字符串返回 null
     * @throws IOException 当字符串格式非法或转换失败时抛出
     */
    @Override
    public Date deserialize(JsonParser p, DeserializationContext context) throws IOException {
        return DateUtil.parse(p.getText());
    }

}
