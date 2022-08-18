package com.lifly.pattern.calculator.正则表达式;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Regexp {
    /**
     * 提取文本中的英文
     * @return
     */
    public static String getEnglish(String txt){
        Pattern pattern=Pattern.compile("[a-zA-Z]]+");
        Matcher matcher=pattern.matcher(txt);
        StringBuilder sb=new StringBuilder();
        while(matcher.find()){
            sb.append(matcher.group(0)+",");
        }
        return sb.toString();
    }
}
