package com.yele.hu.blesdk520demo.event;

public class BleDevRequestConEvent {
    // 请求连接
    public static final int CODE_REQUEST_CONNECT = 0;
    // 请求断开连接
    public static final int CODE_REQUEST_DISCONNECT = 1;
    // 连接成功
    public static final int CODE_RESULT_CONNECT = 2;
    // 连接失败
    public static final int CODE_RESULT_CONNECT_FAIL = 3;
    // 断开连接成功
    public static final int CODE_RESULT_DISCONNECT = 4;
    // 断开连接失败
    public static final int CODE_RESULT_DISCONNECT_FAIL = 5;

    public int code = -1;

    public Object obj;

    public BleDevRequestConEvent(int code, Object obj) {
        this.code = code;
        this.obj = obj;
    }
}
