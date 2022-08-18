package com.yele.huht.bluetoothsdklib.bean;

/**
 * 当前仪表的配置信息
 */
public class YBConfig {
    /**
     * 仪表的SN号
     */
    public static String SN = "00000000000000";
    /**
     * 仪表的显示模式
     * 0：为YD
     * 1：为KM
     */
    public static int YB_SHOW_MODE = 0;
    /**
     * 软件版本信息
     */
    public static String softVersion;
    /**
     * 硬件版本信息
     */
    public static String wareVersion;

    /**
     * 自检模式
     * 1：开始自检模式
     * 0：退出自检模式
     */
    public static int hasStartCheck = 0;

    /**
     * 仪表自检部位
     * 0：速度区
     * 1：电量区
     * 2：功能区
     * 3：全关
     */
    public static int checkParts = 0;

    /**
     * 自检结果
     * 如：转刹把就上报当前霍尔值，电机就上报当前速度等
     */
    public static String checkResult;

    /**
     * 蓝牙缩写
     * 广播包中的型号缩写，用于区分不同车型
     * 0：S052T
     * 1：S521T
     * 2：S522T
     */
    public static int bleModel;

    /**
     * 充电MOS管状态
     */
    public static int chargeMos = 0;

    /**
     * 放电MOS管状态
     */
    public static int dischargeMos = 0;

    /**
     * 电池健康度（0~100.0）（单位：0.1%）
     */
    public static int soh = 0;

    /**
     * 电芯最高温度(±0 - 255)(单位：℃）
     */
    public static int eleCoreHigh = 0;

    /**
     * 电芯最低温度(±0 - 255)(单位：℃）
     */
    public static int eleCoreLow = 0;

    /**
     * MOS管温度(±0 - 255)(单位：℃）
     */
    public static int mosTemp = 0;

    /**
     * 其他温度(±0 - 255)(单位：℃）
     */
    public static int otherTemp = 0;

    /**
     * 电池循环次数
     */
    public static int loopTimes = 0;

    /**
     * 加速转把AD值（0-10000）
     */
    public static int accelerateAD = 0;
    /**
     * 刹把AD（0-10000）
     */
    public static int brakeADLeft = 0;

    public static int brakeADRight = 0;

    /**
     * 报警器功能开关状态
     */
    public static int warnSwitch = 0;

    /**
     * 定时上报序号
     * 0：普通数据定时上报
     * 1：测试数据定时上报
     */
    public static int reportMode = 0;

    /**
     * 蓝牙开关(目前仅ES500B有此功能)
     * 0：开启蓝牙
     * 1：关闭蓝牙
     */
    public static int bleSwitch = 0;

}
