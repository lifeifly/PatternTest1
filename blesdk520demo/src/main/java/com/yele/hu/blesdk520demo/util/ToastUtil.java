package com.yele.hu.blesdk520demo.util;

import android.content.Context;
import android.widget.Toast;

/**
 * 吐司工具类
 */
public class ToastUtil {
    /**
     * 长显示Toast
     * @param context 上下文
     * @param str 显示内容的字符串
     */
    public static void showLong(Context context, String str) {
        Toast.makeText(context, str, Toast.LENGTH_LONG).show();
    }

    /**
     * 长显示Toast
     * @param context 上下文
     * @param resId 显示内容的字符串的资源ID
     */
    public static void showLong(Context context, int resId) {
        Toast.makeText(context, resId, Toast.LENGTH_LONG).show();
    }

    /**
     * 短显示Toast
     * @param context 上下文
     * @param str 显示内容的字符串
     */
    public static void showShort(Context context, String str) {
        Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
    }

    /**
     * 短显示Toast
     * @param context 上下文
     * @param resId 显示内容的字符串的资源ID
     */
    public static void showShort(Context context, int resId) {
        Toast.makeText(context, resId, Toast.LENGTH_SHORT).show();
    }
}
