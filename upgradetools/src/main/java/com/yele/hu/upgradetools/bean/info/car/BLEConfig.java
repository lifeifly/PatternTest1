package com.yele.hu.upgradetools.bean.info.car;

public class BLEConfig {
    /**
     * 当前蓝牙的模式
     * 0：正常模式
     * 1：测试模式
     * 2：恢复出厂设置
     */
    public static int mode = 0;

    /**
     * 广播间隔(范围：0006~3200)（单位：* 0.625ms)(默认300）（0表示不修改）
     */
    public static int SPACE = 300;
    /**
     * 广播持续时间（范围：0006~3200）（单位：* 10ms)（0表示一直广播不超时）
     */
    public static int DELAY = 1200;
    /**
     * 最小连接间隔（范围：0006~3200）（单位：* 1.25ms)（默认：37.5ms = 30 * 1.25）（0表示不修改）
     */
    public static int MIN_SPACE = 30;
    /**
     * 最大时间间隔范围：0006~3200）（单位：* 1.25ms)（默认：125ms = 100 * 1.25）（0表示不修改）
     */
    public static int MAX_SPACE = 100;
    /**
     * 默认密码（在测试模式下，可以不需要改密码进行修改密码）（长度要求：4~20）
     */
    public static String PWD = "OKAIYLBT";
    /**
     * 需要修改的新密码
     */
    public static String NEW_PWD = "ES200BT";
    /**
     * 蓝牙广播的新名称
     */
    public static String NAME_NEW = "OKAI111";

}
