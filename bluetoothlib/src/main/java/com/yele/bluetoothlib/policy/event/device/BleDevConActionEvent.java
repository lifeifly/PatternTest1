package com.yele.bluetoothlib.policy.event.device;

public class BleDevConActionEvent {

    // 请求连接设备
    public static final int CODE_CONNECT = 0;

    // 请求设备断开连接
    public static final int CODE_DISCONNECT = 1;

    public int code = -1;

    public Object obj;

    public BleDevConActionEvent(int code, Object obj) {
        this.code = code;
        this.obj = obj;
    }
}
