package com.yele.hu.upgradetools.bean.comfig.car;

public class PartConnectConfig {

    /**
     * 头盔背包连接指令ID
     * 0：断开当前连接
     * 1：连接设备，一次性连接模式
     * 2：连接设备，智能连接模式
     */
    public String connectState;

    public String sn;

    public String name;

    public PartConnectConfig(String connectStat, String sn, String name) {
        this.connectState = connectStat;
        this.sn = sn;
        this.name = name;
    }
}
