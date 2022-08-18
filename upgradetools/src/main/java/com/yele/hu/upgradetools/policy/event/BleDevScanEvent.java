package com.yele.hu.upgradetools.policy.event;

public class BleDevScanEvent {

    // 请求扫描设备
    public static final int REQUEST_SCAN_DEV = 1;

    // 停止扫描设备
    public static final int REQUEST_STOP_SCAN_DEV = 2;

    // 发送目标设备
    public static final int RESULT_DISCOVER_AIM_DEV = 3;

    // 未发现目标设备
    public static final int RESULT_NOT_DISCOVER_AIM_DEV = 4;

    // 发送设备列表
    public static final int RESULT_DISCOVER_LIST_DEV = 5;

    public BleDevScanEvent(int code, Object obj) {
        this.code = code;
        this.obj = obj;
    }

    public int code = 0;

    public Object obj;

}
