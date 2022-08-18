package com.yele.bluetoothlib.utils;

public class CodeUtils {

    public static String toUNICODE(String s)
    {
        StringBuilder sb=new StringBuilder();
        for(int i=0;i<s.length();i++)
        {
//            sb.append("\\" + "u"+Integer.toHexString(s.charAt(i)));
            String hex = Integer.toHexString(s.charAt(i));
            if (hex.length() == 2) {
                hex = "00" + hex;
            }
            sb.append(hex);
        }
        return sb.toString();
    }
}
