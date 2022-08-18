package com.yele.huht.bluetoothsdklib.bean;

/**
 * 所有配置信息类
 */
public class InitInfo {
    /**
     * 当前蓝牙的模式
     * 0：正常模式
     * 1：测试模式
     * 2：恢复出厂设置
     */
    public int mode;
    /**
     * 仪表SN
     */
    public String SN;
    /**
     * 广播间隔(范围：0006~3200)（单位：* 0.625ms)(默认300）（0表示不修改）
     */
    public int broadcastSpace;
    /**
     * 广播持续时间（范围：0006~3200）（单位：* 10ms)（0表示一直广播不超时）
     */
    public int broadcastTime;
    /**
     * 最小连接间隔（范围：0006~3200）（单位：* 1.25ms)（默认：37.5ms = 30 * 1.25）（0表示不修改）
     */
    public int minConnectSpace;
    /**
     * 最大时间间隔范围：0006~3200）（单位：* 1.25ms)（默认：125ms = 100 * 1.25）（0表示不修改）
     */
    public int maxConnectSpace;
    /**
     * 默认密码（在测试模式下，可以不需要改密码进行修改密码）（长度要求：4~20）
     */
    public String pwd;
    /**
     * 蓝牙广播的新名称
     */
    public String NameNew;
    /**
     * 当前车辆的SN
     */
    public String carSn;
    /**
     * 车型名称
     */
    public String typeName;
    /**
     * 当前车辆上报信息的时间间隔
     */
    public int reportSpace;
    /**
     * 当前车辆允许的最大速度
     * 速度范围为0~63（单位：KM/h）
     */
    public int MaxSpeed;
    /**
     * 车辆的加速模式
     * 0：柔和模式
     * 1：运动模式
     */
    public int addMode;
    /**
     * 待机-关机时间（0~1800）（默认为30）（单位：S）
     */
    public int standbyTime;
    /**
     * 仪表的显示模式
     * 0：为YD
     * 1：为KM
     */
    public int showMode;
    /**
     * 软件版本信息
     */
    public String softVersion;
    /**
     * 硬件版本信息
     */
    public String wareVersion;

    /**
     * 控制器软件版本
     */
    public String controlSV;
    /**
     * 控制器硬件版本
     */
    public String controlWV;
    /**
     * BMS软件版本
     */
    public String bmsSV;
    /**
     * BMS硬件版本
     */
    public String bmsWV;


    @Override
    public String toString() {
        return "InitInfo{" +
                "mode=" + mode +
                ", SN='" + SN + '\'' +
                ", broadcastSpace=" + broadcastSpace +
                ", broadcastTime=" + broadcastTime +
                ", minConnectSpace=" + minConnectSpace +
                ", maxConnectSpace=" + maxConnectSpace +
                ", pwd='" + pwd + '\'' +
                ", NameNew='" + NameNew + '\'' +
                ", carSn='" + carSn + '\'' +
                ", typeName='" + typeName + '\'' +
                ", reportSpace=" + reportSpace +
                ", MaxSpeed=" + MaxSpeed +
                ", addMode=" + addMode +
                ", standbyTime=" + standbyTime +
                ", showMode=" + showMode +
                ", softVersion='" + softVersion + '\'' +
                ", wareVersion='" + wareVersion + '\'' +
                ", controlSV='" + controlSV + '\'' +
                ", controlWV='" + controlWV + '\'' +
                ", bmsSV='" + bmsSV + '\'' +
                ", bmsWV='" + bmsWV + '\'' +
                '}';
    }
}
