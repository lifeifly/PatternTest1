package com.yele.hu.upgradetools.bean.comfig.car;

public class LedConfig {

    /**
     * <LED 序列>：表示要控制的 LED 类型。默认为 0。
     *     0：大灯；
     *     1：前灯模式（ES800 车型）；
     */
    public int type;

    /**
     * <LED 状态>：要控制的 LED 状态。
     *    当“LED 序列”是 0 大灯控制时：
     *        0：关闭。
     *        1：打开。
     *    当“LED 序列”是 1 前灯模式时：
     *        0：标准模式。
     *        1：加强模式。
     */
    public int state;

    public LedConfig(int type, int state) {
        this.type = type;
        this.state = state;
    }
}
