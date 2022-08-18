package com.yele.hu.upgradetools.util;

/**
 * Copyright (C),2020-2021,杭州野乐科技有限公司
 * FileName: PartColorUtils
 *
 * @Author: Chenxc
 * @Date: 2021/8/6 23:40
 * @Description: 配件颜色转换工具类
 * History:
 * <author> <time><version><desc>
 */
public class PartColorUtils {
    /**
     * 将颜色的前两位进行转换
     * @param color 设置的颜色
     * @return 转换后的颜色
     */
    public static String toCorrectColor(String color){
        if (color == null || color.length() < 4) {
            return null;
        }
        char[] inColors = color.toCharArray();
        char a = inColors[0];
        inColors[0] = inColors[2];
        inColors[2] = a;
        a = inColors[1];
        inColors[1] = inColors[3];
        inColors[3] = a;
        return new String(inColors);
    }
}
