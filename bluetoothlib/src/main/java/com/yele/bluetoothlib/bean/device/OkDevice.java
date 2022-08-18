package com.yele.bluetoothlib.bean.device;

import android.bluetooth.BluetoothDevice;

public class OkDevice {
    // 当前的蓝牙设备
    public BluetoothDevice device;
    // 具体的蓝牙型号
    public String type;
    // 当前设备的SN
    public String sn;
    // 当前车辆的类型：0：车辆；1：头盔;2:背包
    public int model;
    // 设备是否已经连接成功
    public boolean isConnected = false;

    public int rssi;

    public byte[] rawBytes;

    public void setType(String type) {
        this.type = type;
        if (type == null) {
            this.model = 0;
            return;
        }
        if (type.equals("SH010")) {
            model = 1;
        }else if(type.equals("SP010")){
            model = 2;
        }else{
            model = 0;
        }
    }
}
