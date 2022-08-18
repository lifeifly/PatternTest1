package com.yele.bluetoothlib.bean.config;

public class LBSConfig {
    // 海拔
    public long altitude;
    // 维度
    public double latitude;
    // 经度
    public double longitude;
    // 当前所在地
    public String address;

    public LBSConfig(long altitude, double latitude, double longitude, String address) {
        this.altitude = altitude;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
    }
}
