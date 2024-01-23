package com.freesia;

import com.alibaba.fastjson.JSONObject;
import com.freesia.dto.GiteeCommitsResponseDto;
import com.freesia.dto.GiteeOauthTokenRequestDto;
import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.Comment;
import japa.parser.ast.CompilationUnit;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


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
}
