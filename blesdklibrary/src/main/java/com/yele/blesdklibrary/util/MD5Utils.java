package com.yele.blesdklibrary.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Utils {

    /**
     * 根据传入的文件路径获取到文件的MD5
     * @param path 文件的绝对路径
     * @return 当前文件的MD5，如果路径不存在就返回空
     */
    public static String md5File(String path){
        if (path == null) {
            return "";
        }
        File file = new File(path);
        return md5File(file);
    }

    /**
     * 根据传入的文件判断
     * @param file 当前文件句柄
     * @return 当前文件对应的MD5
     */
    public static String md5File(File file) {
        if (file == null || !file.isFile() || !file.exists()) {
            return "";
        }
        FileInputStream in = null;
        String result = "";
        byte buffer[] = new byte[8192];
        int len;
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            in = new FileInputStream(file);
            while ((len = in.read(buffer)) != -1) {
                md5.update(buffer, 0, len);
            }
            byte[] bytes = md5.digest();
            for (byte b : bytes) {
                String temp = Integer.toHexString(b & 0xff);
                if (temp.length() == 1) {
                    temp = "0" + temp;
                }
                result += temp;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(null!=in){
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    /**
     * 字符串获取md5值
     * @param str 具体的内容
     * @return 返回的数据内容
     */
    public static String md5String(String str){
        StringBuffer sb = null;
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            //将字符串转换为字节数组
            byte[] oldBytes=  str.getBytes(StandardCharsets.UTF_8);
            //对字节数组进行加密
            byte[] md5Bytes = messageDigest.digest(oldBytes);
            sb = new StringBuffer();
            for (byte b : md5Bytes) {
                int val = ((int) b) & 0xff;//与一个16进制的数值进行与运算
                if (val < 10) {
                    sb.append(0);
                }
                sb.append(Integer.toHexString(val));//转换为16进制
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        if (sb != null) {
            return sb.toString();
        }
        return null;
    }
}
