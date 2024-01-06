package com.freesia;

import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FreesiaTest {
    @Test
    public void match() {
//        String regEx = "^\\/[\\w\\$\\d]+(\\/\\w+\\d*)*$";
//        String regEx = "^/([A-Za-z0-9$_])*";
        String regEx = "^/([A-Za-z0-9$_-])+(/[A-Za-z0-9$_-]*)*$";
        Pattern pattern = Pattern.compile(regEx);
        //用定义好的正则表达式拆分字符串，把字符串中的数字留出来
        Matcher matcher = pattern.matcher("/iframe$/inner$/index%");
        while (matcher.find()) {
            System.out.println(matcher.group());
        }
    }
}
