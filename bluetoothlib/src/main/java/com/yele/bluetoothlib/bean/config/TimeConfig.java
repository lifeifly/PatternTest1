package com.yele.bluetoothlib.bean.config;

import com.yele.baseapp.utils.DateUtils;

public class TimeConfig {

    // 当前年
    public int year;
    // 当前月
    public int month;
    // 当前天
    public int day;
    // 当前小时
    public int hour;
    // 当前分钟
    public int min;
    // 当前妙
    public int sec;
    // 当前星期
    public int week;

    public TimeConfig() {
        long time = System.currentTimeMillis();
        String str = DateUtils.changeTimeStrByStyle(time, "yyyy-MM-dd-hh-mm-ss-E");
        System.out.println(str);
        String[] da = str.split("-");
        this.year = Integer.valueOf(da[0]);
        this.month =Integer.valueOf(da[1]);
        this.day = Integer.valueOf(da[2]);
        this.hour = Integer.valueOf(da[3]);
        this.min = Integer.valueOf(da[4]);
        this.sec = Integer.valueOf(da[5]);
        this.week = 0;
    }
}
