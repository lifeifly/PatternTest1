package com.yele.blesdklibrary.data;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;

public class BindData {


    public static int CMD_NO = 0X0001;

    public static BluetoothDevice bleDevice;    // 当前连接的设备
    public static BluetoothGatt bleDevGatt;     // 当前连接的设备的通讯类

    public static final int CON_ACTION_NONE = 0;        // 连接动作——无
    public static final int CON_ACTION_CONNECT = 1;     // 连接动作——连接蓝牙
    public static final int CON_ACTION_DISCONNECT = 2;  // 连接动作——断开连接
    public static int conAction = CON_ACTION_NONE;

}
