package com.yele.bluetoothlib.bean.config;

public class WeatherConfig {

    // 今日天气 0：晴；1：阴天；2：雨天；3：雾霾天；4：雷阵雨
    public int now;
    // 延后第一天
    public int next1;
    // 延后第一天
    public int next2;
    // 延后第一天
    public int next3;
    // 延后第一天
    public int next4;
    // 延后第一天
    public int next5;
    // 今日温度
    public int temp;
    // 最低温度
    public int lowTemp;
    // 最高温度
    public int highTemp;

    public WeatherConfig(int now, int next1, int next2,
                         int next3, int next4, int next5, int temp, int lowTemp, int highTemp) {
        this.now = now;
        this.next1 = next1;
        this.next2 = next2;
        this.next3 = next3;
        this.next4 = next4;
        this.next5 = next5;
        this.temp = temp;
        this.lowTemp = lowTemp;
        this.highTemp = highTemp;
    }
}
