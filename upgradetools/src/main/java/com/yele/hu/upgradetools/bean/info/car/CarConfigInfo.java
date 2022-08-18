package com.yele.hu.upgradetools.bean.info.car;

public class CarConfigInfo {

    /**
     * 当前车辆的密码
     * 车辆密码就用到的了一个地方：模式切换命令中用到了该密码（其他用的都是蓝牙密码）
     */
    public static String PWD = "OKAI_CAR";
    /**
     * 当前车辆允许的最大速度
     * 速度范围为0~63（单位：KM/h）
     */
    public int maxLimitSpeed = 0;
    /**
     * S档开关
     * 0：开启
     * 1：关闭
     */
    public int sMode = 0;

    /**
     * 当前车辆上报信息的时间间隔
     */
    public int reportInterval = 10;

    /**
     * 车辆的加速模式
     * 0：柔和模式
     * 1：运动模式
     */
    public int accMode = 0;

    /**
     * 待机-关机时间（0~1800）（默认为30）（单位：S）
     */
    public int standbyTime = 0;

    /**
     * 仪表的显示模式
     * 0：为YD
     * 1：为KM
     */
    public int ybShowMode = 0;
    /**
     * 所有的锁是否都锁
     * true:所有车都锁
     * false:所有车解锁
     */
    public static boolean allLock = false;


}
