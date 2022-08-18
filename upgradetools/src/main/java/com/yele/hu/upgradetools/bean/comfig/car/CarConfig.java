package com.yele.hu.upgradetools.bean.comfig.car;

public class CarConfig {

    /**
     * 当前蓝牙的SN
     */
    public String SN;
    /**
     * 车型名称
     */
    public String typeName;

    public CarConfig(String SN, String typeName) {
        this.SN = SN;
        this.typeName = typeName;
    }
}
