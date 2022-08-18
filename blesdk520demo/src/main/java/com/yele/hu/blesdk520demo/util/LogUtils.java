package com.yele.hu.blesdk520demo.util;

import android.util.Log;

/**
 * 调试打印信息工具类
 */
public class LogUtils {

    private static final int VERB = 0;  // 打印等级——基本信息
    private static final int DEBUG = 1; // 打印等级——调试信息
    private static final int INFO = 2;  // 打印等级——基础信息
    private static final int WARN = 3;  // 打印等级——警告信息
    private static final int ERROR = 4; // 打印等级——错误信息

    private static int base = VERB;

    private static boolean isPrint = true;

    /**
     * 调试打印基本信息
     * @param tag 标签
     * @param str 输出内容
     */
    public static void v(String tag, String str) {
        if (VERB >= base) {
            if (isPrint) {
                Log.v(tag, str);
            }else{
                System.out.println("V tag:" + tag + "\n content:" + str);
            }
        }
    }

    /**
     * 调试打印调试信息
     * @param tag 标签
     * @param str 输出内容
     */
    public static void d(String tag, String str) {
        if (DEBUG >= base) {
            if (isPrint) {
                Log.d(tag, str);
            }else{
                System.out.println("D tag:" + tag + "\n content:" + str);
            }
        }
    }

    /**
     * 调试打印普通内容
     * @param tag 标签
     * @param str 输出内容
     */
    public static void i(String tag, String str) {
        if (INFO >= base) {
            if (isPrint) {
                Log.i(tag, str);
            }else{
                System.out.println("I tag:" + tag + "\n content:" + str);
            }
        }
    }

    /**
     * 调试打印警告信息
     * @param tag 标签
     * @param str 输出内容
     */
    public static void w(String tag, String str) {
        if (WARN >= base) {
            if (isPrint) {
                Log.w(tag, str);
            }else{
                System.out.println("W tag:" + tag + "\n content:" + str);
            }
        }
    }

    /**
     * 调试打印错误信息
     * @param tag 标签
     * @param str 具体输出内容
     */
    public static void e(String tag, String str) {
        if (ERROR >= base) {
            if (isPrint) {
                Log.e(tag, str);
            }else{
                System.out.println("E tag:" + tag + "\n content:" + str);
            }
        }
    }

}
