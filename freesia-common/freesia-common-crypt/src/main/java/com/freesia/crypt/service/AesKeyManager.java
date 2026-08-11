package com.freesia.crypt.service;

import cn.hutool.core.codec.Base64;
import com.freesia.constant.CacheConstant;
import com.freesia.redis.util.URedis;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Evad.Wu
 * @Description AES密钥管理：懒触发轮换 + 宽限期
 * 当前密钥超龄(rotationPeriod)时自动轮换：当前钥降为上一把(保留gracePeriod)，生成新钥。
 * 无需定时任务，每次访问自检，应用重启/Redis清空后可自愈。
 * @date 2026-08-08
 */
@Slf4j
@Component
public class AesKeyManager {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    /** AES密钥字节长度（AES-128），Base64后为24字符ASCII串，与前端CryptoJS UTF-8解析一致 */
    private static final int AES_KEY_BYTES = 16;

    private final Duration rotationPeriod;
    private final Duration gracePeriod;

    public AesKeyManager(@Value("${freesia.crypt.aes-rotation-minutes:60}") long rotationMinutes,
                         @Value("${freesia.crypt.aes-grace-minutes:10}") long graceMinutes) {
        this.rotationPeriod = Duration.ofMinutes(Math.max(1, rotationMinutes));
        this.gracePeriod = Duration.ofMinutes(Math.max(1, graceMinutes));
    }

    /**
     * 获取当前AES密钥；无或超龄则生成/轮换
     */
    public synchronized String getCurrentKey() {
        String key = get(CacheConstant.CRYPT_AES);
        Long createdAt = getTimestamp();
        if (key == null) {
            key = generate();
            writeCurrent(key);
            return key;
        }
        if (createdAt != null && System.currentTimeMillis() - createdAt >= rotationPeriod.toMillis()) {
            // 轮换：当前钥降为上一把（保留宽限期），生成新钥
            URedis.set(CacheConstant.CRYPT_AES_PREVIOUS, key, gracePeriod);
            key = generate();
            writeCurrent(key);
            log.info("AES密钥已轮换，上一把保留宽限期 {} 分钟", gracePeriod.toMinutes());
        }
        return key;
    }

    /**
     * 解密尝试用密钥：当前钥在前，上一把兜底
     */
    public List<String> getDecryptKeys() {
        List<String> keys = new ArrayList<>(2);
        String current = get(CacheConstant.CRYPT_AES);
        if (current != null) {
            keys.add(current);
        }
        String previous = get(CacheConstant.CRYPT_AES_PREVIOUS);
        if (previous != null) {
            keys.add(previous);
        }
        if (keys.isEmpty()) {
            keys.add(getCurrentKey());
        }
        return keys;
    }

    private String generate() {
        byte[] bytes = new byte[AES_KEY_BYTES];
        SECURE_RANDOM.nextBytes(bytes);
        return Base64.encode(bytes);
    }

    private void writeCurrent(String key) {
        Duration ttl = rotationPeriod.plus(gracePeriod);
        URedis.set(CacheConstant.CRYPT_AES, key, ttl);
        URedis.set(CacheConstant.CRYPT_AES_TS, String.valueOf(System.currentTimeMillis()), ttl);
    }

    private String get(String redisKey) {
        Object value = URedis.get(redisKey);
        return value == null ? null : value.toString();
    }

    private Long getTimestamp() {
        String ts = get(CacheConstant.CRYPT_AES_TS);
        if (ts == null) {
            return null;
        }
        try {
            return Long.parseLong(ts);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
