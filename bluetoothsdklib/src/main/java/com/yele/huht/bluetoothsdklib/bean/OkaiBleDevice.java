package com.yele.huht.bluetoothsdklib.bean;

import android.bluetooth.BluetoothDevice;

public class OkaiBleDevice {

    public int id;

    public BluetoothDevice device;

    public String type;

    public String sn;

    @Override
    public String toString() {
        return "OkaiBleDevice{" +
                "id=" + id +
                ", device=" + device +
                ", type='" + type + '\'' +
                ", sn='" + sn + '\'' +
                '}';
    }
}
