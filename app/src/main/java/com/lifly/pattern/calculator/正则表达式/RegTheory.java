package com.lifly.pattern.calculator.正则表达式;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegTheory {
    /**
     * 找到四个数字连在一起的子串
     *
     * @param txt
     * @return
     */
    public static String test1(String txt) {
        String regexStr = "\\d\\d\\d\\d";
        Pattern pattern = Pattern.compile(regexStr);
        Matcher matcher = pattern.matcher(txt);

        StringBuilder sb = new StringBuilder();
        /**
         * matcher.find()会把找到的开始位置索引和结束位置的索引放到group[0]和group[1]中，前面包含，后面不包含
         * 同时将olaLast置为结束位置，用于下次匹配
         */
        while (matcher.find()) {
            sb.append(matcher.group(0));
        }
        return sb.toString();
    }
    /**
     * 找到四个数字连在一起的子串
     *
     * @param txt
     * @return
     */
    public static String test2(String txt) {
        String regexStr = "(\\d\\d)(\\d\\d)";
        Pattern pattern = Pattern.compile(regexStr);
        Matcher matcher = pattern.matcher(txt);

        StringBuilder sb = new StringBuilder();
        /**
         * matcher.find()会把找到的开始位置索引和结束位置的索引放到group[0]和group[1]中，前面包含，后面不包含
         * 同时将olaLast置为结束位置，用于下次匹配
         * group[2]第一组的开始位置group[3]第一组的结束位置
         * group[4]第二组的开始位置group[5]第二章的结束位置
         */
        while (matcher.find()) {
            sb.append(matcher.group(0));
        }
        return sb.toString();
    }
}
