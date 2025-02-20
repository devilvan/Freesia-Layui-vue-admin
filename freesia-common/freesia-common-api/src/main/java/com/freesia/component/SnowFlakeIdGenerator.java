package com.freesia.component;

import cn.hutool.core.lang.Singleton;
import com.freesia.pojo.SnowFlake;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * @author Evad.Wu
 * @Description 雪花算法ID生成器
 * @date 2022-08-07
 */
@Slf4j
@Component
public class SnowFlakeIdGenerator implements IdentifierGenerator, com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator {
    /**
     * 数据中心ID
     */
    @Value("${mybatis-plus.global-config.datacenter-id}")
    private long datacenterId;
    /**
     * 机器ID
     */
    @Value("${mybatis-plus.global-config.worker-id}")
    private long workerId;

    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
        return generate();
    }

    @Override
    public Number nextId(Object entity) {
        return generate();
    }

    private long generate() {
        SnowFlake snowflake = Singleton.get(SnowFlake.class, workerId, datacenterId);
        return snowflake.nextId();
    }
}

