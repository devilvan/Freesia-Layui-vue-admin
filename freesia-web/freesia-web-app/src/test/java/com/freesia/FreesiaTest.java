package com.freesia;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.freesia.dto.GiteeOauthTokenRequestDto;
import com.freesia.util.UEmpty;
import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.Comment;
import japa.parser.ast.CompilationUnit;
import org.junit.Test;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @author Evad.Wu
 * @Description java测试类
 * @date 2024-01-15
 */
public class FreesiaTest {
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
    public void testComment() {
        try {
            CompilationUnit compilationUnit = JavaParser.parse(new File("D:\\Mine\\Maven\\freesia\\freesia-web\\freesia-web-dashboard\\src\\main\\java\\com\\freesia\\controller\\GiteeController.java"));
            List<Comment> comments = compilationUnit.getComments();
            for (Comment comment : comments) {
                System.out.println(comment.getContent());
            }
        } catch (ParseException | IOException e) {
            e.printStackTrace();
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
        SecretKey secretKey = new SecretKeySpec(keyBytes, "AES"); // 创建SecretKey对象

        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding"); // 选择加密模式和填充方式
        cipher.init(Cipher.ENCRYPT_MODE, secretKey); // 初始化加密器

        byte[] encryptedBytes = cipher.doFinal(originalText.getBytes()); // 执行加密操作
        System.out.println("Encrypted Text: " + DatatypeConverter.printHexBinary(encryptedBytes));

        cipher.init(Cipher.DECRYPT_MODE, secretKey); // 初始化解密器
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes); // 执行解密操作
        System.out.println("Decrypted Text: " + new String(decryptedBytes));
    }

    private static byte[] generateKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
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
        } else if (nameSplit.length == 1){
            System.out.println(nameSplit[0]);
        }
    }

    @Test
    public void testMobilePhoneDesensitize() {
        String num = "010 88789963";
        System.out.println(StrUtil.hide(num, 3, num.length() - 4));
    }
}
