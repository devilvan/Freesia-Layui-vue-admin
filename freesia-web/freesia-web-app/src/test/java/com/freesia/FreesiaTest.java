package com.freesia;

import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.Comment;
import japa.parser.ast.CompilationUnit;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;
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
}
