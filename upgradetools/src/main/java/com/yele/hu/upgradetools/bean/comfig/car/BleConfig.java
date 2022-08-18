package com.yele.hu.upgradetools.bean.comfig.car;

public class BleConfig {

    /**
     * 默认密码（在测试模式下，可以不需要改密码进行修改密码）（长度要求：4~20）
     * OKAIYLBT
     */
    public String PWD;

    /**
     * 仪表的SN号
     */
    public String SN;

    /**
     * 广播间隔(范围：0006~3200)（单位：* 0.625ms)(默认300）（0表示不修改）
     */
    public int SPACE;

    /**
     * 广播持续时间（范围：0006~3200）（单位：* 10ms)（0表示一直广播不超时）
     */
    public int DELAY;

    /**
     * 最小连接间隔（范围：0006~3200）（单位：* 1.25ms)（默认：37.5ms = 30 * 1.25）（0表示不修改）
     */
    public int MIN_SPACE;

    /**
     * 最大时间间隔范围：0006~3200）（单位：* 1.25ms)（默认：125ms = 100 * 1.25）（0表示不修改）
     */
    public int MAX_SPACE;

    public BleConfig(String PWD, String SN, int SPACE, int DELAY, int MIN_SPACE, int MAX_SPACE) {
        this.PWD = PWD;
        this.SN = SN;
        this.SPACE = SPACE;
        this.DELAY = DELAY;
        this.MIN_SPACE = MIN_SPACE;
        this.MAX_SPACE = MAX_SPACE;
    }
}
