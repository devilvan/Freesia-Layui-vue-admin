package com.freesia;

import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSONObject;
import com.freesia.dto.GiteeOauthTokenRequestDto;
import com.freesia.excel.listener.BaseImportEntityListener;
import com.freesia.excel.pojo.DemoData;
import com.freesia.excel.util.UExcel;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @author Evad.Wu
 * @Description java测试类
 * @date 2024-01-15
 */
@Slf4j
public class FreesiaTest {
    private static final String ENCRYPT_KEY = "Y29tLnNpbm9zZXJ2aWNlcy5vcmc=";

    @Test
    public void testNumberReg() {
        Pattern p = Pattern.compile("\\d+");
        Matcher m = p.matcher("25T以上");
        List<String> numbers = new ArrayList<>();
        while (m.find()) {
            numbers.add(m.group());
        }
        for (String number : numbers) {
            System.out.println(number);
        }
    }

    @Test
    public void testDuration() {
        long millis = Duration.ofMillis(60 * 1000).toMillis();
        Date date = new Date();
        date.setTime(date.getTime() + millis);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println(sdf.format(date));

        Duration pt1H30M = Duration.parse("PT1H30M");
        long millis1 = pt1H30M.toMillis();
        date.setTime(date.getTime() + millis1);
        System.out.println(sdf.format(date));
    }

    @Test
    public void testAesEncrypt() {
        String value = "cU3bR6sY";
//        System.out.println(aesDecrypt("0U3VLpiniSZ3t8tWTNlI4A==", ENCRYPT_KEY));
        System.out.println(aesEncrypt(value, ENCRYPT_KEY));
    }

    private String aesEncrypt(String value, String encryptKey) {
        try {
            String key = getEncryptKey(encryptKey);
            if (key == null) {
                return null;
            }
            KeyGenerator kgen = KeyGenerator.getInstance("$AES");

            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
            secureRandom.setSeed(key.getBytes());
            kgen.init(128, secureRandom);
            Cipher cipher = Cipher.getInstance("$AES");
            cipher.init(1, new SecretKeySpec(kgen.generateKey().getEncoded(), "$AES"));
            byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
            byte[] resultBytes = cipher.doFinal(bytes);
            return base64Encode(resultBytes);
        } catch (NoSuchAlgorithmException localNoSuchAlgorithmException) {
        } catch (NoSuchPaddingException localNoSuchPaddingException) {
        } catch (InvalidKeyException localInvalidKeyException) {
        } catch (IllegalBlockSizeException localIllegalBlockSizeException) {
        } catch (BadPaddingException localBadPaddingException) {
        }
        return null;
    }

    private String aesDecrypt(String value, String encryptKey) {
        try {
            String key = getEncryptKey(encryptKey);
            if (key == null) {
                return null;
            }
            byte[] bytes = base64Decode(value);
            KeyGenerator kgen = KeyGenerator.getInstance("$AES");

            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
            secureRandom.setSeed(key.getBytes());
            kgen.init(128, secureRandom);
            Cipher cipher = Cipher.getInstance("$AES");
            cipher.init(2, new SecretKeySpec(kgen.generateKey().getEncoded(), "$AES"));
            byte[] result = cipher.doFinal(bytes);
            return new String(result, "utf-8");
        } catch (NoSuchAlgorithmException localNoSuchAlgorithmException) {
        } catch (NoSuchPaddingException localNoSuchPaddingException) {
        } catch (InvalidKeyException localInvalidKeyException) {
        } catch (IOException localIOException) {
        } catch (IllegalBlockSizeException localIllegalBlockSizeException) {
        } catch (BadPaddingException localBadPaddingException) {
        }
        return null;
    }

    private String base64Encode(byte[] bytes) {
        Base64.Encoder encoder = Base64.getEncoder();
        return encoder.encodeToString(bytes);
    }

    private String getEncryptKey(String key) {
        try {
            byte[] resultByte = base64Decode(key);
            return new String(resultByte, "utf-8");
        } catch (IOException localIOException) {
        }
        return null;
    }

    private byte[] base64Decode(String base64Value)
            throws IOException {
        Base64.Decoder decoder = Base64.getDecoder();
        byte[] decodeByte = decoder.decode(base64Value);
        return decodeByte;
    }


    @Test
    public void testExcelRead() {
        String fileName = "D:\\Mine\\文本文件\\测试导入.xls";
        UExcel.read(fileName, DemoData.class);
    }

    /**
     * 最简单的读
     * <p>
     * 1. 创建excel对应的实体对象 参照{@link DemoData}
     * <p>
     * 2. 由于默认一行行的读取excel，所以需要创建excel一行一行的回调监听器，参照{@link BaseImportEntityListener}
     * <p>
     * 3. 直接读即可
     */
    @Test
    public void simpleRead() {
        // 写法1：JDK8+ ,不用额外写一个DemoDataListener
        // since: 3.0.0-beta1
        String fileName = "D:\\Mine\\文本文件\\测试导入.xls";
//        // 这里默认每次会读取100条数据 然后返回过来 直接调用使用数据就行
//        // 具体需要返回多少行可以在`PageReadListener`的构造函数设置
//        EasyExcel.read(fileName, DemoData.class, new PageReadListener<DemoData>(dataList -> {
//            for (DemoData demoData : dataList) {
//                log.info("读取到一条数据{}", JSON.toJSONString(demoData));
//            }
//        })).sheet().doRead();

//        // 写法2：
//        // 匿名内部类 不用额外写一个DemoDataListener
//        fileName = "D:\\Mine\\文本文件\\测试导入.xls";
//        // 这里 需要指定读用哪个class去读，然后读取第一个sheet 文件流会自动关闭
//        EasyExcel.read(fileName, DemoData.class, new ReadListener<DemoData>() {
//            /**
//             * 单次缓存的数据量
//             */
//            public static final int BATCH_COUNT = 100;
//            /**
//             *临时存储
//             */
//            private List<DemoData> cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);
//
//            @Override
//            public void invoke(DemoData data, AnalysisContext context) {
//                cachedDataList.add(data);
//                if (cachedDataList.size() >= BATCH_COUNT) {
//                    saveData();
//                    // 存储完成清理 list
//                    cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);
//                }
//            }
//
//            @Override
//            public void doAfterAllAnalysed(AnalysisContext context) {
//                saveData();
//            }
//
//            /**
//             * 加上存储数据库
//             */
//            private void saveData() {
//                log.info("{}条数据，开始存储数据库！", cachedDataList.size());
//                log.info("存储数据库成功！");
//            }
//        }).sheet().doRead();

        // 有个很重要的点 DemoDataListener 不能被spring管理，要每次读取excel都要new,然后里面用到spring可以构造方法传进去
        // 写法3：
        fileName = "D:\\Mine\\文本文件\\测试导入.xls";
        // 这里 需要指定读用哪个class去读，然后读取第一个sheet 文件流会自动关闭
        EasyExcel.read(fileName, DemoData.class, new BaseImportEntityListener()).sheet().doRead();

//        // 写法4
//        fileName = "D:\\Mine\\文本文件\\测试导入.xls";
//        // 一个文件一个reader
//        try (ExcelReader excelReader = EasyExcel.read(fileName, DemoData.class, new DemoDataListener()).build()) {
//            // 构建一个sheet 这里可以指定名字或者no
//            ReadSheet readSheet = EasyExcel.readSheet(0).build();
//            // 读取一个sheet
//            excelReader.read(readSheet);
//        }
    }


    @Test
    public void lastIndexOf() {
        String component = "iframe/inner/index222";
        int index = component.lastIndexOf("/");
        component = component.substring(0, index);
        System.out.println(component);
    }

    @Test
    public void match() {
//        String regEx = "^\\/[\\w\\$\\d]+(\\/\\w+\\d*)*$";
//        String regEx = "^/([A-Za-z0-9$_])*";
        String regEx = "^([A-Za-z0-9$_-])+(/[A-Za-z0-9$_-]*)*$";
        Pattern pattern = Pattern.compile(regEx);
        //用定义好的正则表达式拆分字符串，把字符串中的数字留出来
        Matcher matcher = pattern.matcher("iframe$/inner$/index%");
        while (matcher.find()) {
            System.out.println(matcher.group());
        }
    }

    @Test
    public void testOauthToken() {
        GiteeOauthTokenRequestDto giteeOauthTokenRequestDto = new GiteeOauthTokenRequestDto();
        giteeOauthTokenRequestDto.setGrantType("password");
        giteeOauthTokenRequestDto.setUserName("1005338848@qq.com");
        giteeOauthTokenRequestDto.setPassword("741258963hjkl");
        giteeOauthTokenRequestDto.setClientId("2968807b6c7d6403f62e59b4972e3ac15166fa2c1828c27ed4ca40c2fb79332d");
        giteeOauthTokenRequestDto.setClientSecret("eebc1bb2caf6cd34ae3f93c6ed1d098b16ce323b48129816240d9008f8389b8c");
        giteeOauthTokenRequestDto.setScope(
                GiteeOauthTokenRequestDto.Scope.USER_INFO,
                GiteeOauthTokenRequestDto.Scope.PULL_REQUESTS,
                GiteeOauthTokenRequestDto.Scope.ISSUES
        );
        String json = JSONObject.toJSONString(giteeOauthTokenRequestDto);
        Map<String, Object> params = JSONObject.parseObject(json).getInnerMap();
        System.out.println(json);
    }

    @Test
    public void testCrypt() throws Exception {
        String originalText = "cU3bR6sY"; // 要加密的原始文本

        byte[] keyBytes = generateKey(); // 生成随机的16字节密钥
        SecretKey secretKey = new SecretKeySpec(keyBytes, "$AES"); // 创建SecretKey对象

        Cipher cipher = Cipher.getInstance("$AES/ECB/PKCS5Padding"); // 选择加密模式和填充方式
        cipher.init(Cipher.ENCRYPT_MODE, secretKey); // 初始化加密器

        byte[] encryptedBytes = cipher.doFinal(originalText.getBytes()); // 执行加密操作
        System.out.println("Encrypted Text: " + DatatypeConverter.printHexBinary(encryptedBytes));

        cipher.init(Cipher.DECRYPT_MODE, secretKey); // 初始化解密器
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes); // 执行解密操作
        System.out.println("Decrypted Text: " + new String(decryptedBytes));
    }

    private static byte[] generateKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance("$AES");
        SecureRandom random = new SecureRandom();
        keyGen.init(random);
        return keyGen.generateKey().getEncoded();
    }

    @Test
    public void testCryptBase64() {
        // 原始字符串
        String originalText = "cU3bR6sY"; // 要加密的原始文本

        // 加密
        String encodedString = Base64.getEncoder().encodeToString(originalText.getBytes());
        System.out.println("加密后的字符串: " + encodedString);

        // 解密
        String decodedString = new String(Base64.getDecoder().decode(encodedString));
        System.out.println("解密后的字符串: " + decodedString);
    }

    @Test
    public void testSplit() {
        String name = "Van Darkholme";
        name = StrUtil.trim(name);
        // 第一个空格之后都脱敏处理
        String[] nameSplit = name.split(" ");
        if (nameSplit.length > 1) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0, len = nameSplit.length - 1; i < len; i++) {
                sb.append("*".repeat(nameSplit[i].length()));
                if (i != len - 1) {
                    sb.append("*");
                }
            }
            sb.append(" ").append(nameSplit[nameSplit.length - 1]);
            System.out.println(sb);
        } else if (nameSplit.length == 1) {
            System.out.println(nameSplit[0]);
        }
    }

    @Test
    public void testMobilePhoneDesensitize() {
        String num = "010 88789963";
        System.out.println(StrUtil.hide(num, 3, num.length() - 4));
    }
}
