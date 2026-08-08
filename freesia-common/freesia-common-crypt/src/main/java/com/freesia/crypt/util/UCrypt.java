package com.freesia.crypt.util;

import cn.hutool.crypto.Mode;
import cn.hutool.crypto.Padding;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import cn.hutool.crypto.symmetric.AES;
import com.freesia.constant.Constants;
import com.freesia.crypt.exception.CryptException;
import com.freesia.crypt.service.AesKeyManager;
import com.freesia.json.util.UJSON;
import com.freesia.util.USpring;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

/**
 * @author Evad.Wu
 * @Description 加密/解密 工具类
 * AES密钥不再硬编码，改为由 AesKeyManager 动态轮换提供
 * @date 2024-03-15
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UCrypt {

    private static volatile AesKeyManager aesKeyManager;

    /**
     * 懒解析AES密钥管理器，避免工具类在Spring容器就绪前被加载
     */
    private static AesKeyManager aesKeyManager() {
        if (aesKeyManager == null) {
            aesKeyManager = USpring.getBean(AesKeyManager.class);
        }
        return aesKeyManager;
    }

    private static AES buildAes(String key) {
        return new AES(Mode.ECB, Padding.PKCS5Padding, key.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * RSA 公钥加密
     *
     * @param pubKey 公钥
     * @param data   待加密数据
     * @return 加密数据
     */
    public static String rsaEncrypt(String pubKey, String data) {
        return new RSA(null, pubKey).encryptBase64(data, KeyType.PublicKey);
    }

    /**
     * RSA 私钥解密
     *
     * @param priKey 私钥
     * @param data   待解密数据
     * @return 解密数据
     */
    public static String rsaDecrypt(String priKey, String data) {
        return new RSA(priKey, null).decryptStr(data, KeyType.PrivateKey);
    }

    /**
     * 使用当前AES密钥加密
     *
     * @param data 明文
     * @return 加密后的密文
     */
    public static String aesEncrypt(String data) {
        return buildAes(aesKeyManager().getCurrentKey()).encryptBase64(data);
    }

    /**
     * 依次使用当前/上一把AES密钥解密，均失败则抛出异常
     *
     * @param data 密文
     * @return 解密后的明文
     */
    public static String aesDecrypt(String data) {
        List<String> keys = aesKeyManager().getDecryptKeys();
        for (String key : keys) {
            try {
                return buildAes(key).decryptStr(data, StandardCharsets.UTF_8);
            } catch (RuntimeException e) {
                // 错钥解PKCS5通常抛异常，继续尝试下一把
            }
        }
        throw new CryptException("crypt.aes.decrypt.failed", new Object[] {});
    }

    /**
     * 解密前端传来的报文，转为为对象
     *
     * @param requestBody 请求报文
     * @param clz         待转换的对象类型
     * @param <T>         对象泛型
     * @return 转换后的对象类型
     */
    public static <T> T aesDecryptJSON(String requestBody, Class<T> clz) {
        String encrypt = Optional.ofNullable(UJSON.parseMap(requestBody)).map(item -> item.get(Constants.ENCRYPT, "")).orElse("");
        String decrypt = aesDecrypt(encrypt);
        return UJSON.parseObject(decrypt, clz);
    }

    /**
     * md5加密
     *
     * @param obj 入参
     * @param <T> {@link Object,File,InputStream}
     * @return 加密密文
     */
    public static <T> String md5Encrypt(T obj) {
        if (obj instanceof String) {
            return SecureUtil.md5((String) obj);
        } else if (obj instanceof File) {
            return SecureUtil.md5((File) obj);
        } else if (obj instanceof InputStream) {
            return SecureUtil.md5((InputStream) obj);
        }
        return null;
    }
}
