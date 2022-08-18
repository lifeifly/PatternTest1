package com.yele.blesdklibrary.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateUtils {
    /**
     * 获取当前的系统时间
     * @return 返回当前的时间戳
     */
    public static long getTimeStamp() {
        return System.currentTimeMillis();
    }
    /**
     * 获取时间戳，日期只到天
     * @param date 日期的字符串
     * @return 返回对应的时间戳
     */
    public static long getTimeStampTillDay(String date) {
        return changeTimeStampByStyle(date, "yyyy-MM-dd");
    }

    /**
     * 将日期字符串格式根据输入的日期格式转换成时间戳
     * @param timeStr 需要转换的日期字符串
     * @param style 输入的日期的格式
     * @return 返回对应的时间戳
     */
    public static long changeTimeStampByStyle(String timeStr, String style){
        SimpleDateFormat df = new SimpleDateFormat(style, Locale.US);
        Date date = null;
        try {
            date = df.parse(timeStr);
        } catch (ParseException e) {
//            e.printStackTrace();
        }
        if (date == null) {
            return 0;
        }
        return date.getTime();
    }

    /**
     * 将时间戳转换成具体可以显示的字符串（当前只转换到日）
     * @param timestamp 需要转换的时间戳
     * @return 转换之后的字符串
     */
    public static String changeTimeStrToDay(long timestamp) {
        return changeTimeStrByStyle(timestamp,"yyyy-MM-dd");
    }

    /**
     * 根据指定Style将时间戳转换成时间格式的字符串
     * @param timeStamp 时间戳
     * @param style 指定类型的时间格式
     * @return
     */
    public static String changeTimeStrByStyle(long timeStamp, String style){
        SimpleDateFormat df = new SimpleDateFormat(style, Locale.US);
        Date date = new Date(timeStamp);
        return df.format(date);
    }

    /**
     * 根据指定Style将时间戳转换成时间格式的字符串
     * @param style 指定类型的时间格式
     * @return
     */
    public static String changeTimeStrByStyle(String timeZone, String style){
        TimeZone zone = TimeZone.getTimeZone(timeZone);
        Calendar calendar = Calendar.getInstance(zone);

        SimpleDateFormat df = new SimpleDateFormat(style, Locale.US);
        df.setTimeZone(zone);
        Date date = new Date(calendar.getTimeInMillis());
        return df.format(date);
    }

    /**
     * 根据提供的日期格式获取当前的日期时间
     * @param style 当前的日期格式
     * @return 对应的日期
     */
    public static String getTimeStrByStyle(String style) {
        SimpleDateFormat df = new SimpleDateFormat(style, Locale.US);
        Date date = new Date(System.currentTimeMillis());
        return df.format(date);
    }

    /**
     * 根据提供的日期格式获取当前的日期时间
     * @return 对应的日期
     */
    public static long getDayTimestamp() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        return cal.getTimeInMillis();
    }
}
