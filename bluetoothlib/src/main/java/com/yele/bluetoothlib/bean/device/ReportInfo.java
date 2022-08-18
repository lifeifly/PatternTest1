package com.yele.bluetoothlib.bean.device;

import com.yele.bluetoothlib.bean.config.ble.BluetoothConfig;
import com.yele.bluetoothlib.bean.config.ble.CarConfig;
import com.yele.bluetoothlib.bean.config.ble.DesignConfig;
import com.yele.bluetoothlib.bean.config.ble.VersionInfo;

public class ReportInfo {

    // 上报上来的蓝牙配置信息
    public BluetoothConfig bluetoothConfig ;

    // 上报上来的蓝牙设计信息
    public DesignConfig designConfig;

    // 车辆的配置信息
    public CarConfig carConfig;

    // 当前车辆的模式
    public int mode;

    // 版本信息
    public VersionInfo versionInfo;
}
