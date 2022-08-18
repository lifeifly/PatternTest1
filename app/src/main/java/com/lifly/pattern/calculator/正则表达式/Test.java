package com.lifly.pattern.calculator.正则表达式;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test {
    public static void test1(String txt) {
        String regex = null;
        //验证是不是汉字
        regex = "^[\u0391-\uffe5]+$";
        //邮政编码1-9开头的六位数
        regex = "^[1-9]\\d{5}&";
        //QQ号1-9开头的5位-10位
        regex = "^[1-9]\\d{4,9}$";
        // 手机号13、14、15、18开头的11位数
        regex = "^1[3|4|5|8]\\d{9}$";
        //URL,以http(s)://开头
        regex = "^((http|https)://)([\\w-]+\\.)+[\\w-]+(\\/[\\w-?=&/%.#]*)?$";
    }

    /**
     * 反向引用,内部反向引用用\\分组号
     * 外部的反向引用用$分组号
     */
    public static void reverse() {
        //匹配两个连续相同的数字
        String regex = "(\\d)\\1";
        //匹配5个连续的相同数字
        String regex1 = "(\\d)\\1{4}";
        //匹配个位和千位相同，十位和百位相同的数字
        String regex2 = "(\\d)(\\d)\\2\\1";
    }

    /**
     * 结巴程序
     */
    public static void test2() {
        String content = "我...我要...要学学.....学JAVA";
        //1。去掉所有的.
        Pattern pattern = Pattern.compile("\\.");
        Matcher matcher = pattern.matcher(content);
        content = matcher.replaceAll("");

        //2.找到相同的字符串
        pattern = Pattern.compile("(.)\\1+");
        matcher = pattern.matcher(content);
        while (matcher.find()) {

        }

        //使用反向引用$1来替换匹配的内容根据内部捕获的分组1替换匹配的到字符串
        content = matcher.replaceAll("$1");
    }
}
