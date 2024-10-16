package com.freesia.crypt.service;

/**
 * @author Evad.Wu
 * @Description 加密/解密 业务逻辑接口
 * @date 2024-03-19
 */
public interface CryptService {
    /**
     * 初始化RSA公钥私钥
     */
    void initRsa();

    /**
     * 获取后端公钥
     *
     * @return 后端公钥
     */
    String getPublicKey();

    /**
     * 获取前端根据PUB1加密得到的PUB2，解密后根据AES加密，返回结果
     *
     * @param encryptPub2 前端根据PUB1加密得到的PUB2
     * @return AES加密信息
     * @throws Exception 异常信息
     */
    String wrapEncryptPub2(String encryptPub2) throws Exception;
}
