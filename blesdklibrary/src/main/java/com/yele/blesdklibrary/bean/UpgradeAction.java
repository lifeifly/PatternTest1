package com.yele.blesdklibrary.bean;

public class UpgradeAction {

    public static final int TYPE_CONTROL = 0;  // 控制器
    public static final int TYPE_MCU = 1;    // MCU
    public static final int TYPE_BMS = 2;    // BMS
    public static final int TYPE_ELECTRIC = 2;     // 电池

    public int type;

    public static final int ACTION_START = 0;     // 开始升级
    public static final int ACTION_STOP = 1;      // 开始更新

    public int action;

    public String path;

}
