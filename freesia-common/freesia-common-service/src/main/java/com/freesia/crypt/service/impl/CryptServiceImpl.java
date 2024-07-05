package com.freesia.crypt.service.impl;

import cn.hutool.crypto.asymmetric.RSA;
import com.freesia.constant.CacheConstant;
import com.freesia.constant.CryptModule;
import com.freesia.exception.CryptException;
import com.freesia.crypt.service.CryptService;
import com.freesia.util.UCrypt;
import com.freesia.exception.ServiceException;
import com.freesia.util.UEmpty;
import com.freesia.util.URedis;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @author Evad.Wu
 * @Description 加密/解密 业务逻辑实现
 * @date 2024-03-19
 */
@Service
public class CryptServiceImpl implements CryptService {

    @Override
    public void initRsa() {
        // 初始化RSA工具，生成密钥对
        RSA rsa = new RSA();
        final String pub1 = rsa.getPublicKeyBase64();
        final String pri1 = rsa.getPrivateKeyBase64();
        URedis.set(CacheConstant.CRYPT_PUB, pub1);
        URedis.set(CacheConstant.CRYPT_PRI, pri1);
    }

    @Override
    public String getPublicKey() {
        final String pub1 = URedis.get(CacheConstant.CRYPT_PUB).toString();
        return Optional.of(pub1).orElseThrow(() -> new ServiceException(CryptModule.CRYPT_MANAGEMENT, "crypt.get.pub1.failed"));
    }

    @Override
    public String wrapEncryptPub2(String encryptPub2) {
        // 获取后端私钥，解密入参
        final String pri1 = URedis.get(CacheConstant.CRYPT_PRI).toString();
        if (UEmpty.isEmpty(pri1)) {
            throw new CryptException("crypt.get.pri1.failed");
        }
        final String pub2 = UCrypt.rsaDecrypt(pri1, encryptPub2);
        return UCrypt.rsaEncrypt(pub2, UCrypt.KEY);
    }
}
