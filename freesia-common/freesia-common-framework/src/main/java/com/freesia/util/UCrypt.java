package com.freesia.util;

import cn.hutool.core.codec.Base64;
import cn.hutool.crypto.Mode;
import cn.hutool.crypto.Padding;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import cn.hutool.crypto.symmetric.AES;
import cn.hutool.crypto.symmetric.SymmetricAlgorithm;
import com.alibaba.fastjson.JSONObject;
import com.freesia.constant.Constants;
import com.freesia.exception.CryptException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Security;

/**
 * @author Evad.Wu
 * @Description 加密/解密 工具类
 * @date 2024-03-15
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UCrypt {
    public static final String KEY = "KysrKytqamtrbGw7JzEyMw==";
    public static final String ALGORITHM = SymmetricAlgorithm.AES.getValue();
    public static final String PADDING = "AES/ECB/ZeroPadding";
    public static AES aes;
    public static String aesKey;

    static {
        Security.addProvider(new BouncyCastleProvider());
        initAes();
    }

    /**
     * 初始化AES秘钥
     */
    private static void initAes() {
        if (UEmpty.isEmpty(KEY)) {
            throw new CryptException("crypt.aes.key.required");
        }
        String key = decodeAesKey();
//        System.out.println("AES KEY：" + key);
        aes = new AES(Mode.ECB, Padding.ZeroPadding, key.getBytes(StandardCharsets.UTF_8));
        aesKey = key;
    }

    /**
     * BASE64解密AES key
     *
     * @return 解密后明文
     */
    private static String decodeAesKey() {
        return Base64.decodeStr(KEY);
    }


    /**
     * 没有AES key时，执行该方法获取AES Key，并添加到成员变量{@link UCrypt#KEY} 中
     *
     * @param key 加密前明文
     * @return 生成的AES key
     */
    private static String buildAesKey(String key) {
        if (UEmpty.isEmpty(key)) {
            throw new CryptException("crypt.aes.key.required");
        }
        //随机生成密钥
        final SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), ALGORITHM);
        //转成字符串
        final SecretKey secretKey = SecureUtil.generateKey(PADDING, keySpec);
        return Base64.encodeStr(secretKey.getEncoded(), false, false);
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
     * 使用默认的key和iv加密
     *
     * @param data 明文
     * @return 加密后的密文
     */
    public static String aesEncrypt(String data) {
        return aes.encryptBase64(data);
    }

    /**
     * 使用默认的key和iv解密
     *
     * @param data 密文
     * @return 解密后的明文
     */
    public static String aesDecrypt(String data) {
        return aes.decryptStr(data, StandardCharsets.UTF_8);
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
        String encrypt = JSONObject.parseObject(requestBody).getString(Constants.ENCRYPT);
        String decrypt = aesDecrypt(encrypt);
        return JSONObject.parseObject(decrypt, clz);
    }

    public static void main(String[] args) throws Exception {
        String json = "{\"accountStatus\":\"1\",\"avatar\":\"avatar/DDF.png\",\"buildIn\":false,\"createTime\":\"2023-08-15 08:52:26\",\"creator\":\"Evad\",\"deptId\":\"1697116611017363459\",\"email\":\"1814926857@qq.com\",\"gender\":\"M\",\"id\":\"1\",\"logicDel\":false,\"modifier\":\"admin\",\"modifyTime\":\"2024-02-19 17:56:42\",\"nickName\":\"Dungeon Master\",\"password\":\"$2a$10$/QC8MgeOJWaymIe61PWKCO6SvstpPVpihq6I1iaLAs/y2aNu6CiC2\",\"recVer\":\"7\",\"remark\":\"My name is Van, I'm an artist, a performance artist, I'm hired for people to fulfill their fantasies, the Deep Dark Fantasies.\",\"telNo\":\"18878269603\",\"userName\":\"admin\",\"userType\":\"sys_user\"}";
//        final String encryptBase64 = aesEncrypt("666");
//        System.out.println(encryptBase64);
        String base64 =
//                "b6Gzu7Y+opaihJ0raRLwLViEGz6kDtyYzudQ4TniLgYNxVP5pcs0wQZHTAgiB+ZJM/xexrsJrxXSpF43DhyiyoRqtHTx1DyIZzZ3G4cOX5gr6/7ZFlF2EvpjUumb0lZpRGzU/OAh34VrlnC6Yejfr6xoGyRR65ilGQI781Whhy08fVXHZrHF3/7ZG+/jpHQoHXsjfaXUGiP61bPw1aMduORsmVopx13dgWjcSuDwVoi3nNF9upRN5o446bb9A+2ZbbVavtmXskG1OELY7jI8IKfgm6KUp0c9xyGUf+g3S13QLWFuAL32q5me5NgjPpnXWF4eWRpjphxSZGY16i8eiExJ3Cd19s3dDfX7LQ/XwQpx1qIohQGlkSi1cx0ktp+lXKdeoyVtIPuRELVUbBrMNnzjgCodSIaIrSw/smlYSkjQCfMoy81vsl5Rm0DDSHK5yelxl0+3lk/lOrsjyOQ8Aect3VxnrdcnB2+/b8gFMSMk6Yt5SA+X7L7wztJGUjGKP/7TGokDa13yEQwe5na/cH4vzo+L9hFiVfOOhYL49MSvu8vYv+YfEfZ688chiCvRoq4YJdSrikDhVon8mvSD6lDIkf14Dd3cxCiz9HNIA65ZwoHbnhrWIKTjetqc2yFD7ozInTDZNskEd15ZqTcoSNv+QCJtV4kn8J6zsCfR+IQ7RQzTefoYU7vELD94tOi4BxyftH3tgljWrx4mIWrp2KtPbGiG3CqfmQzyJLZo1djtU3oqLw9d2AgGdY/HXyUCBTlJMuvFRDiXt521iVZZPQ==";
                "b6Gzu7Y+opaihJ0raRLwLViEGz6kDtyYzudQ4TniLgbT78YTAYQsBskpdQ1o/ubKR+mW4PSpNlhVCWfgllfL89nUYQRr3q0f3cwFxD1mlERgA9Le50r/MV6vhynRFRTH4jS/PFBJeyGGziWkmDJmOMSMODfOTXMcxzcajP47qze7MQes4z/7Szh8qXVVD+OjJU4dkat286EX+1s0NiMNW3BMCRqzs7MJ0ef5OM+njkYiBeNQ7dERWDzg0vwsNPo0pra/grLi+IdwRmRPc6Ruw5IQFrEzVUrl0FNCJhs22dloVmi8HGpKTer4Ngyh9tU6tjIXzCKfwp925jnOxAvPUVS4XK5x7NKRonq9G5Tiv1fwWgxoF2LpaWOitRVLiTKa4UTXalxkCq5Ls7l4XOTg4DOEWYlI343/DKG5k7YAVK53LmOMh4jFEVVubFpsWfrVmXa4DLZySc+6BpIQNv8nOuzIyCLvyIImr1/tBpd+l7sJGLRY9KPxZKUo1wCtl4d/BwyukjwQ0f+U3HHCX1MY7RVk3CLaazCdE5l3wvy/Sk7nsZM136IYrx+STcrb68WHL0+nivwXxLIWBlrWsnF9jIRFZd5TRJca7F1nHae5me7auE/s6DPTQkh1RtFriD0/UYphN9gU5mhtBvjxR+CKmg==";
//        final String aesDesEncrypt = aesDecrypt(base64);
//        System.out.println(aesDesEncrypt);


        String aesKey = "KysrKytqamtrbGw7JzEyMw==";
        System.out.println(Base64.decodeStr(aesKey));
    }
}
