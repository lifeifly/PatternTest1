package com.yele.hu.upgradetools.bean.comfig.car;

public class RideConfig {

    /**
     * 当前车辆允许的最大速度
     * 速度范围为0~63（单位：KM/h）
     */
    public int MAX_SPEED = 0;

    /**
     * 是否开启S档开关
     * 1-开启S档
     * 0-关闭S档
     */
    public int hasGearOpen = 0;


    /**
     * 车辆的加速模式
     * 0：柔和模式
     * 1：运动模式
     */
    public int ADD_MODE = 0;


    /**
     * 仪表的显示模式
     * 0：为YD
     * 1：为KM
     */
    public int YB_SHOW_MODE = 0;

    /**
     * 当前车辆上报信息的时间间隔
     * 默认为100ms，最大9999ms,0表示不上报
     */
    public int SPACE = 10;
    

    
    /**
     * 待机-关机时间（0~1800）（默认为30）（单位：S）
     */
    public int WAIT_TIME = 0;


    public RideConfig(int hasGearOpen, int ADD_MODE, int YB_SHOW_MODE, int SPACE, int WAIT_TIME) {
        this.hasGearOpen = hasGearOpen;
        this.ADD_MODE = ADD_MODE;
        this.YB_SHOW_MODE = YB_SHOW_MODE;
        this.SPACE = SPACE;
        this.WAIT_TIME = WAIT_TIME;
    }
}
