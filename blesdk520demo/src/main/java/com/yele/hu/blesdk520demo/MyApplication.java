package com.yele.hu.blesdk520demo;

import android.app.Application;
import android.bluetooth.BluetoothDevice;

import com.yele.blesdklibrary.BleManage;
import com.yele.blesdklibrary.bean.OkaiBleDevice;


public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        BleManage.init(this,true);
    }

    /**
     * 是否扫描
     */
    public static boolean isScan = false;
    /**
     * 是否链接
     */
    public static boolean isCon = false;
    /**
     * 当前链接的设备
     */
    public static BluetoothDevice curDevice;
    public static OkaiBleDevice bleDevice;
}
