package com.yele.hu.blesdk520demo.util;

import java.io.UnsupportedEncodingException;

public class StringUtils {
    /**
     * 判断当前字符串是否为空
     * @param str 需要判断的字符串
     * @return 字符串是否为空
     */
    public static boolean isEmpty(String str) {
        return str == null || str.equals("");
    }

    public static final String ALLOWED_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-_.!~*'()";

    private static String getHex(byte buf[])
    {
        StringBuilder o = new StringBuilder(buf.length * 3);
        for (int i = 0; i < buf.length; i++ )
        {
            int n = (int)buf[i] & 0xff;
            o.append("%");
            if (n < 0x10)
            {
                o.append("0");
            }
            o.append(Long.toString(n, 16).toUpperCase());
        }
        return o.toString();
    }

    public static String encodeURIComponent(String input) {
        if (null == input || "".equals(input.trim())) {
            return input;
        }

        int l = input.length();
        StringBuilder o = new StringBuilder(l * 3);
        try {
            for (int i = 0; i < l; i++) {
                String e = input.substring(i, i + 1);
                if (ALLOWED_CHARS.indexOf(e) == -1) {
                    byte[] b = e.getBytes("utf-8");
                    o.append(getHex(b));
                    continue;
                }
                o.append(e);
            }
            return o.toString();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return input;
    }
}
