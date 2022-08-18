package com.yele.hu.upgradetools.policy.event;

public class BleServiceStatus {

    // 建立蓝牙连接请求
    public static final int BLUE_SERVER_CONNECT = 1;

    // 断开蓝牙连接请求 需要先扫描后连接
    public static final int BLUE_SERVER_SCAN_CONNECT = 4;

    // 蓝牙开关已经被关闭
    public static final int BLUE_SERVER_STOP = 5;

    //蓝牙连接已断开
    public static final int BLUE_CANCEL_CONNECT = 2;

    //蓝牙设备已连接
    public static final int BLUE_SERVER_CONNECTED = 3;

    public int code = -1;

    public Object obj;

    public BleServiceStatus(int code, Object obj) {
        this.code = code;
        this.obj = obj;
    }
}
