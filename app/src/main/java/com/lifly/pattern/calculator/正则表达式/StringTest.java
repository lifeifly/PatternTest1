package com.lifly.pattern.calculator.正则表达式;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringTest {
    public static void main(String[] args) {
        String content = "JDK1.3  JDK1.4 JDKSE 到杀死你的";
        content = content.replace("JDK1\\.3|JDK1\\.4", "JDK");
        content = "13899599699";
        System.out.println(content.matches("^13[8|9]\\d{8}"));
    }

    public static void test1(String txt) {
        //要求只有一个@，@前面可以有0-9a-zA-Z_-字符，后面是域名只能是英文字母
        String regex = "[\\w-]+(@[a-zA-Z]+\\.)+[a-zA-Z]+";

        //验证是不是正数或小数
        String regex1 = "^[+-]?([1-9]\\d*|0)\\d+(\\.\\d+)?$";

        //对URL进行解析要求得到协议、域名、端口、文件名
        String regex2="^([a-zA-Z]+)://([a-zA-Z]):(\\d+)[\\w-/]*/([\\w.]+)$";
        Pattern compile = Pattern.compile(regex2);
        Matcher matcher = compile.matcher(txt);
        if (matcher.matches()){
            matcher.group(1);
            matcher.group(2);
            matcher.group(3);
            matcher.group(4);
        }
    }
}
