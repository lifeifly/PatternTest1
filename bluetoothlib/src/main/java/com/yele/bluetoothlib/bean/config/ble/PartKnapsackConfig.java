package com.yele.bluetoothlib.bean.config.ble;

public class PartKnapsackConfig {

    /**
     * 背包蓝牙名称
     */
    public String name;

    /**
     * 背包设备 SN
     */
    public String sn;

    /**
     * 0：断开当前连接
     * 1：连接设备，一次性连接模式
     * 2：连接设备，智能连接模式
     */
    public String connectState;

    public PartKnapsackConfig(String name, String sn, String connectState) {
        this.name = name;
        this.sn = sn;
        this.connectState = connectState;
    }
}
