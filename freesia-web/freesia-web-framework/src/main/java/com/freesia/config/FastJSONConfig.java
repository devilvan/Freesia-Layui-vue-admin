package com.freesia.config;

import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.ToStringSerializer;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.freesia.constant.Constants;
import com.freesia.desensization.handler.DesensitizeValueFilter;
import com.freesia.oss.seder.DomainSeValueFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;

import java.math.BigInteger;
import java.util.List;

/**
 * @author Bliss.Wu
 * @Description FastJSON 配置类
 * @date 2025-05-15
 */
@Configuration
public class FastJSONConfig {
    @Bean(value = "fastJsonHttpMessageConverter")
    public FastJsonHttpMessageConverter buildFastJsonHttpMessageConverter() {
        FastJsonHttpMessageConverter fastConverter = new FastJsonHttpMessageConverter();
        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        fastJsonConfig.setDateFormat(Constants.YMD_HMS);
        // 解决循环引用导致树结构的children字段为空时依旧返回[]的问题
        fastJsonConfig.setSerializerFeatures(SerializerFeature.DisableCircularReferenceDetect);
        //解决Long转json精度丢失的问题
        SerializeConfig serializeConfig = SerializeConfig.globalInstance;
        serializeConfig.put(BigInteger.class, ToStringSerializer.instance);
        serializeConfig.put(Long.class, ToStringSerializer.instance);
        serializeConfig.put(Long.TYPE, ToStringSerializer.instance);
        // 添加脱敏拦截器
        fastJsonConfig.setSerializeFilters(new DesensitizeValueFilter(), new DomainSeValueFilter());
        fastJsonConfig.setSerializeConfig(serializeConfig);
        //处理中文乱码问题
        List<MediaType> fastMediaTypes = List.of(MediaType.APPLICATION_JSON);
        fastConverter.setSupportedMediaTypes(fastMediaTypes);
        fastConverter.setFastJsonConfig(fastJsonConfig);
        return fastConverter;
    }
}
