package com.yele.hu.upgradetools.bean.comfig.car;

public class FactoryConfig {
    // 车辆清楚标志 0：清楚车辆数据，1：不清楚数据
    public int carFlag;
    // 发船模式 0：切换发船模式；1：不切换发船模式
    public int outFlag;
    // 读取配置标志 0：不读取配置; 1：读取配置（有 OKFCG 应答）
    public int readFlag;

    public FactoryConfig(int carFlag, int outFlag, int readFlag) {
        this.carFlag = carFlag;
        this.outFlag = outFlag;
        this.readFlag = readFlag;
    }
}
