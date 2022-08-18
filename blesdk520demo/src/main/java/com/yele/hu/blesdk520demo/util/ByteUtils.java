package com.yele.hu.blesdk520demo.util;

public class ByteUtils {

    public final static char[] HEX_SMALL_TABLE = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'
            , 'a', 'b', 'c', 'd', 'e', 'f'};

    /**
     * 将byte类型切换为无符号整形
     *
     * @param data 需要转换的数据
     * @return 转换之后的无符号整形
     */
    public static int changeUnit16Int(byte data) {
        if (data < 0) {
            return 256 + data;
        } else {
            return data;
        }
    }

    /***
     * 将无符号整形转换成byte类型
     * @param data 需要转换的整形
     * @return 返回的字节
     */
    public static byte intToUnitByte(int data) {
        if (data < 128) {
            return (byte) data;
        } else {
            return (byte) (data - 256);
        }
    }

    /**
     * 字符串转换成byte数组
     *
     * @param str 需要转换的字符串
     * @return 转换之后的数组
     */
    public static byte[] strToBytes(String str) {
        char[] chars = str.toCharArray();
        byte[] buff = new byte[chars.length / 2];
        for (int i = 0; i < chars.length; i += 2) {
            buff[i / 2] = (byte) (charToInt(chars[i]) * 16 + charToInt(chars[i + 1]));
        }
        return buff;
    }

    public static byte strToByte(String str){
        byte buf;
        char[] chars = str.toCharArray();
        buf = (byte) (charToInt(chars[0]) * 16 + charToInt(chars[1]));
        return buf;
    }

    /**
     * 字符串转换成byte数组（大端模式）
     * @param str 需要转换的字符串
     * @return 转换之后的数组
     */
    public static byte[] strToBytesByBig(String str) {
        char[] chars = str.toCharArray();
        byte[] buff = new byte[chars.length / 2];
        for (int i = chars.length - 1; i >= 0; i -= 2) {
            buff[buff .length - i / 2 - 1] = (byte) (charToInt(chars[i-1]) * 16 + charToInt(chars[i]));
        }
        return buff;
    }


    /**
     * char字符转换成INT 类型
     *
     * @param ch 需要转换的char
     * @return 转换之后的byte
     */
    public static int charToInt(char ch) {
        int data = ch;
        if (ch >= 48 && ch <= 57) {
            data -= 48;
        } else if (ch >= 65 && ch <= 70) {
            data -= 55;
        } else if (ch >= 97 && ch <= 102) {
            data -= 87;
        }
        return data;
    }

    /**
     * 将byte数组转换成对应的string显示类型(大段模式)
     *
     * @param bytes 需要转换的byte数组
     * @return 转换之后的string显示类型
     */
    public static String bytesToStringByBig(byte[] bytes) {
        StringBuffer buff = new StringBuffer();
        for (byte by : bytes) {
            buff.append(byteToString(by));
        }
        return buff.toString();
    }

    /**
     * 将byte数组转换成对应的string显示类型(小端模式)
     *
     * @param bytes 需要转换的byte数组
     * @return 转换之后的string显示类型
     */
    public static String bytesToStringBySmall(byte[] bytes) {
        StringBuffer buff = new StringBuffer();
        for (int i = bytes.length - 1; i >= 0; i--) {
            buff.append(byteToString(bytes[i]));
        }
        return buff.toString();
    }

    /**
     * byte转换成char类型
     *
     * @param by 需要转换的字节(byte)
     * @return 显示的char类型
     */
    public static String byteToString(byte by) {
        int code;
        if (by < 0) {
            code = 256 + by;
        } else {
            code = by;
        }
        return intToString(code);
    }

    /**
     * int类型转换成字符串类型
     *
     * @param code 需要转换的数据
     * @return 转换之后的字符串数据
     */
    public static String intToString(int code) {
        int x = code / 16 % 16;
        int y = code % 16;
        return "" + HEX_SMALL_TABLE[x] + HEX_SMALL_TABLE[y];
    }

    /**
     * 将bytes数组转换成INT类型，根据小端模式转换
     *
     * @param data 需要转换的数据
     * @param len  需要转换的长度
     * @return
     */
    public static int bytesToIntBySmall(byte[] data, int len) {
        int result = 0;
        for (int i = 0; i < len; i++) {
            int mid = changeUnit16Int(data[i]);
            mid = (int) (mid * Math.pow(256, i));
            result += mid;
        }
        return result;
    }

    /**
     * 将bytes数组转换成INT类型，根据小端模式转换
     *
     * @param data 需要转换的数据
     * @param len  需要转换的长度
     * @return
     */
    public static int bytesToIntByBig(byte[] data, int len) {
        int result = 0;
        for (int i = len - 1; i >= 0; i--) {
            int mid = changeUnit16Int(data[i]);
            mid = (int) (mid * Math.pow(256, len - 1 - i));
            result += mid;
        }
        return result;
    }

    public static long bytesToLongBySmall(byte[] data, int len) {
        long result = 0;
        for (int i = 0; i < len; i++) {
            long mid = changeUnit16Int(data[i]);
            mid = (long) (mid * Math.pow(256, i));
            result += mid;
        }
        return result;
    }

    public static long bytesToLongByBig(byte[] data, int len) {
        long result = 0;
        for (int i = len - 1; i >= 0; i--) {
            long mid = changeUnit16Int(data[i]);
            mid = (long) (mid * Math.pow(256, len - 1 - i));
            result += mid;
        }
        return result;
    }

    public static byte[] longToBytesBySmall(long data, int len) {
        byte[] buff = new byte[len];
        for (int i = 0; i < len; i++) {
            int result = (int) (data % 256);
            buff[i] = intToUnitByte(result);
            data = data / 256;
        }
        return buff;
    }

    public static byte[] longToBytesByBig(long data, int len) {
        byte[] buff = new byte[len];
        for (int i = len - 1 ; i >= 0; i--) {
            int result = (int) (data % 256);
            buff[i] = intToUnitByte(result);
            data = data / 256;
        }
        return buff;
    }
}
