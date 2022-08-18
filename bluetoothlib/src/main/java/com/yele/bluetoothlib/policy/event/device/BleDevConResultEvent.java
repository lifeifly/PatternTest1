package com.yele.bluetoothlib.policy.event.device;

import android.bluetooth.BluetoothDevice;

public class BleDevConResultEvent {
    // 设备已经连接
    public static final int CODE_RESULT_CONNECT = 2;
    // 连接设备失败
    public static final int CODE_RESULT_CONNECT_FAIL = 3;
    // 设备断开连接
    public static final int CODE_RESULT_DISCONNECT = 4;
    // 设备断开连接失败
    public static final int CODE_RESULT_DISCONNECT_FAIL = 5;

    // 结果码
    public int code;
    // 状态设备
    public BluetoothDevice device;

    public BleDevConResultEvent(int code, BluetoothDevice device) {
        this.code = code;
        this.device = device;
    }
}
