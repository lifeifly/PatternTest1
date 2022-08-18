package com.yele.bluetoothlib.bean.config;

public class FactoryConfig {
    // 车辆清楚标志 0：清楚车辆数据，1：不清楚数据
    public int carFlag;
    // 发船模式 0：切换发船模式；1：不切换发船模式
    public int outFlag;

    public FactoryConfig(int carFlag, int outFlag) {
        this.carFlag = carFlag;
        this.outFlag = outFlag;
    }
}
