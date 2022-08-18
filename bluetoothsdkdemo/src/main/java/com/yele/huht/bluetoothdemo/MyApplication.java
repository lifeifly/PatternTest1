package com.yele.huht.bluetoothdemo;

import android.app.Application;

import com.yele.huht.bluetoothsdklib.BleManage;
import com.yele.huht.bluetoothsdklib.bean.OkaiBleDevice;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        BleManage.init(this);   // sdk初始化
    }

    // 是否正在扫描
    public static boolean isScan = false;
    // 是否正在连接或连接中
    public static boolean isCon = false;
    // 连接设备的信息类
    public static OkaiBleDevice curDevice;
}
