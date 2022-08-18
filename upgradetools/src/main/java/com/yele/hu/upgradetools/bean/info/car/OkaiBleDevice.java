package com.yele.hu.upgradetools.bean.info.car;

import android.bluetooth.BluetoothDevice;

public class OkaiBleDevice {

    public int id;

    public BluetoothDevice device;

    public String type;

    public String sn;

    public int rssi;

    // 当前车辆的类型：0：车辆；1：头盔;2:背包
    public int model;

    // 设备是否已经连接成功
    public boolean isConnected = false;


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
